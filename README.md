Static mustache
===============

Logicless text templating engine.
Templates are compiled alone with Java-sources.
Value bindings are statically checked.

Features
--------

 * Logicless templating language.

 * [Mustache](http://mustache.github.io/) syntax.

 * Templates are compiled into effective code

 * Value bindings are statically checked.

 * Friendly error messages with context.

 * Zero configuration. No plugins or tweaks are required.
   Everything is done with standard javac with any IDE and/or build-system.

 * Non-HTML templates are supported. Set of supported formats is extensible.

Example
-------

### User.java ###

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
        this.array1 = array1;
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

### Rendering ###

New class `RenderableHtmlUserAdapter` will be mechanically generated with the above code. This class can be used to render template filled with actual data. To render template following code can be used:

```java
class Main {
    public static void main(String[] args) throws IOException {
        User user = new User("John Doe", 21, new String[] {"Knowns nothing"}, list);
        Renderable<Html> renderable = new RenderableHtmlUserAdapter(user);

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
/TODO: Examples of compilation error messages: field not found, unable to render type, etc/

Design
------

The idea is to create templating engine combining [mustache](http://mustache.github.io/) logicless philosophy
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

Current differences from mustache
---------------------------------

 * Lambdas are not supported
 * Partials are not supported
 * Delimiter redefinition is not supported

Interpretation of Java-types and values
---------------------------------------

See [original mustache manual](http://mustache.github.io/mustache.5.html).

When some value is null nothing is rendered for this mustache-variable or mustache-section anyway.

Boxed and unboxed booleans can be used for mustache-sections. Section is only rendered if value is true.

Arrays and Iterables can be used in mustache-sections and are treated like Javascript-arrays in original mustache.

Any non-null object can be used for mustache-section.
This object serves like a data-binding context for given section.

Data-binding contexts are nested.
Names are looked up in innermost context first.
If name is not found in current context, parent context is inspected.
This process continues up to root context.

/TODO: Detailed description of name lookup: method first, then getter, then field/

Primitive types and strings can be used in mustache-variables.

Escaping is always performed for mustache-variables.

Unescaped variables are supported analogues to original mustache.

Any boxed or unboxed primitive type is rendered with toString method.
Strings are rendered as is.

Rendering of other Java-types as mustache-variable is currently compile-time error.

License
-------

Static mustache is under BSD 3-clause license.

Flattr
------

[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=sviperll&url=https%3A%2F%2Fgithub.com%2Fsviperll%2Fstatic-mustache&title=static-mustache&language=Java&tags=github&category=software)

