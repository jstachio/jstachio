[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jstach/jstachio/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.jstach/jstachio)
[![Javadoc](https://javadoc.io/badge/io.jstach/jstachio.svg)](https://javadoc.io/doc/io.jstach/jstachio/latest)
[![Github](https://github.com/jstachio/jstachio/actions/workflows/maven.yml/badge.svg)](https://github.com/jstachio/jstachio/actions)

<img src="etc/social-media.svg" alt="jstachio">


A typesafe Java Mustache templating engine.

Templates are compiled into readable Java source code and value bindings are statically checked.

(formerly called [static-mustache](https://github.com/sviperll/static-mustache))

## Features

 * Logicless [Mustache (v 1.3)](https://github.com/mustache/spec) syntax.

   * Full support of non-optional Mustache spec v1.3.0 requirements (including whitespace)
   * Optional inheritance support with some caveats
   * Optional lambda support with some differences due to static nature
 
 * Get [JEP 430](https://openjdk.org/jeps/430) like support today but wth even more power.
 
 * Templates are compiled into Java code

 * Value bindings are statically checked.

 * Methods, fields and getter-methods can be referenced in templates.

 * Friendly error messages with context.

 * Zero configuration. No plugins or tweaks are required.
   Everything is done with standard javac with any IDE and/or build-system.
 
 * Non-HTML templates are supported. Set of supported escaping content types is extensible.
 * Layouts are supported via the Mustache inheritance spec.
 * Fallback render service extension point via ServiceLoader

   * Seamlessly Fallback to reflection based runtime rendering via [JMustache](https://github.com/samskivert/jmustache) and [mustache.java](https://github.com/spullara/mustache.java) (useful for development and changing templates in real time)
   * If you are not a fan of generated code you can still use JStachio to type check your mustache templates.
 
 * Customize allowed types that can be outputted otherwise compiler error (to avoid toString on classes that do not have a friendly toString).
 * Formatter for custom `toString` of variables at runtime
 * Add extra `implements` interfaces to generated code for trait like add ons (`@JStacheInterfaces`)
 * Powerful Lambda support
 * `Map<String, ?>` support
 * `Optional<?>` support
 * Compatible with [JMustache](https://github.com/samskivert/jmustache#-first-and--last) and [Handlebars](https://handlebarsjs.com/api-reference/data-variables.html#root) list index extensions (like `-first`, `-last`, `-index`)
 * It is by far the [fastest Java Mustache-like template engine as well one of the fastest in general](#performance).
 * Zero dependencies other than JStachio itself

    * An absolutely zero runtime dependency option is in the works (as in all the code needed is generated and not even jstachio is needed during runtime). No need to use Maven shade for annotation processors and other zero dep projects.

## Documentation

[Latest JStachio API javadoc](https://jstach.io/jstachio/)

The doc is also on javadoc.io but is not aggregated like the above.


## Quick Example

```java

@JStache(template = """
        {{#people}}
        {{message}} {{name}}! You are {{#ageInfo}}{{age}}{{/ageInfo}} years old!
        {{#-last}}
        That is all for now!
        {{/-last}}
        {{/people}}
        """)
public record HelloWorld(String message, List<Person> people) implements AgeLambdaSupport {
}

public record Person(String name, LocalDate birthday) {
}

public record AgeInfo(long age, String date) {
}

public interface AgeLambdaSupport {

    @JStacheLambda
    default AgeInfo ageInfo(Person person) {
        long age = ChronoUnit.YEARS.between(person.birthday(), LocalDate.now());
        String date = person.birthday().format(DateTimeFormatter.ISO_DATE);
        return new AgeInfo(age, date);
    }

}

@Test
public void testPerson() throws Exception {
    Person rick = new Person("Rick", LocalDate.now().minusYears(70));
    Person morty = new Person("Morty", LocalDate.now().minusYears(14));
    Person beth = new Person("Beth", LocalDate.now().minusYears(35));
    Person jerry = new Person("Jerry", LocalDate.now().minusYears(35));
    String actual = JStachio.render(new HelloWorld("Hello alien", List.of(rick, morty, beth, jerry)));
    String expected = """
            Hello alien Rick! You are 70 years old!
            Hello alien Morty! You are 14 years old!
            Hello alien Beth! You are 35 years old!
            Hello alien Jerry! You are 35 years old!
            That is all for now!
                            """;
    assertEquals(expected, actual);

}
```

## Installation


### Maven


```xml
<properties>
    <io.jstach.version>0.6.0-SNAPSHOT</io.jstach.version>
</properties>
...
<dependencies>
    <dependency>
        <groupId>io.jstach</groupId>
        <artifactId>jstachio</artifactId>
        <version>${io.jstach.version}</version>
    </dependency>
</dependencies>
...
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>17</source> <!-- 17 is the minimum -->
                <target>17</target> <!-- 17 is the minimum -->
                <annotationProcessorPaths>
                    <path>
                        <groupId>io.jstach</groupId>
                        <artifactId>jstachio-apt</artifactId>
                        <version>${io.jstach.version}</version>
                    </path>
                    <!-- other annotation processors -->
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

*N.B. The annotations jar (jstachio-annotation) is pulled in transitively*

### Gradle

```gradle
dependencies {
 
    implementation 'io.jstach:jstachio:VERSION'
 
    annotationProcessor 'io.jstach:jstachio-apt:VERSION'
}
```



Examples
--------


### user.mustache ###

```hbs
{{#name}}
<p>Name: {{.}}, Name Length is {{length}}</p>
{{/name}}

<p>Age: {{  age  }}</p>

<p>Achievements:</p>

<ul>
{{#array}}
  <li>{{.}}</li>
{{/array}}
</ul>

{{^array}}
<p>No achievements</p>
{{/array}}

<p>Items:</p>

<ol>
{{#list1}}
  <li>{{value}}</li>
{{/list1}}
</ol>
```

### User.java

Following class can be used to provide actual data to fill into above template.

```java
@JStache(
    // points to src/main/resources/user.mustache file
    path = "user.mustache",
   
    // or alternatively you can inline the template
    template = "", 

    )
public record User(String name, int age, String[] array, List<Item<String>> list) {

   public static class Item<T> {
        private final T value;
        public Item(T value) {
            this.value = value;
        }
        T value() {
            return value;
        }
    }
}
```


### Rendering

New class `UserRenderer` will be mechanically generated with the above code. 
This class can be used to render template filled with actual data. To render template following code can be used:

```java
class Main {
    public static void main(String[] args) throws IOException {
        User user = new User("John Doe", 21, new String[] {"Knowns nothing"}, list);
        StringBuilder appendable = new StringBuilder();
        JStachio.render(user, appendable);
    }
}
```

The result of running this code will be

```html
<p>Name: John Doe, Name Length is 8</p>

<p>Age: 21</p>

<p>Achievements:</p>

<ul>
  <li>Knowns nothing</li>
</ul>

<p>Items:</p>

<ol>
  <li>helmet</li>
  <li>shower</li>
</ol>
```

Referencing non existent fields, or fields with non renderable type, all result in compile-time errors. These errors are reported at your project's compile-time alone with other possible errors in java sources.

```
target/classes/user.mustache:5: error: Field not found in current context: 'age1'
  <p>Age: {{  age1  }} ({{birthdate}}) </p>
                  ^
  symbol: mustache directive
  location: mustache template
```

```
target/classes/user.mustache:5: error: Unable to render field: type error: Can't render data.birthdate expression of java.util.Date type
  <p>Age: {{  age  }} ({{birthdate}}) </p>
                                    ^
  symbol: mustache directive
  location: mustache template
```

See `test/examples` project for more examples.


## Current differences from mustache spec

 * Delimiter redefinition is not supported
 * Whitespace in block tags is explicit (currently the [spec is ill-defined on this](https://github.com/mustache/spec/pull/131)) 
 * Inheritance block scoping is eager: https://github.com/mustache/spec/pull/129
   * I hope to fix that soon

## Java specific extensions

### Enum matching support

Basically enums have boolean keys that are the enums name (`Enum.name()`) that can be used as conditional sections.

Assume `light` is an enum like:

```java
public enum Light {
  RED,
  GREEN,
  YELLOW
}
```

You can conditinally select on the enum like a pattern match:

```hbs
{{#light.RED}}
STOP
{{/light.RED}}
{{#light.GREEN}}
GO
{{/light.GREEN}}
{{#light.YELLOW}}
Proceeed with caution
{{/light.YELLOW}}
```

### Index support

JStachio is compatible with both handlebars and JMustache index keys for iterable sections.

* `-first` is boolean that is true when you are on the first item
* `-last` is a boolean that is true when you are on the last item in the iterable
* `-index` is a one based index. The first item would be `1` and not `0`

### Lambda support

JStachio supports lambda section calls in a similar manner to JMustache. Just tag your methods
with `@JStacheLambda` and the returned models will be used to render the contents of the lambda section.
The top of the context stack can be passed to the lambda.

JStachio unlike the spec does not support returning dynamic templates that are then rendered against the context stack.
However dynamic output can be achieved by the caller changing the contents of the lambda section as the contents of the
section act as an inline template.

## Performance

*It is not a goal of this project to be the fastest templating engine*.

Not that peformance matters much with templating languges 
(it is rarely the bottleneck) but JStachio is very fast and is basically equivalent to jte.

![Template Comparison](https://github.com/agentgt/template-benchmark/raw/master/results.png)

The one called Manual is as the name implies. Raw Java code.

JStachio has to do a lot of null checking for falsey and from my testing that is what is just ever so slighly making it slower than the fastest. I hope to add `@Nullable` annotation checking in the future but not really for performance.

Design
------

The idea is to create templating engine combining [mustache](https://jgonggrijp.gitlab.io/wontache/mustache.5.html) logicless philosophy
with Java's single responsibility and static-typing.
Full compile-time check of syntax and data-binding is the main requirement.

Currently Java-code is generated for templates. Generated Java-code should never fail to compile.
If it is impossible to generate valid Java-code from some template,
friendly compile-time error pointing to template file should be generated.
Users should never be exposed to generated Java-code.

Original mustache uses Javascript-objects to define rendering context.
Fields of selected Javascript-objects are binded with template fields.

Static mustache uses Java-objects to define rendering context.
Binding of template fields is defined and checked at compile-time.
Missing fields are compile-time error.
 

Interpretation of Java-types and values
---------------------------------------

See [mustache manual (v 1.3)](https://jgonggrijp.gitlab.io/wontache/mustache.5.html) .

When some value is null nothing is rendered if it is used as a section.
If some value is null and it is used as a variable a null pointer exception will be thrown by default.
This is configurable via `@JStacheFormatterTypes` and custom `Formatters`.

Boxed and unboxed `boolean` can be used for mustache-sections. Section is only rendered if value is true.

`Optional<?>` empty is treated like an empty list or a boolean false. Optional values are always assumed to be non null.

Arrays and Iterables can be used in mustache-sections and are treated like Javascript-arrays in original mustache.

`Map<String,?>` follow different nesting rules than other types. If you are in a `Map` nested section
the rest of the context is checked before the `Map`. Once that is done the Map is then checked. 

Any non-null object can be used for mustache-section.
This object serves like a data-binding context for given section.

Data-binding contexts are nested.
Names are looked up in innermost context first.
If name is not found in current context, parent context is inspected.
This process continues up to root context.

In each rendering context name lookup is performed as following.
Lookup is performed in Java-class serving as a rendering context.

 1. Method with requested name is looked up.
    Method should have no arguments and should throw no checked exceptions.
    If there is such method it is used to fetch actual data to render.
    Compile-time error is raised if there is method with given name, but
    it is not accessible, has parameters or throws checked exceptions.

 2. Method with getter-name for requested name is looked up.
    (For example, if 'age' is requested, 'getAge' method is looked up.)
    Method should have no arguments and should throw no checked exceptions.
    If there is such method it is used to fetch actual data to render.
    Compile-time error is raised if there is method with such name, but
    it is not accessible, has parameters or throws checked exceptions.

 3. Field with requested name is looked up.
    Compile-time error is raised if there is field with such name but it's not accessible.

 4. Search continues in parent-context if all of the above failed.

Primitive types and strings can be used in mustache-variables.

Escaping is always performed for mustache-variables.

Unescaped variables are supported as in original mustache.

Any boxed or unboxed primitive type is rendered with toString method.
Strings are rendered as is.

Rendering of other Java-types as mustache-variable is currently compile-time error.


License
-------

JStachio is under BSD 3-clause license.
