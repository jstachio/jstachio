package io.jstach.opt.spring.example.hello;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateModel;
import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import io.jstach.opt.spring.webmvc.JStachioModelView;

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
	 * @param jstachio spring powered jstachio
	 * @param wired in template
	 */
	public HelloController(JStachio jstachio, Template<HelloModel> view) {
		this.jstachio = jstachio;
		this.view = view;
	}

	/**
	 * (Optional) Spring will inject this template as the templates are either component
	 * scanned or loaded by the ServiceLoader into Spring's context.
	 *
	 * This is usually not needed as just returning the models is good enough.
	 */
	public final Template<HelloModel> view;

	/**
	 * Although not needed You can also wire in JStachio directly
	 */
	public final JStachio jstachio;

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
	 * Here we use the generated code directly and return a {@link TemplateModel} which is
	 * analogous to {@link ModelAndView}.
	 *
	 * @apiNote Notice that the method has to be annotated with
	 * <code>&#64;ResponseBody</code>.
	 * @return the template model pair that already has the template found.
	 * @see JStachioHttpMessageConverter
	 */
	@GetMapping(value = "/templateModel")
	@ResponseBody
	public TemplateModel templateModel() {
		return HelloModelView.of().model(new HelloModel("Spring Boot is using JStachio TemplateModel!"));
	}

	/**
	 * Here we use a {@link ResponseEntity} which allows use to set status codes with our
	 * model to be rendered.
	 * @return a response entity.
	 * @see JStachioHttpMessageConverter
	 */
	@GetMapping(value = "/responseEntity")
	public ResponseEntity<HelloModel> entity() {
		return ResponseEntity.badRequest().body(new HelloModel("Spring Boot is using JStachio ResponseEntity. "
				+ "This is a 400 http error code but is not an actual error!"));
	}

	/**
	 * Here we could use {@link JStacheInterfaces} to make our model implement
	 * {@link JStachioModelView} to support the traditional servlet MVC approach. The
	 * model will use the static jstachio singleton that will be the spring one.
	 * <p>
	 * This approach has pros and cons. It makes your models slightly coupled to Spring
	 * MVC but allows you to return different views if say you had to redirect on some
	 * inputs ({@link org.springframework.web.servlet.view.RedirectView}).
	 *
	 * @apiNote Notice that the return type is {@link View}.
	 * @return the model and view that will be used as View (see
	 * {@link HelloModelAndView}).
	 * @see JStachioHttpMessageConverter
	 * @see HelloModelAndView
	 */
	@GetMapping(value = "/mvc")
	@SuppressWarnings("exports")
	public View mvc() {
		return new HelloModelAndView("Spring Boot MVC is now JStachioed!");
	}

	/**
	 * Here we use the {@linkplain #view wired renderer}.
	 */
	@GetMapping(value = "/wired")
	public TemplateModel wired() throws IOException {
		var model = new HelloModel("JStachioed is wired!");
		return view.model(model);
	}

}
