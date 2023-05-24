package io.jstach.opt.spring.webflux.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStachePath;

/**
 * Entry point and class that defines shared jstachio config to be imported in other
 * places with {@link JStacheConfig#using()}.
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
