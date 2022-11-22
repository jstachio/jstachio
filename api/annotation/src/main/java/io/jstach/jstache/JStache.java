package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Optional;

import io.jstach.jstache.JStacheContentType.AutoContentType;
import io.jstach.jstache.JStacheFormatter.AutoFormatter;

/**
 * Generates a JStachio Renderer from a template and a model (the annotated class).
 * <p>
 * Classes annotated are typically called "models" as they will be the root context for
 * the template.
 *
 * <h2 class="toc-title">Contents</h2> <div class="js-toc"></div>
 * <div class="js-toc-content">
 * <h2 id="_example">Example Usage</h2>
 *
 * <pre class="code">
 * <code>
 * &#64;JStache(template = &quot;&quot;&quot;
 *     {{#people}}
 *     {{message}} {{name}}! You are {{#ageInfo}}{{age}}{{/ageInfo}} years old!
 *     {{#-last}}
 *     That is all for now!
 *     {{/-last}}
 *     {{/people}}
 *     &quot;&quot;&quot;)
 * public record HelloWorld(String message, List&lt;Person&gt; people) implements AgeLambdaSupport {}
 *
 * public record Person(String name, LocalDate birthday) {}
 *
 * public record AgeInfo(long age, String date) {}
 *
 * public interface AgeLambdaSupport {
 *   &#64;JStacheLambda
 *   default AgeInfo ageInfo(
 *       Person person) {
 *     long age = ChronoUnit.YEARS.between(person.birthday(), LocalDate.now());
 *     String date = person.birthday().format(DateTimeFormatter.ISO_DATE);
 *     return new AgeInfo(age, date);
 *   }
 * }
 * </code> </pre>
 *
 * <h2 id="_model_and_templates">Models and Templates</h2>
 *
 * Because JStachio checks types its best to think of the model and template as married.
 * With the exception of partials JStachio cannot have a template without a model and vice
 * versa. The way to create Renderer (what we call the model and template combined) is to
 * annotate your model with {@link io.jstach.jstache.JStache}.
 *
 * <h3 id="_models">Models</h3> <strong>&#64;{@link io.jstach.jstache.JStache}</strong>
 * <p>
 * A JStachio model can be any class type including Records and Enums so long as you can
 * you annotate the type with {@link io.jstach.jstache.JStache}.
 * <p>
 * When the compiler runs the annotation processor will create readable java classes that
 * are suffixed with "Renderer" which will have methods to write the model to an
 * {@link java.lang.Appendable}. The generated instance methods are named
 * <code>execute</code> and the corresponding static methods are named
 * <code>render</code>.
 * <p>
 * <em>TIP: If you like to see the generated classes from the annotation processor they
 * usually get put in <code>target/generated-sources/annotations</code> for Maven
 * projects.</em>
 *
 * <h4 id="_decorating_models">Adding interfaces to models and renderers</h4>
 * <strong>&#64;{@link io.jstach.jstache.JStacheInterfaces}</strong>
 * <p>
 * Java has a huge advantage over JSON and Javascript. You can use interfaces to add
 * additional variables as well as lambda methods
 * ({@link io.jstach.jstache.JStacheLambda}). To enforce that certain interfaces are added
 * to models (the ones annotated) and renderers (the generated classes) you can use
 * {@link io.jstach.jstache.JStacheInterfaces} on packages or the classes themselves. You
 * can also make generated classes have {@link ElementType#TYPE} annotations as well which
 * maybe useful for integration with other frameworks.
 *
 * <h3 id="_templates">Templates</h3>
 *
 * The format of the templates should by default be Mustache. The syntax is informally
 * explained by the
 * <a href="https://jgonggrijp.gitlab.io/wontache/mustache.5.html">mustache manual</a> and
 * formally explained by the <a href="https://github.com/mustache/spec">spec</a>. There
 * are some subtle differences in JStachio version of Mustache due to the static nature
 * that are discussed elsewhere.
 * <p>
 * Template resolution is as follows:
 * <ol>
 * <li><code>path</code> which is a classpath with slashes following the same format as
 * the ClassLoader resources. The path maybe augmented with {@link JStachePath}.
 * <li><code>template</code> which if not empty is used as the template contents
 * <li>if the above is not set then the name of the class suffixed with ".mustache" is
 * used as the resource
 * </ol>
 *
 * <h4 id="_inline_templates">Inline Templates</h4>
 * <strong>{@link io.jstach.jstache.JStache#template()}</strong>
 * <p>
 * Inline templates are pretty straight forward. Just set
 * {@link io.jstach.jstache.JStache#template()} to a literal string. If you go this route
 * it is <em>highly recommend you use the new triple quote string literal for inline
 * templates</em>
 *
 * <h4 id="_resource_templates">Resource Template</h4>
 * <strong>{@link io.jstach.jstache.JStache#path()}</strong>
 * <p>
 * Resource templates are more complicated because of lookup resolution. You can control
 * how that is done with {@link io.jstach.jstache.JStachePath}.
 *
 * <h4 id="_partials">Partials</h4>
 * <strong><code>{{&gt; partial }} and {{&lt; parent }}{{/parent}} </code></strong>
 * <p>
 * JStachio supports Mustache partials (and parents) and by default works just like
 * template resources such that {@link io.jstach.jstache.JStachePath} is used for
 * resolution if specified.
 * <p>
 * You may also remap partial names via {@link io.jstach.jstache.JStachePartial} to a
 * different location as well as to an inline template (string literal).
 *
 * <h3 id="_context_ext">Context Lookup</h3>
 *
 * JStachio unlike almost all other Mustache implementations does its context lookup
 * statically during compile time. Consequently JStachio pedantically is early bound where
 * as Mustache is traditionally late bound. Most of the time this difference will not
 * manifest itself so long as you avoid using {@link Map} in your models.
 * <p>
 * The other notable difference is JStachio does not like missing variables (a compiler
 * error will happen) where as many Mustache implementations sometimes allow this and will
 * just not output anything.
 *
 * <h4 id="_context_java_types">Interpretation of Java-types and values</h4> When some
 * value is null nothing is rendered if it is used as a section. If some value is null and
 * it is used as a variable a null pointer exception will be thrown by default. This is
 * configurable via {@link JStacheFormatterTypes} and custom {@link JStacheFormatter}.
 * <p>
 * Boxed and unboxed <code>boolean</code> can be used for mustache-sections. Section is
 * only rendered if value is true.
 * <p>
 * {@link Optional} empty is treated like an empty list or a boolean false. Optional
 * values are always assumed to be non null.
 * <p>
 * {@code Map<String,?>} follow different nesting rules than other types. If you are in a
 * {@link Map} nested section the rest of the context is checked before the
 * <code>Map</code>. Once that is done the Map is then checked using
 * {@link Map#get(Object)}' where the key is the <em>last part of the dotted name</em>.
 * <p>
 * Data-binding contexts are nested. Names are looked up in innermost context first. If
 * name is not found in current context, parent context is inspected. This process
 * continues up to root context.
 *
 * In each rendering context name lookup is performed as follows:
 *
 * <ol>
 * <li>Method with requested name is looked up. Method should have no arguments and should
 * throw no checked exceptions. If there is such method it is used to fetch actual data to
 * render. Compile-time error is raised if there is method with given name, but it is not
 * accessible, has parameters or throws checked exceptions.</li>
 * <li>Method with requested name and annotated correctly with {@link JStacheLambda} and
 * the lookup is for a section than the method lambda method will be used.</li>
 * <li>Method with getter-name for requested name is looked up. (For example, if 'age' is
 * requested, 'getAge' method is looked up.) Method should have no arguments and should
 * throw no checked exceptions. If there is such method it is used to fetch actual data to
 * render. Compile-time error is raised if there is method with such name, but it is not
 * accessible, has parameters or throws checked exceptions</li>
 *
 * <li>Field with requested name is looked up. Compile-time error is raised if there is
 * field with such name but it's not accessible.</li>
 * </ol>
 *
 * <h4 id="_enums">Enum matching Support Extension</h4> Basically enums have boolean keys
 * that are the enums name (`Enum.name()`) that can be used as conditional sections.
 * Assume `light` is an enum like:
 *
 * <pre>
 * <code class="language-java">
 * public enum Light {
 *   RED,
 *   GREEN,
 *   YELLOW
 * }
 * </code> </pre>
 *
 * You can conditinally select on the enum like a pattern match:
 *
 * <pre>
 * <code class="language-hbs">
 * {{#light.RED}}
 * STOP
 * {{/light.RED}}
 * {{#light.GREEN}}
 * GO
 * {{/light.GREEN}}
 * {{#light.YELLOW}}
 * Proceeed with caution
 * {{/light.YELLOW}}
 * </code> </pre>
 *
 * <h4 id="_index_support">Index Support Extension</h4>
 *
 * JStachio is compatible with both handlebars and JMustache index keys for iterable
 * sections.
 * <ol>
 * <li><code>-first</code> is boolean that is true when you are on the first item
 * <li><code>-last</code> is a boolean that is true when you are on the last item in the
 * iterable
 * <li><code>-index</code> is a one based index. The first item would be `1` and not `0`
 * </ol>
 *
 * <h3 id="_lambdas">Lambda Support</h3>
 *
 * <strong>&#64;{@link JStacheLambda}</strong>
 * <p>
 * JStachio supports lambda section calls in a similar manner to
 * <a href="https://github.com/samskivert/jmustache">JMustache</a>. Just tag your methods
 * with {@link JStacheLambda} and the returned models will be used to render the contents
 * of the lambda section. The top of the context stack can be passed to the lambda.
 *
 * <h2 id="_formatting">Formatting variables</h2>
 *
 * JStachio has strict control on what happens when you output a variable like
 * <code>{{variable}}</code> or <code>{{{variable}}}</code>.
 *
 * <h3 id="_allowed_types">Allowed formatting types</h3> <strong>
 * &#64;{@link io.jstach.jstache.JStacheFormatterTypes}</strong>
 * <p>
 * Only a certain set of types are allowed to be formatted and if they are not a compiler
 * error will happen (as in the annotation processor will fail). To understand more about
 * that see {@link io.jstach.jstache.JStacheFormatterTypes}.
 *
 * <h3 id="_runtime_formatting">Runtime formatting</h3>
 * <strong>&#64;{@link io.jstach.jstache.JStacheFormatter} and
 * {@link io.jstach.jstache.JStache#formatter()}</strong>
 * <p>
 * Assuming the compiler allowed the variable to be formatted you can control the output
 * via {@link io.jstach.jstache.JStacheFormatter} and setting
 * {@link io.jstach.jstache.JStache#formatter()}.
 *
 * <h2 id="_escaping">Escaping and Content Type</h2>
 * <strong>&#64;{@link io.jstach.jstache.JStacheContentType} and
 * {@link io.jstach.jstache.JStache#contentType()}</strong>
 * <p>
 * If you are using the JStachio runtime (io.jstach.jstachio) you will get out of the box
 * escaping for HTML per the mustache spec. </div>
 *
 *
 * @author agentgt
 * @see JStachePath
 * @see JStacheFormatterTypes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JStache {

	/**
	 * Resource path to template
	 * @return Path to mustache template
	 * @see JStachePath
	 */
	String path() default "";

	/**
	 * Inline the template as a Java string instead of a file. Use the new triple quote
	 * string literal for complex templates.
	 * @return An inline template
	 */
	String template() default "";

	/**
	 * Name of generated class.
	 * <p>
	 * adapterName can be omitted. "{{className}}Renderer" name is used by default.
	 * @return Name of generated class
	 */
	String adapterName() default "";

	/**
	 * Class representing template content type to be used by escapers.
	 * <p>
	 * You can create custom escapers using {@link JStacheContentType} annotation.
	 * @return contentType of given template. If not provided it will be resolved (HTML is
	 * the default if the jstachio runtime is found).
	 */
	Class<?> contentType() default AutoContentType.class;

	/**
	 * Class providing the base formatter.
	 * <p>
	 * You can create custom formatters using {@link JStacheFormatter} annotation.
	 * @return formatter of given template. The default will be resolved (a non null that
	 * will throw NPE is the default if the jstachio runtime is found)
	 *
	 * @see JStacheFormatterTypes
	 */
	Class<?> formatter() default AutoFormatter.class;

	/**
	 * Encoding of given template file.
	 * <p>
	 * charset can be omitted. Default system charset is used by default.
	 * @return encoding of given template file
	 */
	String charset() default "";

}
