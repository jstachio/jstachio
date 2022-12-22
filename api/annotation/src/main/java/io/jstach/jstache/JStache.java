package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Optional;

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
 * Java has a huge advantage over JSON and Javascript. <em>You can use interfaces to add
 * additional variables as well as lambda methods
 * ({@link io.jstach.jstache.JStacheLambda})!</em> To enforce that certain interfaces are
 * added to models (the ones annotated) and renderers (the generated classes) you can use
 * {@link io.jstach.jstache.JStacheInterfaces} on packages or the classes themselves.
 * <p>
 * You can also make generated classes have {@link ElementType#TYPE} annotations (see
 * {@link JStacheInterfaces#templateAnnotations()}) and extend a class
 * {@link JStacheInterfaces#templateExtends()}) as well which maybe useful for integration
 * with other frameworks particularly DI frameworks.
 *
 * <h3 id="_templates">Templates</h3>
 *
 * The format of the templates should by default be Mustache. The syntax is informally
 * explained by the
 * <a href="https://jgonggrijp.gitlab.io/wontache/mustache.5.html">mustache manual</a> and
 * formally explained by the <a href="https://github.com/mustache/spec">spec</a>. There
 * are some subtle differences in JStachio version of Mustache due to the static nature
 * that are discussed in <a href="#_context_lookup">context lookup</a>. <strong>Template
 * finding is as follows:</strong>
 * <ol>
 * <li><code>path</code> which is a classpath with slashes following the same format as
 * the ClassLoader resources. The path maybe augmented with {@link JStachePath}.
 * <li><code>template</code> which if not empty is used as the template contents
 * <li>if the above is not set then the name of the class suffixed with ".mustache" is
 * used as the resource.
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
 * <h4 id="_resource_templates">Resource Templates</h4>
 * <strong>{@link io.jstach.jstache.JStache#path()} and
 * &#64;{@link io.jstach.jstache.JStachePath} </strong>
 * <p>
 * Resource templates are files that are in the classpath and are more complicated because
 * of lookup resolution.
 * <p>
 * When the annotation processor runs these files usually are in:
 * <code>javax.tools.StandardLocation#CLASS_OUTPUT</code> and in a Maven or Gradle project
 * they normally would reside in <code>src/main/resources</code> or
 * <code>src/test/resources</code> which get copied on build to
 * <code>target/classes</code> or similar. <em>N.B. becareful not to have resource
 * filtering turned on for mustache templates.</em>
 * <p>
 * Ideally JStachio would use <code>javax.tools.StandardLocation#SOURCE_PATH</code> to
 * find resource templates but that is currently <a href=
 * "https://stackoverflow.com/questions/22494596/eclipse-annotation-processor-get-project-path">
 * problematic with incremental compilers such as Eclipse</a>.
 * <p>
 * Another issue with incremental compiling is that template files are not always copied
 * after being edited to <code>target/classes</code> and thus are not found by the
 * annotation processor. To deal with this issue JStachio during compilation fallsback to
 * direct filesystem access and assumes that your templates are located:
 * <code>CWD/src/main/resources</code>. (TODO make that configurable).
 * <p>
 * Normally you need to specify the full path in {@link #path()} which is a resource path
 * as specified by {@link ClassLoader#getResource(String)}) however you can make path
 * expansion happen with {@link io.jstach.jstache.JStachePath} which allows you to prefix
 * and suffix the path.
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
 *
 * <h4 id="_optional_spec">Optional Spec Support</h4> JStachio implements some optional
 * parts of the specification. Below shows what is and is not supported.
 * <table border="1">
 * <caption><strong>Optional Spec Features Table</strong></caption>
 * <tr>
 * <th>Name</th>
 * <th>Supported</th>
 * <th>Manual Description</th>
 * </tr>
 * <tr>
 * <td>Lambda variables (arity 0)</td>
 * <td style="color:red;">NO</td>
 * <td>An optional part of the specification states that if the final key in the name is a
 * lambda that returns a string, then that string should be rendered as a Mustache
 * template before interpolation. It will be rendered using the default delimiters (see
 * Set Delimiter below) against the current context.</td>
 * </tr>
 * <tr>
 * <td>Lambda sections (arity 1)</td>
 * <td style="color:blue;">YES</td>
 * <td>An optional part of the specification states that if the final key in the name is a
 * lambda that returns a string, then that string replaces the content of the section. It
 * will be rendered using the same delimiters (see Set Delimiter below) as the original
 * section content. In this way you can implement filters or caching.</td>
 * </tr>
 * <tr>
 * <td>Dynamic Names</td>
 * <td style="color:red;">NO</td>
 * <td>Partials can be loaded dynamically at runtime using Dynamic Names; an optional part
 * of the Mustache specification which allows to dynamically determine a tag's content at
 * runtime.</td>
 * </tr>
 * <tr>
 * <td>Blocks</td>
 * <td style="color:blue;">YES</td>
 * <td>A block begins with a dollar and ends with a slash. That is, {{$title}} begins a
 * "title" block and {{/title}} ends it.</td>
 * </tr>
 * <tr>
 * <td>Parents</td>
 * <td style="color:blue;">YES</td>
 * <td>A parent begins with a less than sign and ends with a slash. That is,
 * {{&lt;article}} begins an "article" parent and {{/article}} ends it.</td>
 * </tr>
 * </table>
 *
 * <h3 id="_context_lookup">Context Lookup</h3>
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
 *
 * <h2 id="_code_generation">Code Generation</h2>
 *
 * <strong>&#64;{@link io.jstach.jstache.JStacheConfig#type()}</strong>
 * <p>
 * JStachio by default reads mustache syntax and generates code that needs the jstachio
 * runtime (io.jstache.jstachio). However it is possible to generate code that does not
 * need the runtime and possibly in the future other syntaxs like Handlebars might be
 * supported.
 *
 * <h3 id="_methods_generated">Generated Renderer Classes</h3> JStachio generates a single
 * class from a mustache template and model (class annotated with JStache) pair. The
 * generated classes are generally called "Renderers" or sometimes "Templates". Depending
 * on which JStache type is picked different methods are generated. The guaranteed
 * generated methods <em>not to change on minor version or less</em> on the renderer
 * classes are discussed in <strong>{@link JStacheType}</strong>.
 *
 * <h3 id="_zero_dep">Zero dependency code generation</h3>
 *
 * <strong>&#64;{@link io.jstach.jstache.JStacheConfig#type()} ==
 * {@link JStacheType#STACHE}</strong>
 * <p>
 * Zero dependency code generation is useful if you want to avoid coupling your runtime
 * and downstream dependencies with JStachio (including the annotations themselves) as
 * well as minimize the overall footprint and or classes loaded. A common use case would
 * be using jstachio for code generation in an annotation processing library where you
 * want as minimal class path issues as possible.
 * <p>
 * If this configuration is selected generated code will <strong>ONLY have references to
 * stock base JDK module ({@link java.base/}) classes</strong>. However one major caveat
 * is that generated classes will not be reflectively accessible to the JStachio runtime
 * and thus fallback and filtering will not work. Thus in a web framework environment this
 * configuration choice is less desirable.
 * <p>
 * <em>n.b. as long as the jstachio annotations are not accessed reflectively you do not
 * need the annotation jar in the classpath during runtime thus the annotations jar is
 * effectively an optional compile time dependency.</em>
 *
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
 * &#64;{@link JStacheConfig#formatter()}</strong>
 * <p>
 * Assuming the compiler allowed the variable to be formatted you can control the output
 * via {@link io.jstach.jstache.JStacheFormatter} and setting
 * {@link io.jstach.jstache.JStacheConfig#formatter()}.
 *
 * <h2 id="_escaping">Escaping and Content Type</h2>
 * <strong>&#64;{@link io.jstach.jstache.JStacheContentType}, and
 * &#64;{@link JStacheConfig#contentType()} </strong>
 * <p>
 * If you are using the JStachio runtime (io.jstach.jstachio) you will get out of the box
 * escaping for HTML (see <code>io.jstach.jstachio.escapers.Html</code>) per the mustache
 * spec.
 * <p>
 * <strong>To disable escaping</strong> set {@link JStacheConfig#contentType()} to
 * <code>io.jstach.jstachio.escapers.PlainText</code>
 *
 * <h2 id="_config">Configuration</h2> <strong>&#64;{@link JStacheConfig}</strong>
 * <p>
 * You can set global configuration on class, packages and module elements. See
 * {@link JStacheConfig} for more details on config resolution. Some configuration is set
 * through compiler flags and annotation processor options. However {@link JStacheConfig}
 * unlike compiler flags and annotation processor options are available during runtime
 * through reflective access.
 *
 * <h3 id="_config_flags">Compiler flags</h3>
 *
 * The compiler has some boolean flags that can be set statically via {@link JStacheFlags}
 * as well as through annotation processor options.
 *
 * <h3 id="_config_compiler">Annotation processor options</h3>
 *
 * Some configuration is available as an annotation processor option. Current available
 * options are:
 *
 * <ul>
 * <li>{@link #RESOURCES_PATH_OPTION}</li>
 * </ul>
 *
 * The previously mentioned {@link JStacheFlags compiler flags} are also available as
 * annotation options. The flags are prefixed with "<code>jstache.</code>". For example
 * {@link JStacheFlags.Flag#DEBUG} would be:
 * <p>
 * <code>jstache.debug=true/false</code>.
 *
 * <h4 id="_config_compiler_maven">Configuring options with Maven</h4>
 *
 * Example configuration with Maven:
 *
 * <pre class="language-xml">{@code
 * <plugin>
 *     <groupId>org.apache.maven.plugins</groupId>
 *     <artifactId>maven-compiler-plugin</artifactId>
 *     <version>3.8.1</version>
 *     <configuration>
 *         <source>17</source>
 *         <target>17</target>
 *         <annotationProcessorPaths>
 *             <path>
 *                 <groupId>io.jstach</groupId>
 *                 <artifactId>jstachio-apt</artifactId>
 *                 <version>${io.jstache.version}</version>
 *             </path>
 *         </annotationProcessorPaths>
 *         <compilerArgs>
 *             <arg>
 *                 -Ajstache.resourcesPath=src/main/resources
 *             </arg>
  *             <arg>
 *                 -Ajstache.debug=false
 *             </arg>
 *         </compilerArgs>
 *     </configuration>
 * </plugin>
 * }</pre>
 *
 * <h4 id="_config_compiler_gradle">Configuring options with Gradle</h4>
 *
 * Example configuration with Gradle:
 *
 * <pre><code class="language-kotlin">
 * compileJava {
 *     options.compilerArgs += [
 *     '-Ajstache.resourcesPath=src/main/resources'
 *     ]
 * }
 * </code> </pre>
 *
 *
 * </div>
 *
 * @author agentgt
 * @see JStachePath
 * @see JStacheFormatterTypes
 * @see JStacheConfig
 * @see JStacheFormatter
 * @see JStacheContentType
 * @see JStacheConfig
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
	 * name can be omitted. <code>model.getClass().getName()</code> +
	 * {@link JStacheName#DEFAULT_SUFFIX} name is used by default.
	 * @return Name of generated class
	 */
	String name() default "";

	/**
	 * An annotation processor compiler flag that says where the templates files are
	 * located.
	 * <p>
	 * When the annotation processor runs these files usually are in:
	 * <code>javax.tools.StandardLocation#CLASS_OUTPUT</code> and in a Maven or Gradle
	 * project they normally would reside in <code>src/main/resources</code> or
	 * <code>src/test/resources</code> which get copied on build to
	 * <code>target/classes</code> or similar. However due to incremental compiling
	 * template files are not always copied to <code>target/classes</code> and thus are
	 * not found by the annotation processor. To deal with this issue JStachio during
	 * compilation fallsback to direct filesystem access of the <em>source</em> directory
	 * instead of the output (<code>javax.tools.StandardLocation#CLASS_OUTPUT</code>) if
	 * the files cannot be found.
	 * <p>
	 * If the path does not start with a path separator then it will be appended to the
	 * the current working directory otherwise it is assumed to be a fully qualified path.
	 * <p>
	 * The default location is <code>CWD/src/main/resources</code> where CWD is the
	 * current working directory.
	 *
	 * <strong>If the option is blank or empty then NO fallback will happen and
	 * effectively disables the above behavior. </strong>
	 *
	 * You can change it by passing to the annotation processor a setting for
	 * {@value #RESOURCES_PATH_OPTION} like:
	 * <pre><code>jstache.resourcesPath=some/path</code></pre>
	 *
	 *
	 */
	public static final String RESOURCES_PATH_OPTION = "jstache.resourcesPath";

}
