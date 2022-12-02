package io.jstach.opt.spring.example;

import java.io.IOException;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @hidden
 * @author agent
 */
@Controller
public class HelloController {

	@Autowired(required = true)
	HelloModelRenderer template;

	@GetMapping(value = "/")
	@ResponseBody
	public HelloModel hello() {
		return new HelloModel("Spring Boot is now JStachioed!");
	}

	@GetMapping(value = "/wired")
	public void writed(Writer writer) throws IOException {
		var model = new HelloModel("JStachioed is wired!");
		template.execute(model, writer);
	}

}
