package io.jstach.test.opt.spring.webflux.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.jstach.opt.spring.webflux.example.App;
import io.jstach.opt.spring.webflux.example.SpringTemplateConfig;
import io.jstach.opt.spring.webflux.example.WebConfig;

@SpringBootTest(classes = { App.class, WebConfig.class, SpringTemplateConfig.class })
@AutoConfigureWebTestClient
public class HelloControllerTest {

	@Autowired
	WebTestClient client;
	static final String ENCODER_BODY = """
			<!doctype html>
			<html lang="en">
			  <head>
			    <meta charset="utf-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1">
			    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
			    <title>Hello, Spring Boot WebFlux is now JStachioed!!</title>
			  </head>
			  <body>
			    <main class="container">
			      <h1>Spring Boot WebFlux is now JStachioed!</h1>
			    </main>
			  </body>
			</html>""";
	static final String VIEW_BODY = """
			<!doctype html>
			<html lang="en">
			  <head>
			    <meta charset="utf-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1">
			    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
			    <title>Hello, Spring Boot WebFlux View is now JStachioed!!</title>
			  </head>
			  <body>
			    <main class="container">
			      <h1>Spring Boot WebFlux View is now JStachioed!</h1>
			    </main>
			  </body>
			</html>""";

	@ParameterizedTest
	@ValueSource(strings = { "text/html", "*/*", /* "application/json" */ })
	public void testEncoder(String mediaType) {
		String path = "/";
		String expected = ENCODER_BODY;
		MediaType accept = MediaType.parseMediaType(mediaType);
		assertEndpoint(path, expected, accept);
	}

	@ParameterizedTest
	@ValueSource(strings = { "application/json", "application/fake" })
	public void testEncoderBadMediaType(String mediaType) {
		String path = "/";
		MediaType accept = MediaType.parseMediaType(mediaType);
		/*
		 * This is not ideal
		 *
		 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/406
		 *
		 * In practice, this error is very rarely used. Instead of responding using this
		 * error code, which would be cryptic for the end user and difficult to fix,
		 * servers ignore the relevant header and serve an actual page to the user. It is
		 * assumed that even if the user won't be completely happy, they will prefer this
		 * to an error code.
		 */
		client.get().uri(path) //
				.accept(accept) //
				.exchange() //
				.expectStatus() //
				.isEqualTo(HttpStatusCode.valueOf(406));
	}

	@ParameterizedTest
	@ValueSource(strings = { "text/html", "*/*", /* "application/json" */ })
	public void testView(String mediaType) {
		String path = "/mvc";
		String expected = VIEW_BODY;
		MediaType accept = MediaType.parseMediaType(mediaType);
		assertEndpoint(path, expected, accept);
	}

	@ParameterizedTest
	@ValueSource(strings = { "application/json", "application/fake" })
	public void testViewBadMediaType(String mediaType) {
		String path = "/mvc";
		MediaType accept = MediaType.parseMediaType(mediaType);
		/*
		 * This is not ideal
		 *
		 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/406
		 *
		 * In practice, this error is very rarely used. Instead of responding using this
		 * error code, which would be cryptic for the end user and difficult to fix,
		 * servers ignore the relevant header and serve an actual page to the user. It is
		 * assumed that even if the user won't be completely happy, they will prefer this
		 * to an error code.
		 */
		client.get().uri(path) //
				.accept(accept) //
				.exchange() //
				.expectStatus() //
				.isEqualTo(HttpStatusCode.valueOf(406));
	}

	void assertEndpoint(String path, String expected, MediaType accept) {
		long contentLength = expected.getBytes(StandardCharsets.UTF_8).length;
		EntityExchangeResult<byte[]> result = client.get().uri(path) //
				.accept(accept) //
				.exchange().expectHeader().contentType("text/html;charset=UTF-8") //
				.expectBody() //
				.returnResult();
		String actual = new String(result.getResponseBody(), StandardCharsets.UTF_8);
		assertEquals(expected, actual);
		long actualLength = result.getResponseHeaders().getContentLength();
		assertEquals(contentLength, actualLength);
	}

}
