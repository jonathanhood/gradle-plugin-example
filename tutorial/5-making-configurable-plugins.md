# 5: Making Configurable Plugins

- [Home](../README.md)
- [Previous](4-making-unit-testable-plugins.md)

When applying a plugin to a lot of projects, inevitably something will be different between projects.  An artifact name will change, some behavior will need to be different, and a multitude of other things. Luckily Gradle provides an ``extensions`` infrastructure to support this configurability.

In this tutorial we will cover:

- How to define an extension for our plugin.
- How users can work with the extension.
- How a tasks can read the extension's content.
- How to test our plugin based on configuration from an extension.

## Defining an Extension

Extensions are simple groovy classes with default values. Their definition is very simple. They are just plain POGO classes.

```groovy
package com.jhood

class MyPluginExtension {
    String fileContent = "¯\\_(ツ)_/¯"
}
```

In the above case we've defined a simple extension with one configurable item ``fileContent``. Adding the extension to the gradle project is also very simple.

``` groovy
package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
		project.extensions.add("myplugin", MyPluginExtension)
    }
}
```

This should only be done once and must be done before project evaluation. 

## Deep(ish) Dive: Gradle Project Lifecyle

Now you immediately ask: what is project evaluation? It might help to review Gradle's documentation on the [build lifecyle](https://docs.gradle.org/current/userguide/build_lifecycle.html) or the even better documentation as part of the [Project groovy docs](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html).

The simple way to state it is, projects have two fundamental times where we can manipulate them: 
- ``beforeEvaluate`` occurs during initial plugin execution but before any configuration is read or dependencies evaluated. This is the time when most tasks will get constructed. So far, most of the plugin code we've written will execute at this stage. At this point user configuration should not be read since it hasn't be pulled from the ``build.gradle`` yet.
- ``afterEvaluate`` occurs after all configuration and dependencies have been evaluated. At this point many parts of the project structure (such as configurations and dependencies) are rendered immutable. We may also read configuration with assurance that values are set as the user intends.

After all ``afterEvaluate`` actions have been handled is when a gradle project actual begins to execute all tasks. 

It should be noted that any ``@TaskAction`` handler occurs after evaluation is completed. As a result, reading configuration in these functions is acceptable.

## Reading the extension's configuration

Using an extension is pretty easy assuming we've followed the rules about project lifecycles. To state it again:

- **Do not read extension data unless you are in an ``afterEvaluate`` block or in a tasks ``@TaskAction`` handler.**

We can show this in action:

```groovy

package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
		project.extensions.add("myplugin", MyPluginExtension)

		project.afterEvaluate {
			println(project.extensions.myplugin.fileContent)
		}
    }
}

```

That's not very useful though. Let's use the configuration from within a task.

```groovy
package com.jhood

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MyTask extends DefaultTask {
    File outputFile = new File(project.buildDir, "myfile.txt")

    @TaskAction
    def action() {
        outputFile.parentFile.mkdirs()
        outputFile.createNewFile()
        outputFile.text = project.extensions.myplugin.fileContent
    }
}
```

That's a little more useful. If we wanted to make this unit testable, you can imagine that we'd modify ``FileCreator`` to take the file content as an argument, and pass that configuration in from the task that constructs it. We'll leave that as an excercise for the reader.

## Setting Configuration in ``build.gradle``

Using the new extension should be familiar to all gradle users. You probably didn't know it, but you've been interacting with extensions all along when using the many built in gradle plugins.

```groovy
apply plugin: "myplugin"

buildscript {
  repositories {
    mavenLocal()
  }
  dependencies {
    classpath "com.jhood:myplugin:1.+"
  }
}

myplugin {
  fileContent = "OMGWTFBBQ"
}

```

If the ``myplugin`` block isn't placed in the ``build.gradle`` then the default values taken from the extension will be used.

## Testing with the Extension

The easiest test we can write involves asserting that the extension is registered with correct default values.

```groovy
import com.jhood.MyPluginExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import com.jhood.MyPlugin

class TestMyPlugin extends GroovyTestCase {
    void testHasExtension() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin
        assertTrue(project.extensions.myplugin instanceof MyPluginExtension)
        assertEquals("¯\\_(ツ)_/¯", project.extensions.myplugin.fileContent)
    }
}
```

The only other test we can write is an integration test which asserts that the ``myplugin`` block is read correctly from the ``build.gradle``.  We use the following ``build.gradle`` in a new test project ``configuredProject``.

```groovy
plugins {
    id "myplugin"
}

myplugin {
    fileContent = "CONFIGURED"
}
```

We can then write the following integration test:

```groovy
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestRealBuild extends GroovyTestCase {
    def configuredProjectDir = new File(System.getProperty("user.dir") + "/testProjects/configuredProject")
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

    void setUp() {
        def configuredBuildDir = new File(configuredProjectDir, "build")
        if(configuredBuildDir.exists()) configuredBuildDir.deleteDir()
        configuredBuildDir.delete()
    }

    void tearDown() {
        setUp()
    }

    void testConfiguration() {
        def testFile = new File(configuredProjectDir, "build/myfile.txt")

        def result = GradleRunner.create()
                .withProjectDir(configuredProjectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("mytask")
                .build()

        assertEquals(SUCCESS, result.task(":mytask").getOutcome())
        assertTrue(testFile.exists())
        assertEquals("CONFIGURED", testFile.text)
    }
}
```

## Next Steps

In this tutorial we've shown how-to make our plugins configurable by using an extension. We can now write general purpose plugins whose behavior can be changed based on the need of the user.

Hopefully by this point you've gotten a nice tour of the various gradle plugin mechanisms. There is way more you can do with plugins, I suggest referring to the [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide) and [Gradle API Reference](https://docs.gradle.org/current/javadoc/) to get a feel for what you can do with your plugins. 

You may also fork this project and use it as a starting point for new plugins. This project was used to build many of the examples in the tutorial, so you should find its code familiar.

