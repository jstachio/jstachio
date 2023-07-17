package io.jstach.script;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Version Helper (vh) is to help manage versions on git tags, poms, and properties as
 * well as manage timestamps. It replaces a lot of functionality that the Maven release
 * plugin would do.
 *
 * It should be run in the project root like:
 *
 * <code>
 * bin/vh [COMMAND]
 * </code>
 *
 * @author agentgt
 *
 */
public final class VersionHelper {

	public static void main(String[] args) {
		try {
			run(List.of(args));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

	}

	static void run(List<String> _args) throws Exception {
		Command command = Command.parseCommand(Command.class, _args, 0);
		List<String> params = _args.stream().skip(1).toList();
		command.run(params);
	}

}

interface HelpSupport {

	String desc();

}

enum Command implements HelpSupport {

	RELEASE("Validates and prepares the enviroment for release by tagging based on version.properties") {
		@Override
		public void run(List<String> args) throws IOException {
			out.println("Validate Git");
			validateGit();
			var current = current();
			long timestamp = timestamp();
			var pom = pom();
			out.println("Validating POM version");
			validatePom(pom, current);
			out.println("Tagging");
			tag(current);
			out.println("Setting POM to release version and updating timestamp");
			pom(current, timestamp);
			out.println("Ready to release. Do not forget to push tags!");
			out.println("Now manually run:");
			out.println("mvn clean deploy -Duser.timezone=UTC -Ddeploy=release \\");
			out.println("&& git checkout . \\");
			out.println("&& git push --follow-tags");
		}
	},
	CURRENT("Prints the current version based on version.properties.") {
		@Override
		public void run(List<String> args) throws IOException {
			var version = current();
			out.println(version.print());
		}
	},
	VALIDATE("Validates that version.properties, git tag, and pom are specified correctly.") {
		@Override
		public void run(List<String> args) throws IOException {
			validate(current(), tag(), pom());
		}
	},
	PARSE("Parses a version: 'parse <VERSION>'") {
		@Override
		public void run(List<String> args) throws IOException {
			var a = Version.of(args.get(0));
			out.println(a.print());
		}
	},
	GET("Gets a version string from a source: 'get <SOURCE>'") {
		@Override
		public void run(List<String> args) throws IOException {
			VersionSource getCmd = Command.parseCommand(VersionSource.class, args, 0, this);
			switch (getCmd) {
				case CURRENT -> {
					var version = current();
					out.println(version.print());
				}
				case POM -> {
					var v = pom();
					out.println(v.label());
				}
				case TAG -> {
					var v = tag();
					out.println(v.label());
				}
			}

		}
	},
	SET("Sets a version string on a source: 'set <SOURCE>'") {
		@Override
		public void run(List<String> args) throws IOException {
			VersionSource setCmd = Command.parseCommand(VersionSource.class, args, 0, this);
			List<String> params = args.stream().skip(1).toList();
			Version v;
			long timestamp;
			if (params.isEmpty()) {
				v = current();
				timestamp = timestamp();
			}
			else {
				v = Version.of(params.get(0));
				timestamp = java.time.Instant.now().getEpochSecond();
			}
			switch (setCmd) {
				case CURRENT -> {
					current(v, timestamp);
				}
				case POM -> {
					pom(v, timestamp);
				}
				case TAG -> {
					tag(v);
				}
			}
		}

	};

	public static final String VERSION_PROPERTIES = "version.properties";

	private final String desc;

	Command(String desc) {
		this.desc = desc;
	}

	public String desc() {
		return this.desc;
	}

	public abstract void run(List<String> args) throws IOException;

	enum VersionSource implements HelpSupport {

		CURRENT(VERSION_PROPERTIES), POM("pom.xml"), TAG("git tag");

		private final String desc;

		VersionSource(String desc) {
			this.desc = desc;
		}

		public String desc() {
			return this.desc;
		}

	}

	static void validate(Version current, Version tag, Version pom) throws IOException {

		if (!tag.equals(current)) {
			throw new RuntimeException("version mismatch. current = " + current.print() + " tag = " + tag.print());
		}
		validatePom(pom, current);
	}

	static void validatePom(Version pom, Version current) {
		if (pom.compareTo(current) <= 0) {
			throw new RuntimeException("pom version is not greater than current version.properties. current = "
					+ current.print() + " pom = " + pom);
		}
		if (!pom.snapshot()) {
			throw new RuntimeException("pom should be a -SNAPSHOT version");
		}
	}

	static Version current() throws IOException {
		var props = new Properties();
		try (var r = Files.newBufferedReader(Path.of(VERSION_PROPERTIES))) {
			props.load(r);
		}
		String v = props.getProperty("version");
		return Version.of(v);
	}

