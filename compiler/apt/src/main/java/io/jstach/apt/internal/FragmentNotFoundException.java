package io.jstach.apt.internal;

import java.io.FileNotFoundException;

public class FragmentNotFoundException extends FileNotFoundException {

	private static final long serialVersionUID = 1L;

	private final String path;

	private final String fragment;

	public FragmentNotFoundException(String path, String fragment) {
		super(message(path, fragment));
		this.path = path;
		this.fragment = fragment;
	}

	private static String message(String path, String fragment) {
		return String.format("Fragment \"%s\" not found in resource \"%s\".", fragment, path);
	}

	public String getFragment() {
		return fragment;
	}

	public String getPath() {
		return path;
	}

}
