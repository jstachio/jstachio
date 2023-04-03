package io.jstach.opt.spring.webflux.example.message;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.View;

import io.jstach.opt.spring.webflux.JStachioModelView;

/**
 * Example controller that uses a View which has global state injected into it via a
 * handler interceptor.
 *
 * @author dsyer
 */
@Controller
public class MessageController {

	/**
	 * Called by Spring
	 */
	public MessageController() {
	}

	/**
	 * Here we use the global configurer to inject state into the {@link View}.
	 * @return reactive jstachio view
	 */
	@GetMapping(value = "/message")
	public View message() {
		return JStachioModelView.of(new MessagePage());
	}

}
