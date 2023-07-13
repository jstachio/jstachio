package io.jstach.test.opt.dropwizard.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.dropwizard.testing.DropwizardTestSupport;
import io.jstach.opt.dropwizard.example.ExampleApplicationStart;
import io.jstach.opt.dropwizard.example.ExampleConfiguration;

public class DropwizardExampleAppTest {

	public static DropwizardTestSupport<ExampleConfiguration> SUPPORT;

	@BeforeAll
	public static void beforeClass() throws Exception {
		SUPPORT = new DropwizardTestSupport<ExampleConfiguration>(ExampleApplicationStart.class,
				new ExampleConfiguration());
		SUPPORT.before();
	}

	@AfterAll
	public static void afterClass() {
		SUPPORT.after();
	}

	String MIXIN_EXPECTED = """
			<!doctype html>
			<html lang="en">
			  <head>
			    <meta charset="utf-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1">
			    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
			    <title>Hello, Hello world dropwizard using mixin!</title>
			  </head>
			  <body>
			    <main class="container">
			      <h1>Hello world dropwizard using mixin</h1>
			    </main>
			  </body>
			</html>""";

	String TEMPLATE_EXPECTED = """
			<!doctype html>
			<html lang="en">
			  <head>
			    <meta charset="utf-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1">
			    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
			    <title>Hello, Hello dropwizard using template directly.!</title>
			  </head>
			  <body>
			    <main class="container">
			      <h1>Hello dropwizard using template directly.</h1>
			    </main>
			  </body>
			</html>""";

	@Test
	public void testExampleMixin() throws IOException, InterruptedException {
		var response = get("/example");
		assertEquals(MIXIN_EXPECTED, response.body());
	}

	@Test
	public void testTemplate() throws IOException, InterruptedException {
		var response = get("/example/template");
		assertEquals(TEMPLATE_EXPECTED, response.body());
	}

	private HttpResponse<String> get(String path) throws IOException, InterruptedException {
		var client = HttpClient.newBuilder().build();
		var uri = URI.create("http://localhost:" + SUPPORT.getLocalPort() + path);
		var request = HttpRequest.newBuilder(uri).GET().build();
		var response = client.send(request, BodyHandlers.ofString());
		return response;
	}

}
