package io.jstach.test.opt.spring.example;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.jstach.opt.spring.example.App;

@SpringBootTest(classes = { App.class })
@AutoConfigureMockMvc
public class HelloControllerTest {

	@Autowired
	private MockMvc mockMvc;

	static final String SLASH_BODY = """
			<!doctype html>
			<html lang="en">
			  <head>
			    <meta charset="utf-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1">
			    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
			    <title>Hello, Spring Boot is now JStachioed!!</title>
			  </head>
			  <body>
			    <main class="container">
			      <h1>Spring Boot is now JStachioed!</h1>
			    </main>
			  </body>
			</html>""";

	@Test
	public void testHelloWithMediaTypeAll() throws Exception {
		var rb = MockMvcRequestBuilders.get("/").accept(MediaType.ALL);
		var result = mockMvc.perform(rb).andReturn();
		var bytes = result.getResponse().getContentAsByteArray();
		System.out.println(bytes.length);
		String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertEquals(414, result.getResponse().getContentLength());
		assertEquals(414, SLASH_BODY.getBytes(StandardCharsets.UTF_8).length);
		assertEquals(414, body.getBytes(StandardCharsets.UTF_8).length);
		assertEquals(SLASH_BODY, body);
		String contentType = result.getResponse().getContentType();
		assertEquals("text/html;charset=UTF-8", contentType);
	}

	@Test
	public void testTemplateModelWithAcceptJson() throws Exception {
		var rb = MockMvcRequestBuilders.get("/templateModel").accept(MediaType.APPLICATION_JSON);
		var result = mockMvc.perform(rb).andReturn();
		String expectedBody = """
				<!doctype html>
				<html lang="en">
				  <head>
				    <meta charset="utf-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1">
				    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
				    <title>Hello, Spring Boot is using JStachio TemplateModel!!</title>
				  </head>
				  <body>
				    <main class="container">
				      <h1>Spring Boot is using JStachio TemplateModel!</h1>
				    </main>
				  </body>
				</html>""";
		assertResponse(result.getResponse(), expectedBody);
	}

	@Test
	public void testResponseEntity() throws Exception {
		var rb = MockMvcRequestBuilders.get("/responseEntity");
		var result = mockMvc.perform(rb).andReturn();
		String expectedBody = """
				<!doctype html>
				<html lang="en">
				  <head>
				    <meta charset="utf-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1">
				    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
				    <title>Hello, Spring Boot is using JStachio ResponseEntity. This is a 400 http error code but is not an actual error!!</title>
				  </head>
				  <body>
				    <main class="container">
				      <h1>Spring Boot is using JStachio ResponseEntity. This is a 400 http error code but is not an actual error!</h1>
				    </main>
				  </body>
				</html>""";

		assertResponse(result.getResponse(), expectedBody);
		assertEquals(400, result.getResponse().getStatus());
	}

	@Test
	public void testMvc() throws Exception {
		var rb = MockMvcRequestBuilders.get("/mvc");
		var result = mockMvc.perform(rb).andReturn();
		String expectedBody = """
				<!doctype html>
				<html lang="en">
				  <head>
				    <meta charset="utf-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1">
				    <link rel="stylesheet" href="https://unpkg.com/@picocss/pico@latest/css/pico.min.css">
				    <title>Hello, Spring Boot MVC is now JStachioed!!</title>
				  </head>
				  <body>
				    <main class="container">
				      <h1>Spring Boot MVC is now JStachioed!</h1>
				    </main>
				  </body>
				</html>""";

		assertResponse(result.getResponse(), expectedBody);
	}

	private void assertResponse(MockHttpServletResponse response, String expectedBody)
			throws UnsupportedEncodingException {
		String body = response.getContentAsString(StandardCharsets.UTF_8);
		assertEquals(expectedBody, body);
		int expectedLength = expectedBody.getBytes(StandardCharsets.UTF_8).length;
		assertEquals(expectedLength, response.getContentLength());
		assertEquals(expectedLength, body.getBytes(StandardCharsets.UTF_8).length);
		String contentType = response.getContentType();
		assertEquals("text/html;charset=UTF-8", contentType);
	}

}
