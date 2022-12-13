package io.jstach.opt.spring;

import org.springframework.web.servlet.View;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheInterfaces;

/**
 * A mixin interface for generated templates/renderers that will allow you to construct a
 * {@link View} from the corresponding model instances.
 * <p>
 * To make this work set {@link JStacheInterfaces#templateImplements()} to this interface
 * in the same package or module of the models.
 * <p>
 * <strong>Example:</strong>
 *
 * <pre><code class="language-java">
 * &#64;JStacheInterfaces(templateImplements=ViewFactory.class)
 * module mymodule {
 * }
 * </code> </pre>
 *
 * Now in the controller:
 *
 * <pre><code class="language-java">
 * &#64;Autowired(required = true)
 * public ViewFactory&lt;MyModel&gt; myModelView;
 *
 * &#64;RequestMapping("/somepath")
 * public View somepath() {
 *     return myModelView.view(new MyModel());
 * }
 * </code> </pre>
 *
 * @author agentgt
 * @param <T> should be the model type
 */
public interface ViewFactory<T> {

	/**
	 * Creates Spring MVC View from a model.
	 * <p>
	 * <em>The created view will ignore the Map model in render and instead use the model
	 * passed into this method.</em>
	 * @param model the {@link JStache} model.
	 * @return view that has the model and is now ready for rendering
	 */
	@SuppressWarnings("exports")
	default View view(T model) {
		return JStachioModelView.of(model);
	}

}
