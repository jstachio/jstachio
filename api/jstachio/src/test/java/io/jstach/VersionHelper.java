package io.jstach;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.System.out;

public enum VersionHelper {

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
			var a = Version.of(args.get(0));
			var b = current();
			if (a.equals(b)) {
				out.println(b.print());
			}
			else {
				throw new IllegalArgumentException(
						"version mismatch. current = " + b.print() + " input = " + a.print());
			}
		}
	},
	PARSE() {
		@Override
		public void run(List<String> args) throws IOException {
			var a = Version.of(args.get(0));
			out.println(a.print());
		}
	},
	GIT() {
		@Override
		public void run(List<String> args) throws IOException {
			String r = execute("git tag -l --sort=v:refname", 1);
			var v = Version.of(r.trim());
			out.println(v);
		}
	},
	MVN() {
		@Override
		public void run(List<String> args) throws IOException {
			var current = current();
			out.println("mvn versions:set -DnewVersion=" + current.print());
		}
	};

	public abstract void run(List<String> args) throws IOException;

	record Version(int major, int minor, int patch) {

		static final Pattern pattern = Pattern.compile("v?([0-9]+).([0-9]+).([0-9]+)(-SNAPSHOT)?");
		static Version of(String s) {
			Matcher m = pattern.matcher(s);
			if (!m.matches()) {
				throw new IllegalArgumentException(s);
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
	}

	static Version current() throws IOException {
		var props = new Properties();
		props.load(Files.newBufferedReader(Path.of("version.properties")));
		String v = props.getProperty("version");
		return Version.of(v);
	}

	static String execute(String command) throws IOException {
		return execute(command, -1);
	}

	static String execute(String command, int lines) throws IOException {
		ProcessBuilder b = new ProcessBuilder();
		b.command("bash", "-c", command);

		StringBuilder output = new StringBuilder();
		Process process = b.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		int i = 0;
		if (lines < 0) {
			lines = Integer.MAX_VALUE;
		}
		while ((line = reader.readLine()) != null) {
			if (i++ > lines) {
				break;
			}
			output.append(line + "\n");
		}
		int e;
		try {
			e = process.waitFor();
		}
		catch (InterruptedException e1) {
			throw new RuntimeException(e1);
		}
		if (e == 0) {
			return output.toString();
		}
		else {
			throw new IOException(
					"Failure executing command: " + command + ", exit: " + e + ", output: " + output.toString());
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			out.println("No command passed");
			out.println("COMMANDS = " + EnumSet.allOf(VersionHelper.class));
			System.exit(1);
		}
		VersionHelper helper = VersionHelper.valueOf(args[0].toUpperCase());
		var params = Stream.of(args).skip(1).toList();
		try {
			helper.run(params);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
