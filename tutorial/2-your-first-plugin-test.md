# 2: Your First Plugin Test

- [Home](../README.md)
- [Previous](1-your-first-gradle-plugin.md)
- [Next](3-declaring-tasks-the-right-way.md)

Testing Gradle plugins is a task which must occur at multiple levels. Testing our simple plugin can only occur at the integration level today because of how its structured. 

In later steps we'll develop more decoupled plugin structures that can be unit tested, but for now we'll cover:

- How to create a test gradle project.
- How to include your plugin in a test gradle build.
- How to inspect the results of a test gradle build.

## Defining an Integration Test using ``GradleRunner``

Gradle in its newer versions contains a built in [test kit](https://docs.gradle.org/current/userguide/test_kit.html) which is extremely useful for this kind of testing. We will take advantage of the ``GradleRunner`` implementation from that testkit.

```groovy
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class TestRealBuild extends GroovyTestCase {
    def projectDir = new File(System.getProperty("user.dir") + "/testProjects/simpleProject")
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

    void testDealWithIt() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("dealwithit")
                .build()

        assertEquals(UP_TO_DATE, result.task(":dealwithit").getOutcome())
        assertTrue(result.output.contains("(•_•) ( •_•)>⌐■-■ (⌐■_■)"))
    }
}
```

You'll notice the project setup requires a few things to be functional, namely:

1. A test project with a ``build.gradle`` to execute.
2. A ``plugin-classpath.txt`` which adds our built plugin (which is under test) to the test project's classpath.

## Creating a Test Project

For the first piece of test infrastructure, we simply create a test project in the plugin's code tree. We will place this test project in ``testProjects/simpleProject`` and include a simple ``build.gradle`` in it:

```groovy
plugins {
    id "myplugin"
}
```

## Setting up ``plugin-classpath.txt``

This can be done by adding the following snippet to the plugin projects ``build.grade``.

```groovy
task createClasspathManifest {
    def outputDir = file("$buildDir/$name")

    inputs.files sourceSets.main.runtimeClasspath
    outputs.dir outputDir

    doLast {
        outputDir.mkdirs()
        file("$outputDir/plugin-classpath.txt").text = sourceSets.main.runtimeClasspath.join("\n")
    }
}

dependencies {
    testRuntime files(createClasspathManifest)
}
```

This instructs our plugin project to create the ``plugin-claspath.txt`` file before tests are run. It is now accessible to the test bench.

## Next Steps

As you can see, integration testing of plugins isn't the easiest thing. That said, integration testing is necessary in many cases. Hopefully this tutorial gives you some tools for implementing these integration tests. 

In the next step we'll present a better way to define tasks. This will allow us to build larger plugins that span multiple files.