	static void current(Version v, long timestamp) throws IOException {
		if (v.snapshot()) {
			throw new IllegalArgumentException("version.properties cannot have SNAPSHOT versions. version: "
					+ v.print(Version.PrintFlag.SNAPSHOT));
		}
		try (var br = Files.newBufferedWriter(Path.of(VERSION_PROPERTIES), StandardCharsets.ISO_8859_1)) {
			writeProperties(br, Map.entry("version", v.print()), Map.entry("timestamp", "" + timestamp));
			br.flush();
		}
	}

	static long timestamp() throws IOException {
		var props = new Properties();
		try (var r = Files.newBufferedReader(Path.of(VERSION_PROPERTIES))) {
			props.load(r);
		}
		String t = props.getProperty("timestamp");
		if (t == null) {
			return -1;
		}
		return Long.parseLong(t);
	}

	static Version pom() throws IOException {
		String command = "mvn help:evaluate -Dexpression=project.version -q -DforceStdout";
		String r = execute(command, 1).trim();
		return Version.of(r);
	}

	static void pom(Version current, long timestamp) throws IOException {
		run("mvn versions:set -DnewVersion=" + current.print(Version.PrintFlag.SNAPSHOT));
		updateTimestamp(timestamp);

	}

	static Version tag() throws IOException {
		String r = execute("git tag -l --sort=-v:refname", 1);
		var v = Version.of(r.trim());
		return v;
	}

	static void tag(Version version) throws IOException {
		if (version.snapshot()) {
			throw new IllegalArgumentException(
					"tag cannot have SNAPSHOT versions. version: " + version.print(Version.PrintFlag.SNAPSHOT));
		}
		String command = "git tag -a -m 'release " + version.print() + "'" + " v" + version.print();
		out.println("Executing " + command);
		String r = execute(command, -1);
		out.println(version);
		out.println(r);
	}

	static void validateGit() throws IOException {
		String command = "git status --porcelain=v1 2>/dev/null";
		String r = execute(command, -1);
		if (!r.trim().isBlank()) {
			throw new RuntimeException("Git has changed files: \n" + r);
		}
	}

	static String execute(String command) throws IOException {
		return execute(command, -1);
	}

	static void run(String command) throws IOException {
		int e = execute(command, System.out, System.err, -1);
		if (e != 0) {
			System.exit(e);
		}
	}

	static String execute(String command, int lines) throws IOException {
		StringBuilder out = new StringBuilder();
		StringBuilder err = new StringBuilder();
		int e = execute(command, out, err, lines);
		if (e == 0) {
			return out.toString();
		}
		else {
			throw new IOException("Failure executing command: " + command + ", exit: " + e + "\nstdout: "
					+ out.toString() + "\nstderr: " + err.toString());
		}
	}

