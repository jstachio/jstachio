package io.jstach;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

enum Command {

	RELEASE() {
		@Override
		public void run(List<String> args) throws IOException {
			out.println("Validate Git");
			validateGit();
			var current = current();
			var pom = pom();
			out.println("Validating POM version");
			validatePom(pom, current);
			out.println("Tagging");
			tag(current);
			out.println("Setting POM to release version");
			pom(current);
			out.println("Ready to run 'mvn clean deploy -Pcentral'. Do not forget to push tags!");
		}
	},
	CURRENT() {
		@Override
		public void run(List<String> args) throws IOException {
			var version = current();
			out.println(version.print());
		}
	},
	VALIDATE() {
		@Override
		public void run(List<String> args) throws IOException {
			validate(current(), tag(), pom());
		}
	},
	PARSE() {
		@Override
		public void run(List<String> args) throws IOException {
			var a = Version.of(args.get(0));
			out.println(a.print());
		}
	},
	GET() {
		@Override
		public void run(List<String> args) throws IOException {
			GetCommand getCmd = Command.parseCommand(GetCommand.class, args, 0);
			switch (getCmd) {
				case CURRENT -> {
					var version = current();
					out.println(version.print());
				}
				case POM -> {
					var v = pom();
					out.println(v);
				}
				case TAG -> {
					var v = tag();
					out.println(v);
				}
			}

		}
	},
	SET() {
		@Override
		public void run(List<String> args) throws IOException {
			SetCommand setCmd = Command.parseCommand(SetCommand.class, args, 0);
			switch (setCmd) {
				case POM -> {
					var current = current();
					pom(current);
				}
				case TAG -> {
					var version = current();
					tag(version);
				}
			}
		}

	};

	public abstract void run(List<String> args) throws IOException;

	enum SetCommand {

		POM, TAG

	}

	enum GetCommand {

		CURRENT, POM, TAG

	}

	static void validate(Version current, Version tag, Version pom) throws IOException {

		if (!tag.equals(current)) {
			throw new RuntimeException("version mismatch. current = " + current.print() + " tag = " + tag.print());
		}
		validatePom(pom, current);
	}

	static void validatePom(Version pom, Version current) {
		if (pom.compareTo(current) <= 0) {
			throw new RuntimeException(
					"pom version is not greater than current. current = " + current.print() + " pom = " + pom);
		}
	}

	static Version current() throws IOException {
		var props = new Properties();
		props.load(Files.newBufferedReader(Path.of("version.properties")));
		String v = props.getProperty("version");
		return Version.of(v);
	}

	static Version pom() throws IOException {
		String command = "mvn help:evaluate -Dexpression=project.version -q -DforceStdout";
		String r = execute(command, 1).trim();
		return Version.of(r);
	}

	static void pom(Version current) throws IOException {
		run("mvn versions:set -DnewVersion=" + current.print());
	}

	static Version tag() throws IOException {
		String r = execute("git tag -l --sort=-v:refname", 1);
		var v = Version.of(r.trim());
		return v;
	}

	static void tag(Version version) throws IOException {
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

	public static <E extends Enum<E>> E parseCommand(Class<E> commandType, List<String> args, int index) {
		if (args.size() < (index + 1)) {
			String message = "Missing command. pick: " + printCommands(commandType);
			throw new RuntimeException(message);
		}
		String arg = args.get(index);

		try {
			if (arg.equalsIgnoreCase("help")) {
				out.println("Commands: " + printCommands(commandType));
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
		return "" + EnumSet.allOf(commandType).stream().map(e -> e.name().toLowerCase()).toList();
	}

}

record Version(int major, int minor, int patch) implements Comparable<Version> {

	static final Pattern pattern = Pattern.compile("v?([0-9]+).([0-9]+).([0-9]+)(-SNAPSHOT)?");
	static final Comparator<Version> COMPARATOR = Comparator.comparingInt(Version::major)
			.thenComparingInt(Version::minor).thenComparing(Version::patch);

	static Version of(String s) {
		Matcher m = pattern.matcher(s);
		if (!m.matches()) {
			throw new IllegalArgumentException("bad version: " + s);
		}
		int major = Integer.parseInt(m.group(1));
		int minor = Integer.parseInt(m.group(2));
		int patch = Integer.parseInt(m.group(3));
		return new Version(major, minor, patch);
	}

	public String print() {
		return major() + "." + minor() + "." + patch();
	}

	public String toString() {
		return print();
	}

	void validate(Version b) {
		if (this.equals(b)) {
			out.println(b.print());
		}
		else {
			throw new IllegalArgumentException("version mismatch. a = " + this.print() + " b = " + b.print());
		}
	}

	@Override
	public int compareTo(Version o) {
		return COMPARATOR.compare(this, o);
	}

}
