package io.jstach.jstachio.test.graalvm;

import java.io.IOException;
import java.lang.System.Logger;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioFactory;

public class Main {

	public static void main(String[] args) {
		var js = JStachioFactory.builder().add(new MyConfig()).build();
		JStachio.setStatic(() -> js);
		var helloworld = new HelloWorldModel("Hello native");
		// js.supportsType(helloworld.getClass());
		try {
			JStachio.of().execute(helloworld, System.out);
			JStachio.of().execute(new Bad(), System.out);

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	record Bad() {
	}

	static class MyConfig implements JStachioConfig {

		MyLogger logger = new MyLogger();

		@Override
		public @Nullable String getProperty(String key) {
			System.out.println("Getting key: " + key);
			return System.getProperty(key);
		}

		@Override
		public Logger getLogger(String name) {
			System.out.println("Getting logger");
			return logger;
		}

	}

	static class MyLogger implements System.Logger {

		@Override
		public @NonNull String getName() {
			return "MyLogger";
		}

		@Override
		public boolean isLoggable(@NonNull Level level) {
			return true;
		}

		@Override
		public void log(@NonNull Level level, @Nullable ResourceBundle bundle, @Nullable String msg,
				@Nullable Throwable thrown) {
			System.out.println("" + level + " " + msg);

		}

		@Override
		public void log(@NonNull Level level, @Nullable ResourceBundle bundle, @Nullable String format,
				@Nullable Object @NonNull... params) {
			System.out.println("" + level + " " + format);

		}

	}

}