	static int execute(String command, Appendable out, Appendable err, int lines) throws IOException {
		ProcessBuilder b = new ProcessBuilder();
		b.command("bash", "-c", command);

		Process process = b.start();

		BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		if (lines < 0) {
			lines = Integer.MAX_VALUE;
		}
		int e;
		try {
			e = process.waitFor();
			append(out, stdout, lines);
			append(err, stderr, -1);
		}
		catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e1);
		}
		return e;
	}

	private static void append(Appendable output, BufferedReader reader, int lines) throws IOException {
		String line;
		int i = 0;
		while ((line = reader.readLine()) != null) {
			if (i >= lines) {
				break;
			}
			output.append(line + "\n");
			i++;
		}
	}

	public static <E extends Enum<E> & HelpSupport> E parseCommand(Class<E> commandType, List<String> args, int index) {
		return parseCommand(commandType, args, index, null);
	}

	public static <E extends Enum<E> & HelpSupport, P extends Enum<P> & HelpSupport> E parseCommand(
			Class<E> commandType, List<String> args, int index, P parent) {
		if (args.size() < (index + 1)) {
			String message;
			if (parent != null) {
				message = "Missing subcommand for '" + parent.name().toLowerCase() + "'. pick: ";
			}
			else {
				message = "Missing command. pick: ";
			}
			message = message + printCommands(commandType);
			throw new RuntimeException(message);
		}
		String arg = args.get(index);

		try {
			if (arg.equalsIgnoreCase("help")) {
				out.println("");

				String message;
				if (parent != null) {
					out.println(parent.desc());
					out.println();
					message = "Sub Commands for '" + parent.name().toLowerCase() + "': ";
				}
				else {
					out.println("""
							Version Helper (vh) is to help manage versions on git tags, poms, and properties
							as well as manage timestamps. It replaces a lot of functionality that the
							Maven release plugin would do.

							It should be run in the project root like:

								bin/vh [COMMAND]

							""");
					message = "Commands: ";
				}
				out.println(message + printCommands(commandType));
				out.println(helpCommands(commandType));
				System.exit(1);
				throw new RuntimeException();
			}
			return Enum.valueOf(commandType, arg.toUpperCase());

		}
		catch (Exception exception) {
			String message = "Bad command. " + arg + " pick: " + printCommands(commandType);
			throw new RuntimeException(message);
		}
	}

	public static <E extends Enum<E>> String printCommands(Class<E> commandType) {
		return ""
				+ Stream.concat(Stream.of("help"), EnumSet.allOf(commandType).stream().map(e -> e.name().toLowerCase()))
						.toList();
	}

	public static <E extends Enum<E> & HelpSupport> String helpCommands(Class<E> commandType) {
		StringBuilder sb = new StringBuilder();
		for (var e : EnumSet.allOf(commandType)) {
			sb.append("\n\t");
			sb.append(e.name().toLowerCase()).append(" - ").append(e.desc());
		}
		sb.append("\n\t");
		sb.append("help - describe commands: '[<COMMAND>] help'");
		sb.append("\n");
		return sb.toString();
	}

	@SafeVarargs
	static void writeProperties(Appendable sb, Entry<String, String>... kvs) throws IOException {
		Map<String, String> m = new LinkedHashMap<>();
		for (var kv : kvs) {
			m.put(kv.getKey(), kv.getValue());
		}
		writeProperties(m, sb);
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	static void writeProperties(Map<String, String> map, Appendable sb) throws IOException {
		StringWriter sw = new StringWriter();
		new Properties() {
			public java.util.Enumeration keys() {
				return Collections.enumeration(map.keySet());
			}

			public java.util.Set entrySet() {
				return map.entrySet();
			};

			public Object get(Object key) {
				return map.get(key);
			};
		}.store(sw, null);
		LineNumberReader lr = new LineNumberReader(new StringReader(sw.toString()));

		String line;
		while ((line = lr.readLine()) != null) {
			if (!line.startsWith("#")) {
				sb.append(line).append("\n");
				out.append(line).append("\n");
			}
		}
	}

	static void updateTimestamp(long timestamp) throws IOException {
		try {
			Document doc;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			try (InputStream is = Files.newInputStream(Path.of("pom.xml"), StandardOpenOption.READ)) {

				doc = builder.parse(is);
				XPath xpath = XPathFactory.newInstance().newXPath();
				String path = "/project/properties/project.build.outputTimestamp";
				Node v = (Node) xpath.evaluate(path, doc, XPathConstants.NODE);
				v.setTextContent("" + timestamp);
			}
			try (OutputStream os = Files.newOutputStream(Path.of("pom.xml"), StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC)) {
				DOMSource domSource = new DOMSource(doc);
				StreamResult result = new StreamResult(os);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
			}
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

}

record Version(int major, int minor, int patch, boolean snapshot) implements Comparable<Version> {

	static final Pattern pattern = Pattern.compile("v?([0-9]+).([0-9]+).([0-9]+)(-SNAPSHOT)?");
	static final Comparator<Version> COMPARATOR = Comparator.comparingInt(Version::major)
			.thenComparingInt(Version::minor).thenComparingInt(Version::patch).thenComparing(Version::snapshot);

	static Version of(String s) {
		Matcher m = pattern.matcher(s);
		if (!m.matches()) {
			throw new IllegalArgumentException("bad version: " + s);
		}
		int major = Integer.parseInt(m.group(1));
		int minor = Integer.parseInt(m.group(2));
		int patch = Integer.parseInt(m.group(3));
		boolean snapshot = false;
		if (m.groupCount() > 3 && "-SNAPSHOT".equals(m.group(4))) {
			snapshot = true;
		}
		return new Version(major, minor, patch, snapshot);
	}

	enum PrintFlag {

		PREFIX, SNAPSHOT

	}

	public String print(Set<PrintFlag> flags) {
		return (flags.contains(PrintFlag.PREFIX) ? "v" : "") + major() + "." + minor() + "." + patch()
				+ (flags.contains(PrintFlag.SNAPSHOT) && snapshot ? "-SNAPSHOT" : "");
	}

	public String print() {
		return print(Set.of());
	}

	public String print(PrintFlag flag, PrintFlag... flags) {
		Set<PrintFlag> fs = EnumSet.of(flag, flags);
		return print(fs);
	}

	public String label() {
		return print(Set.of(PrintFlag.PREFIX, PrintFlag.SNAPSHOT));
	}

	public String tag() {
		return "v" + print();
	}

	public String toString() {
		return label();
	}

	void validate(Version b) {
		if (this.equals(b)) {
			out.println(b.print());
		}
		else {
			throw new IllegalArgumentException("version mismatch. a = " + this.label() + " b = " + b.label());
		}
	}

	@Override
	public int compareTo(Version o) {
		return COMPARATOR.compare(this, o);
	}

}
