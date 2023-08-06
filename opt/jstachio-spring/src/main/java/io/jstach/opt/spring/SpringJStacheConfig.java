package io.jstach.opt.spring;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;
import io.jstach.jstache.JStachePath;

/**
 * Static config for Spring Boot like conventions of finding templates in
 * <code>classpath:/templates</code>.
 * <p>
 * Place the below on a <code>package-info.java</code> where your JStache models are or if
 * in a modular environment <code>module-info.java</code>
 * <pre><code class="language-java">
 * &#64;JStacheConfig(using = SpringJStacheConfig.class)
 * &#47;&#47; some class, package-info, module-info
 * </code> </pre>
 *
 * Assuming you have a JStache model with {@linkplain JStache#path() path of}
 * "<code>MyView</code>" the resolved template location would be
 * <code>src/main/resources/templates/MyView.mustache</code>.
 *
 * @author agentgt
 * @apiNote This annotation does not have a dependency on Spring Boot and maybe used
 * without it
 */
@JStacheConfig(pathing = @JStachePath(prefix = "templates/", suffix = ".mustache"))
@JStacheFlags(flags = Flag.CONTEXT_SUPPORT)
public enum SpringJStacheConfig {

}
