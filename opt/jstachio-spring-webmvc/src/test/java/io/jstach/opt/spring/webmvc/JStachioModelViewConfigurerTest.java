package io.jstach.opt.spring.webmvc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class JStachioModelViewConfigurerTest {

	@Test
	public void testIssue248() {
		JStachioModelViewConfigurer c = (page, model, request) -> {
			model.put("key", "value");
		};

		Map<String, Object> model = new LinkedHashMap<>();
		c.configure(c, model, new MockHttpServletRequest());
	}

}
