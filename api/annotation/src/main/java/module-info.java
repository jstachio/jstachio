/**
 * JStachio compile time annotations.
 * <p>
 * <em>This module ONLY has annotations, constants, and enums.</em>
 * 
 * <h2>Example usage</h2>
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
 * </code>
 * </pre>
 * 
 * @author agentgt
 *
 */
module io.jstach.jstache {
	exports io.jstach.jstache;
}