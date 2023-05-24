package io.jstach.opt.spring.example.message;

import io.jstach.jstache.JStache;

/**
 * Model using a configurer to add state.
 *
 * @author agentgt
 */
@JStache(path = "hello")
public class MessagePage {

	/**
	 * Message field we have made mutable on purpose to before rendering but after the
	 * controller is done.
	 */
	public String message;

	/**
	 * Default constructor required for javadoc 18+
	 */
	public MessagePage() {
	}

}
