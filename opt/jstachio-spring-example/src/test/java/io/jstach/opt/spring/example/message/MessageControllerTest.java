package io.jstach.opt.spring.example.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.jstach.opt.spring.webmvc.JStachioModelView;

@WebMvcTest(MessageController.class)
@Import(MessageConfiguration.class)
public class MessageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testShowMessage() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.get("/message")).andExpect(status().isOk()).andExpect(result -> {
			JStachioModelView view = (JStachioModelView) result.getModelAndView().getView();
			MessagePage message = (MessagePage) view.model();
			assertThat(message.message).isNotNull();
		});

	}

}
