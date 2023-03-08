package io.jstach.opt.spring.webflux.example.message;

import io.jstach.jstache.JStache;

/**
 * Model using a configurer to add state.
 *
 * @author agentgt
 * @author dsyer
 */
@JStache(path = "hello")
public class MessagePage {

	/**
	 * An example of field access.
	 */
	public String message;

}
