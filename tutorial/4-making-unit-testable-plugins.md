# 4: Making Unit Testable Plugins


- [Home](../README.md)
- [Previous](3-declaring-tasks-the-right-way.md)
- [Next](5-making-configurable-plugins.md)

At this point we've created plugins, added tasks, and created integration tests. We have a learned some techniques already that allow us to build useful plugins. There is a big problem though, as your plugin grows integration tests will take a long time. This can make it painful to work in the plugin as its feature set grows. We can solve that problem by applying some heathly software practices to create decoupled and unit testable plugins.

In this tutorial we will cover:

- How to define a task implementation in away that allows unit testing.
- How to write some fast and simple unit tests.

## The Problem with Gradle Tasks

At the end of the day, a gradle task requires a project to run. Creating a project from within a test infrastructure is difficult as we've already seen. In order to work around this problem we will seperate our implementation from the gradle plugin infrastructure. We'll test the implementation using normal unit testing, and test the ties to the gradle infrastructure using the mechanisms already described.

Let's take the following task from our previous tutorial:

``` groovy
package com.jhood

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MyTask extends DefaultTask {
    File outputFile = new File(project.buildDir, "myfile.txt")

    @TaskAction
    def action() {
        outputFile.parentFile.mkdirs()
        outputFile.createNewFile()
        outputFile.text = "HELLO FROM MY PLUGIN"
    }
}
```

The primary problem here is that the task extends ``DefaultTask`` which is hard to construct without a fully defined ``Project``. The implementation of the task only depends on one thing from gradle itself though - ``project.buildDir``. The solution then, is simple. We'll define the implementation of the task as a new class which doesn't depend on ``Project`` and then make the task construct and use the implementation.

## Creating a Task Implementation Class

Let's create the following new class:

``` groovy
package com.jhood.impl

class FileCreator {
    File outputFile
    
    FileCreator(File outputFile) {
        this.outputFile = outputFile
    }
    
    def create() {
        this.outputFile.parentFile.mkdirs()
        this.outputFile.createNewFile()
        this.outputFile.text = "HELLO FROM MY PLUGIN"
    }
}
```

Let's take a minute to point out a few important things:

1. There are no dependencies on Gradle. In fact this only uses standard Groovy constructs. This will be easy to write tests for.
2. We can trivially construct this inside our task definition. Users won't notice a difference.

## Making our Tasks Simpler

Our task implementation now simplified to:

``` groovy
package com.jhood

import com.jhood.impl.FileCreator
import org.gradle.api.tasks.TaskAction

class MyTestableTasik extends DefaultTask {
    File outputFile = new File(project.buildDir, "myfile.txt")

    @TaskAction
    def action() {
        def creator = new FileCreator(outputFile)
        creator.create()
    }
}
```

When this happens, the job of the task class is just to:

1. Expose configuration to the user where necessary.
2. Define a ``@TaskAction`` handler.

This is really nice. It's not such a big deal now that we can't unit test the implementation of the task itself because its job is very simple.

## Testing the ``FileCreator``

Lets now test our ``FileCreator`` using a unit test bench.

``` groovy
package impl

import com.jhood.impl.FileCreator

class TestFileCreator extends GroovyTestCase {
    void testCreatesFileWithContent() {
        def tempFile = File.createTempFile("temp", ".tmp")
        def creator = new FileCreator(tempFile)
        creator.create()
        assertTrue(tempFile.exists())
        assertEquals("HELLO FROM MY PLUGIN", tempFile.text)
    }

    void testCreatesFileIfParentDirMissing() {
        def tempDir = File.createTempDir()
        def tempFile = new File(tempDir, "testing.tmp")
        tempDir.delete()

        def creator = new FileCreator(tempFile)
        creator.create()
        assertTrue(tempFile.exists())
        assertEquals("HELLO FROM MY PLUGIN", tempFile.text)
    }
}
```

Very quickly we can now build up coverage around the corner cases of our implementation. It helps that groovy's built in ``GroovyTestCase`` provides a really nice integrated unit test infrastructure.

## Next Steps

In this tutorial we've learned to seperate our plugin implementation from the actual Gradle infrastructure. This allows us to leverage unit testing capabilities available in groovy rather than relying on full builds and integration tests.

In the next tutorial we'll cover how-to make our plugin configuration using Gradle extensions. We'll also cover an important project lifeclye event - the project evaluation - which is required to read that configuration from a user's ``build.gradle``.

