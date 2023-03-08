package io.jstach.opt.spring.webflux.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.jstach.jstache.JStachePath;

/**
 * Entry point.
 *
 * @author agentgt
 */
@SpringBootApplication
@JStachePath(prefix = "views/", suffix = ".mustache")
public class App {

	/**
	 * To placate JDK 18 javadoc.
	 */
	public App() {
	}

	/**
	 * Canonical entry point that will launch Spring
	 * @param args the command line args
	 */
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
