adt4j - Algebraic Data Types for Java
=====================================

This library implements [Algebraic Data Types](http://en.wikipedia.org/wiki/Algebraic_data_type) for Java.
ADT4J provides annotation processor for @GenerateValueClassForVisitor annotation.
ADT4J generates new class for each @GenerateValueClassForVisitor annotation.

It allows you to easily define custom data types. Like this:

```java
    // Define Expression data type
    @GenerateValueClassForVisitor(resultVariableName="R",
                                  selfReferenceVariableName="E")
    interface ExpressionVisitor<E, R> {
        @GeneratePredicate("isLiteral");
        R lit(int i);

        R sum(@Getter("leftOperand") E e1, @Getter("rightOperand") E e2);
        R mul(@Getter("leftOperand") E e1, @Getter("rightOperand") E e2);
    }
```

And that's it. `Expression` class will be generated and you'll be able to define expressions like this:

```java
    import static ...Expression.*;

    /* ... */

    Expression e = mul(sum(lit(5), lit(1)), lit(2));
```

You can process expressions with "pattern-matching" a. k. a. visitor-pattern:

```java
    int value = e.accept(new ExpressionVisitor<Expression, Integer>() {
        @Override
        public Integer int(int i) {
            return i;
        }
        @Override
        public Integer sum(Expression e1, Expression e2) {
            return e1.accept(this) + e2.accept(this);
        }
        @Override
        public Integer mul(Expression e1, Expression e2) {
            return e1.accept(this) * e2.accept(this);
        }
    });
```

Features
--------

 * Support recursive data types
 * Generate hashCode, equals and toString implementations with value semantics
 * Generate predicates, getters and "updaters" with additional annotations
 * Optionally generate Comparable implementation with presise compile-time type-check if it is possible
 * Optionally generate serializable classes with presise compile-time type-check if it is possible
 * Sensible error messages
 * Support generated class extention through standard Java's inheritance.

License
-------

ADT4J is under BSD 3-clause license.

Flattr
------

[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=sviperll&url=https%3A%2F%2Fgithub.com%2Fsviperll%2Fadt4j&title=adt4j&language=Java&tags=github&category=software)

Installation
------------

Use maven dependency to use ADT4J:

```xml
    <dependency>
        <groupId>com.github.sviperll</groupId>
        <artifactId>adt4j</artifactId>
        <version>0.12</version>
    </dependency>
```

Changelog
---------

Since 0.12

 * Fix recursive types support (see `TreeVisitor` example)
 * Fix varargs support
 * Code cleanup
 * Rename `adt4j-core` artifact to `adt4j`

Since 0.11

 * Nullable and Nonnull annotations on generated methods
 * No warnings from generated code

Since 0.10

 * Use maven as build system

Since 0.9

 * Predicates can be generated to test for specific case
 * Comparable instances can be generated

Build
-----

    $ git clone git@github.com:sviperll/adt4j.git
    $ cd adt4j
    $ mvn test

Check for errors and warnings.

ADT4J is built to be compatible with Java 6.
Reference to JDK6 is required even if you use Java 7 or 8 compiler.
ADT4J is built with boot class path pointing to JDK6
to prevent usage of some standard library routines missing from Java 6.

To install adt4j run

    $ JDK6_HOME=<path-to-your-jdk-root> mvn clean install

For example:

    $ JDK6_HOME=/usr/lib/jvm/java-6-openjdk-i386 mvn clean install

It is recomended to compile without JDK6_HOME variable during development and
to compile with JDK6_HOME only when all errors and warnings are cleared.

When you compile without JDK6_HOME maven uses Java compiler API to compile project.
When you compile with JDK6_HOME maven always forks javac executable.

Forking javac is slow. Another drawback is error messages. When compiler API is used
all compiler messages are passed back to maven and correctly printed.
When javac executable is used maven has to process and parse it's output, this
process is prone to errors and causes several warnings not to be outputed...

Usage
-----

We will use an implemetation of optional-type similar to `Optional` class provided by Java 8 as
an example of ADT4J usage.

 1. Define an interface that will describe your Algebraic Data Type like this:

    ```java
        interface OptionalVisitor<T, R> {
            R present(T value);
            R missing();
        }
    ```

    You must define a variation of visitor interface (see Visitor-pattern).
    This interface is a discription of your data-type.
    Each method represents one possible case or pattern.
    Arguments of methods represent data stored in your data-type.

    There are two cases in an example above:

     1. `present` - when optional data of type `T` is present.
     2. `missing` - when no data is present.

    All methods in your interface are to return the same type declared as type-variable
    (`R` in the example above).
    Methods can have any number of any arguments.
    Additional type variables are allowed.
    Methods should not throw any checked exceptions.

 2. Add a `@GenerateValueClassForVisitor` and specify special type-variable names in arguments to annotation.

    ```java
        @GenerateValueClassForVisitor(resultVariableName = "R")
        interface OptionalVisitor<T, R> {
            R present(T value);
            R missing();
        }
    ```

    Here we declare that type-variable `R` is used as a result-type of all interface methods.

    Note: you should always add @Nullable annotation to make any field nullable. Otherwise null checks are
    generated and exception is thrown upon construction:

    ```java
        @GenerateValueClassForVisitor(resultVariableName = "R")
        interface Record<T, R> {
            R valueOf(T mandatory1, Object mandatory2, @Nullable Object optional);
        }
    ```

 3. We are ready to go.

    New class `Optional` will be automatically generated when you compile your project.

    You can create instances of this class like this:

    ```java
        Optional<String> optional1 = Optional.present("Test");
        Optional<String> optional2 = Optional.missing();
    ```

    You can use visitors as pattern-matching construct:

    ```java
        OptionalVisitor<String, Void> printVisitor = new OptionalVisitor<>() {
           public Void present(String value) {
               System.out.println("present: " + value);
               return null;
           }
           public Void missing() {
               System.out.println("missing");
               return null;
           }
        }

        System.out.println("optional1:");
        optional1.accept(printVisitor);
        System.out.println("optional2:");
        optional2.accept(printVisitor);
    ```

    The result should be like this:

        optional1:
        present: Test
        optional2:
        missing

    Generated class contains correct `equals` and `hashCode` method impelementation to support
    value-semantics of generated class.

    Generated class name is chosen by removing `Visitor`-suffix from visitor-interface name.
    If visitor-interface name doesn't end with `Visitor`, `Value`-suffix is appended
    to visitor-interface name to form generated class name.

    You can customize class name with additional arguments. Like this:

    ```java
        @GenerateValueClassForVisitor(resultVariableName = "R",
                                      valueClassName = "OptionalBase",
                                      valueClassIsPublic = false)
        interface OptionalVisitor<T, R> {
            R present(@Nonnull T value);
            R missing();
        }
    ```

    In the example above `OptionalBase` class will be generated instead of `Optional`.

    You can extend generated class to add more methods like this:

    ```java
        public class MyOptional<T> extends OptionalBase<T> {
            public static <T> MyOptional<T> missing() {
                return new MyOptional<>(OptionalBase.missing());
            }

            public static <T> MyOptional<T> present(T value) {
                return new MyOptional<>(OptionalBase.present(value));
            }

            private MyOptional(OptionalBase<T> value) {
                // protected constructor from OptionalBase class
                super(value);
            }

            //
            // equals and hashCode are correctly inherited from OptionalBase
            //

            public <U> MyOptional<U> flatMap(final Function<T, MyOptional<U>> function) {
                return accept(new OptionalVisitor<T, MyOptional<U>>() {
                    @Override
                    public MyOptional<U> missing() {
                        return MyOptional.missing();
                    }

                    @Override
                    public MyOptional<U> present(T value) {
                        return function.apply(value);
                    }
                });
            }
        }
    ```

    Now you have `MyOptional` class similar to `Optional` class provided by Java 8.

    You can use it to chain optional operations:

    With Java 8 syntax:

    ```java
        lookup(key1).flatMap((key2) -> lookup(key2));
    ```

    or with anonymous classes:

    ```java
        lookup(key1).flatMap(new Function<String, MyOptional<String>>() {
                public MyOptional<String> apply(String key2) {
                    return lookup(key2);
                }
            });
        }
    ```

    See adt4j-examples project for more complete examples.
