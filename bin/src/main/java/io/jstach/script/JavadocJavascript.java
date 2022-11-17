package io.jstach.script;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class JavadocJavascript {

	static PrintStream out = System.out;

	public static void main(
			String[] args) {
		try {
			findFiles();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static void findFiles()
			throws Exception {
		String path = "../doc/target/site/apidocs";
		//Path resourcesPath = Path.of("../doc/target/site/apidocs/resources");
		Path resourcesPath = Path.of("../doc/target/site/apidocs/resources");

		var p = Path.of(path);
		int maxDepth = 100;
		BiPredicate<Path, BasicFileAttributes> matcher = (
				_p,
				_a) -> {
			return _p.getFileName().toString().endsWith(".html");
		};
		try (Stream<Path> stream = Files.find(p, maxDepth, matcher)) {
			for (var _p : stream.toList()) {
				out.println("Fixing " + _p);
				var relativeResourcesPath = _p.getParent().relativize(resourcesPath);
				addJavascript(_p, relativeResourcesPath);
			}
		}
	}

	static void addJavascript(
			Path htmlPath, Path resourcesPath)
			throws IOException {
		List<String> lines = Files.readAllLines(htmlPath);
		List<String> processed = new ArrayList<>();
		boolean found = false;
		for (String line : lines) {
			if (line.startsWith("</body>")) {
				found = true;
				processed.add(scriptTag("https://cdnjs.cloudflare.com/ajax/libs/tocbot/4.11.1/tocbot.min.js"));
				processed.add(scriptTag(resourcesPath + "/" + "jstachio.js"));
				processed.add(scriptTag("https://cdn.jsdelivr.net/npm/anchor-js/anchor.min.js"));
				processed.add("<script>anchors.add();</script>");
			}
			processed.add(line);
		}
		if (found) {
			Files.write(htmlPath, processed, StandardOpenOption.WRITE);
		}
		else {
			out.println("body tag not found for: " + htmlPath);
		}
	}
	
	static String scriptTag(String src) {
		return "<script src=\"" + src + "\"></script>";
	}

}
