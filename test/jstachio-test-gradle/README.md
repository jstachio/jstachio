
Credit to @hrstoyanov for putting this together

# Issue #201
I marked some section in **app/build.gradle** and **gradle.properties** with 'Comment me!!!'. 
Try commenting them out, change the test.tpl, sun the app,  and see the effect.

You need to have Java 17+ installed, check you JAVA_HOME.

Open this as a Gradle project in IntelliJ and use the 'Run anything' to run gradle with these commands (or just use terminal):

To run the app (Mac/Linux)
> ./gradlew run

To run the app (Windows)
> gradle run

Show dependencies tree:
> ./gradlew :app:dependencies
