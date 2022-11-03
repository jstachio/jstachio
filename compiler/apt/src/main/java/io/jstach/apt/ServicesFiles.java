package io.jstach.apt;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;

import io.jstach.apt.internal.util.ClassRef;

import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * A helper class for reading and writing Services files.
 */
final class ServicesFiles {

	public static final String SERVICES_PATH = "META-INF/services";

	private ServicesFiles() {
	}

	/**
	 * Returns an absolute path to a service file given the class name of the service.
	 * @param serviceName not {@code null}
	 * @return SERVICES_PATH + serviceName
	 */
	static String getPath(String serviceName) {
		return SERVICES_PATH + "/" + serviceName;
	}

	/**
	 * Reads the set of service classes from a service file.
	 * @param input not {@code null}. Closed after use.
	 * @return a not {@code null Set} of service class names.
	 * @throws IOException
	 */
	static Set<String> readServiceFile(InputStream input) throws IOException {
		LinkedHashSet<String> serviceClasses = new LinkedHashSet<String>();

		try (BufferedReader r = new BufferedReader(new InputStreamReader(input, UTF_8))) {
			String line;
			while ((line = r.readLine()) != null) {
				int commentStart = line.indexOf('#');
				if (commentStart >= 0) {
					line = line.substring(0, commentStart);
				}
				line = line.trim();
				if (!line.isEmpty()) {
					serviceClasses.add(line);
				}
			}
			return serviceClasses;
		}
	}

	static FileObject getResourceFile(Filer filer, final String file) throws IOException {
		return filer.getResource(StandardLocation.CLASS_OUTPUT, "", file);
	}

	static FileObject createResourceFile(Filer filer, final String file) throws IOException {
		return filer.createResource(StandardLocation.CLASS_OUTPUT, "", file);
	}

	/**
	 * Writes the set of service class names to a service file.
	 * @param output not {@code null}. Not closed after use.
	 * @param services a not {@code null Collection} of service class names.
	 * @throws IOException
	 */
	private static void writeServiceFile(Collection<String> services, OutputStream output) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8));
		for (String service : services) {
			writer.write(service);
			writer.newLine();
		}
		writer.flush();
	}

	public static void writeServicesFile(Filer filer, Messager messager, ClassRef serviceInterface,
			Iterable<ClassRef> classes) {
		String serviceFile = ServicesFiles.SERVICES_PATH + "/" + serviceInterface.requireCanonicalName();
		SortedSet<String> services = new TreeSet<>();
		try {
			FileObject existingFile = getResourceFile(filer, serviceFile);
			Set<String> oldServices = ServicesFiles.readServiceFile(existingFile.openInputStream());
			services.addAll(oldServices);
		}
		catch (IOException ioe) {
			messager.printMessage(Kind.NOTE, "no existing services file found");
		}
		SortedSet<String> newServices = StreamSupport.stream(classes.spliterator(), false).map(c -> c.getBinaryName())
				.collect(Collectors.toCollection(TreeSet::new));

		services.addAll(newServices);
		try {
			FileObject existingFile = createResourceFile(filer, serviceFile);
			try (OutputStream os = existingFile.openOutputStream()) {
				ServicesFiles.writeServiceFile(services, os);
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			messager.printMessage(Kind.ERROR, "error writing services files: " + ioe.getMessage());

		}
	}

}