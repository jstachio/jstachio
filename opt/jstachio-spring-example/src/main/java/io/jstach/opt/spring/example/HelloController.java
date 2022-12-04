package io.jstach.opt.spring.example;

import java.io.IOException;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.jstach.opt.spring.JStachioHttpMessageConverter;

/**
 * Example hello world controller using different ways to use JStachio for web
 * development.
 *
 * @author agentgt
 */
@Controller
public class HelloController {

	/**
	 * Placate JDK 18 Javadoc
	 */
	public HelloController() {
	}

	/**
	 * Spring will inject this as the templates are component scanned as this projects
	 * module {@link io.jstach.opt.spring.example/ } has a config that will add &#64;
	 * {@link Component} to all generated code.
	 */
	@Autowired(required = true)
	public HelloModelView view;

	/**
	 * Here we use JStachio runtime to resolve the renderer (in this case we are calling
	 * them Views).
	 * @return the model that will be used to find the correct view and then rendered
	 * using that view
	 * @see JStachioHttpMessageConverter
	 */
	@GetMapping(value = "/")
	@ResponseBody
	public HelloModel hello() {
		return new HelloModel("Spring Boot is now JStachioed!");
	}

	/**
	 * Here we use the {@link #view wired renderer} that does not have filtering and thus
	 * cannot use JMustache for dynamic editing of templates.
	 * @param writer spring will inject the servlet output
	 * @throws IOException an error while writing to the output
	 */
	@GetMapping(value = "/wired")
	public void wired(Writer writer) throws IOException {
		var model = new HelloModel("JStachioed is wired!");
		view.execute(model, writer);
	}

}
