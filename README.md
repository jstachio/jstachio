Static mustache
===============

A typesafe Java Mustache templating engine.

Templates are compiled into readable Java source code and value bindings are statically checked.

**N.B. this is the SnapHop fork of https://github.com/sviperll/static-mustache .**

**This project will be renamed soon and heavily refactored. Updated link to come soon.**

Features
--------

 * Logicless templating language.

 * [Mustache (v 1.3)](https://github.com/mustache/spec) syntax.

 * Templates are compiled into effective code

 * Value bindings are statically checked.

 * Methods, fields and getter-methods can be referenced in templates.

 * Friendly error messages with context.

 * Zero configuration. No plugins or tweaks are required.
   Everything is done with standard javac with any IDE and/or build-system.

 * Non-HTML templates are supported. Set of supported formats is extensible.

 * Layouts are supported, i. e. generation of header and footer from one template.
 
Installation
------------

Use maven dependency:

```xml
    <dependency>
        <groupId>com.snaphop.staticmustache</groupId>
        <artifactId>static-mustache</artifactId>
        <version>(see release tags)</version>
    </dependency>
```




SnapHop additions
-----------------

 * Full support of non-optional Mustache spec v1.3.0 requirements (including whitespace)
   * Optional inheritance support with some caveats
   * Optional lambda support with some differences due to static nature
 * RenderService extension point via ServiceLoader
 * Formatter for custom `toString` of variables
 * Add extra `implements` interfaces to generated code for trait like add ons (`@TemplateInterface`)
 * Compound dotted path like variables similar to Handlebars.
 * `Map<String, ?>` support
 * `Optional<?>` support
 * Customize allowed types that can be outputted otherwise compiler error (to avoid toString on classes that do not have a friendly toString).

Example
-------

### user.mustache ###

```
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

### User.java ###

Following class can be used to provide actual data to fill into above template.

```java
@GenerateRenderableAdapter(
    // points to src/main/resources/user.mustache file
    template = "user.mustache",

    // adapterName can be omitted. "Renderable{{className}}Adapter" name is used by default
    adapterName = "RenderableHtmlUserAdapter")
public class User {
    final String name;
    final int age;
    final String[] array;
    final List<Item<String>> list1;

    public User(String name, int age, String[] array, List<Item<String>> list1) {
        this.name = name;
        this.age = age;
        this.array = array;
        this.list1 = list1;
    }

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


### Rendering ###

New class `RenderableHtmlUserAdapter` will be mechanically generated with the above code. This class can be used to render template filled with actual data. To render template following code can be used:

```java
class Main {
    public static void main(String[] args) throws IOException {
        User user = new User("John Doe", 21, new String[] {"Knowns nothing"}, list);
        Renderable<Html> renderable = RenderableHtmlUserAdapter.of(user);

        // Any appendable will do: StringBuilder, Writer, OutputStream
        Renderer renderer = renderable.createRenderer(System.out);

        // Write rendered template
        renderer.render();
    }
}
```

The result of running this code will be

```
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

See `static-mustache-examples` project for more examples.

Current differences from mustache
---------------------------------

 * Lambdas are not supported (yet)
 * Delimiter redefinition is not supported
 * Whitespace in block tags is explicit (currently the [spec is ill-defined on this](https://github.com/mustache/spec/pull/131)) 

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

~~When some value is null nothing is rendered for this mustache-variable or mustache-section anyway.~~ Configurable via `@TemplateFormatterTypes` as well as the RenderService SPI.

Boxed and unboxed booleans can be used for mustache-sections. Section is only rendered if value is true.

Arrays and Iterables can be used in mustache-sections and are treated like Javascript-arrays in original mustache.

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

Static mustache is under BSD 3-clause license.
