package io.jstach.opt.spring.webflux.example.hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.reactive.result.view.View;

import io.jstach.jstache.JStacheInterfaces;
import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import io.jstach.opt.spring.webflux.JStachioModelView;

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
	 * Here we use JStachio runtime to resolve the renderer (in this case we are calling
	 * them Views) via Springs Http Message Converter.
	 * @apiNote Notice that the method has to be annotated with
	 * <code>&#64;ResponseBody</code>.
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
	 * Here we use {@link JStacheInterfaces} to make our model implement a Spring View to
	 * support the traditional servlet MVC approach. The model will use the static
	 * jstachio singleton that will be the spring one.
	 * <p>
	 * This approach has pros and cons. It makes your models slightly coupled to Spring
	 * MVC but allows you to return different views if say you had to redirect on some
	 * inputs ({@link RedirectView}).
	 *
	 * @apiNote Notice that the return type is {@link View}.
	 * @return the model that will be used as View
	 * @see JStachioHttpMessageConverter
	 */
	@GetMapping(value = "/webflux")
	public View mvc() {
		return JStachioModelView.of(new HelloModel("Spring Boot WebFlux is now JStachioed!"));
	}

}
