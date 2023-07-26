package io.jstach.opt.spring.example.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.View;

import io.jstach.opt.spring.webmvc.JStachioModelView;

/**
 * Example controller that uses a View which has global state injected into it via a
 * handler interceptor.
 *
 * @author dsyer
 */
@SuppressWarnings("exports")
@Controller
public class MessageController {

	/**
	 * Created by Spring
	 */
	public MessageController() {
	}

	/**
	 * Here we use the global configurer to inject state into the {@link View}.
	 * @return view
	 */
	@GetMapping(value = "/message")
	public View message() {
		return JStachioModelView.of(new MessagePage());
	}

	/**
	 * Here we use the return value type to construct a {@link View} that will be
	 * rendered.
	 */
	@GetMapping(value = "/msg")
	public MessagePage msg() {
		return new MessagePage();
	}

}
