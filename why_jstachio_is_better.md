# Why JStachio is Better than X

Where X is one of the following Java template engines:

* JSP
* [handlebars.java](https://github.com/jknack/handlebars.java)
* [JTE](https://github.com/casid/jte)
* [Rocker](https://github.com/fizzed/rocker)
* [Mustache.java](https://github.com/spullara/mustache.java)
* [Thymeleaf](https://www.thymeleaf.org/)


## Type Safe

JStachio is type safe.

The following are also type safe:

* JTE
* Rocker
* JSP (sort of)

JStachio does not use reflection and checks that all methods and fields exist at compile time.
JStachio treats the *"template as a contract"* and thus there is always root model class that is associated with
every template (ignoring partials).

Furthermore JStachio unlike the other compiled type safe engines does not automatically `toString` variables.
At *compile time* JStachio checks to see if the type is allowed to be "formatted". If it is not a compiler error
will happen.


Here is an example:

```java
@JStache(template = "{{foo}}") // the template can also be a file
public record Model(String foo, Object bar){}
```

If you mispell "foo" it would be a compiler error. If our template was `{{bar}}` a compiler
error would happen as `Object` is not allowed to be formatted (by default but is configurable).

Its unclear what if any safe guards JSP, JTE or Rocker have in preventing accidental `toString`.

Furthermore JStachio has an experimental feature do null checking only if the type is annotated
with `@Nullable` following JSpecify rules.


JStachio does its type checking and code generation with the Java annotation processor which is built into the JDK.
JTE and Rocker require pre-compiling with a build plugin. Thus you cannot precompile
easily if your build system is not supported. JTE, Rocker, and JSP do not actually read the symbolic tree of 
the Java models but rather just translate the code to Java. 

## Fast

JStachio is fast at regular String output as well as UTF-8 byte output.

The following is also fast:

* JTE
* Rocker

Below is UTF-8 byte output benchmarking:

![UTF-8 encode](https://github.com/agentgt/template-benchmark/blob/utf8/results-utf8.png?raw=true)

More explanation of the benchmarking is here:
https://github.com/agentgt/template-benchmark

JStachio is currently the fastest Java templating language I know of 
and I spent time trying to tune the others particularly for UTF-8 extended which is what
[TechEmpower benchmarks](https://www.techempower.com/benchmarks/) and not just plain latin1
which is what most template benchmarks test but is unrealistic because a single emoji anywhere in the output 
will slow String.getBytes().

## Language Specification 

JStachio is fully compliant with the Mustache v1.3 specification.

The following also have a language spec:

* Mustache.java - follows the same spec as JStachio but is less compliant in regards to whitespace
* handlebars.java - follows the handlebars.js spec
* JSP (and EL spec)
* Rocker (kind of because Razor + Java)


Languages that invent their own syntax are risky particulary if that syntax is complicated.
What happens if the language is no longer supported?

Mustache has been around for over 10 years and has implementations in multiple programming languages.

Java alone has 5 or so Mustache (like) implementations that are largely syntactically compatible:

 * Mustache.java
 * JMustache
 * Handlebars.java
 * Trimou

The semantics and syntax are so similar that JStachio uses JMustache to simulate "hot reload".

## Easy to learn

JStachio is easy to learn. It has excellent complete Javadoc. Mustache is also very easy to learn
and there are numerous examples of its use.

The following is also easy to learn:

* All the Mustache template engines 
* Rocker - because it is basically just Java
* JTE - sort because of its IntelliJ plugin

## Expressibility

Mustache is by design logicless and thus all the other Java Mustache-like implementations
are as well.

It is Mustache's greatest strength but often chided as its greatest weakness 
particularly when dealing with iterating over items and is one of the reasons Handlebars.js was created.

However with recent advancements in the Mustache spec most problems can be solved
with what Mustache calls lambdas. Out of all the implementations JStachio makes this
very easy by just annotating a method.


## Editor support

Mustache is supported in most editors. 

The following has editor support as well:

* JTE - (only IntelliJ)
* JSP - (all the major Java IDEs and editors)
* Thymeleaf 
* Handlebars.java

If there is one feature that JStachio does not have it is IDE auto-completion and refactoring. Only JSP
appears to have that support across multipe IDEs. JTE has support in Intellij.

In practice this is not much of a problem being JStachio encourages a model class with every template which is a good practice
one can focus on keeping the name of fields short and specific to the context. 
