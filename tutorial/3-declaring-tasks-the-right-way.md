# 3: Declaring Tasks the Right Way

- [Home](../README.md)
- [Previous](2-your-first-plugin-test.md)
- [Next](4-making-unit-testable-plugins.md)

Implementing tasks directly in our top-level plugin class can rapidly grow out of control. As you add tasks to the plugin, it becomes beneficial to declare your tasks in their own files. 

In this tutorial we will cover:

- How to define a task in its own file.
- How to add the task to your project.
- How to add categories and descriptions to your task.
- How to add tests at the integration level for our new tasks.

## Declaring a Task

Declaring a task in your plugin as a seperate file is easy. You simply inherit from the ``DefaultTask`` class and implement an ``@TaskAction`` handler.

We'll implement a simple task which creates a file in the project every time it is run.

```groovy
package com.jhood

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MyTask extends DefaultTask {
    @TaskAction
    def action() {
        def file = new File(project.buildDir, "myfile.txt")
		file.parentFile.mkdirs()
        file.createNewFile()
        file.text = "HELLO FROM MY PLUGIN"
    }
}
```

Once a task has been defined, it must be added to the project with a name. This definition typically goes in your top-level plugin class. 

```groovy
package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
		project.task("mytask", type: MyTask)
    }
}
```

## Making a Task Configurable

Another benefit to declaring tasks this way is we can now make them configurable. For instance, we'll modify ``MyTask`` above to make the file path configurable.

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
        outputFile.text = "HELLO FROM MY PLUGIN"
    }
}
```

Now we just need to configure ``outputFile`` when we construct the task. We can use this in our ``MyPlugin`` definition to reuse a task multiple times with different outcomes.

```groovy
package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
		project.task("mytask", type: MyTask)
		project.task("myothertask", type: MyTask) {
			outputFile = new File(project.buildDir, "otherfile.txt")
		}
    }
}
```

## Adding Group and Description

Adding group and description statements to your tasks can help users understand how-to user your task. Adding these items done in a similiar fashion to adding plugin configuration.


```groovy 

package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
		project.task("mytask", type: MyTask) {
			group = "MyPlugin"
			description = "Create myfile.txt in the build directory"
		}
		project.task("myothertask", type: MyTask) {
			group = "MyPlugin"
			description = "Create otherfile.txt in the build directory"
			
			outputFile = new File(project.buildDir, "otherfile.txt")
		}
    }
}

```

You will be able to view this information when listing tasks with ``gradle tasks``.

```
$> gradle tasks
:tasks

------------------------------------------------------------
All tasks runnable from root project
------------------------------------------------------------

Build Setup tasks
-----------------
init - Initializes a new Gradle build. [incubating]
wrapper - Generates Gradle wrapper files. [incubating]

Help tasks
----------
buildEnvironment - Displays all buildscript dependencies declared in root project 'test'.
components - Displays the components produced by root project 'test'. [incubating]
dependencies - Displays all dependencies declared in root project 'test'.
dependencyInsight - Displays the insight into a specific dependency in root project 'test'.
help - Displays a help message.
model - Displays the configuration model of root project 'test'. [incubating]
projects - Displays the sub-projects of root project 'test'.
properties - Displays the properties of root project 'test'.
tasks - Displays the tasks runnable from root project 'test'.

MyPlugin tasks
--------------
myothertask - Create otherfile.txt in the build directory
mytask - Create myfile.txt in the build directory

```

## Testing that Tasks are Added Properly

With this change we can start unit testing some functionality of hte plugin. We'll write a test which ensures that tasks are added properly to a project by the plugin.

```groovy
import com.jhood.MyTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import com.jhood.MyPlugin

class TestMyPlugin extends GroovyTestCase {
    void testDealWithIt() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin

        assertNotNull(project.tasks.dealwithit)
    }

    void testMyTask() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin

        assertTrue(project.tasks.mytask instanceof MyTask)
        assertEquals(new File(project.buildDir, "myfile.txt"), project.tasks.mytask.outputFile)
    }

    void testMyOtherTask() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin

        assertTrue(project.tasks.myothertask instanceof MyTask)
        assertEquals(new File(project.buildDir, "otherfile.txt"), project.tasks.myothertask.outputFile)
    }
}
```

The testing above covers a few important aspects of the plugin itself:

1. Tasks are registered appropriately with the project.
2. Tasks registered of the appropriate type.
3. Tasks registered have the proper configuration.

We'll test the implementation of each task seperately in other steps. For the plugin itself, it is sufficient to say the plugin adds the tasks correctly.

## Testing the Task Itself

Unfortunately, testing tasks themselves is not such an easy task. We will investigate strategies later which make it possible to unit test them. For now, we will test them using our previously built integration test infrastructure.

``` groovy
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestRealBuild extends GroovyTestCase {
    def projectDir = new File(System.getProperty("user.dir") + "/testProjects/simpleProject")
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

    void setUp() {
        def buildDir = new File(projectDir, "build")
        if(buildDir.exists()) buildDir.deleteDir()
        buildDir.delete()
    }

    void tearDown() {
        setUp()
    }

    void testMyTask() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("mytask")
                .build()

        assertEquals(SUCCESS, result.task(":mytask").getOutcome())
        assertTrue((new File(projectDir, "build/myfile.txt")).exists())
    }

    void testMyOtherTask() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("myothertask")
                .build()

        assertEquals(SUCCESS, result.task(":myothertask").getOutcome())
        assertTrue((new File(projectDir, "build/otherfile.txt")).exists())
    }
}
```

In this new integration test there is some important items to point out:

1. We added a ``setUp`` and ``tearDown`` block which ensures that our project is always cleaned up between tests.
2. We are now checking for some build output. Groovy includes many methods for inspecting and working with files. For this test we are only focusing on checking existence of an output file.

Otherwise, the new tests look very much the same as those added in the previous tutorial. You can probably imagine other ways you might check the output of your tasks, but for now we'll leave this as it is.

## Next Steps

In this tutorial we have learned how to define tasks in a reusable way. We have also learned how to make tasks configurable. To aid in use of our plugin we learned how to add documentation to our tasks. Finally, we learned how to add some simple tests for our next tasks.

In the next tutorial we will cover how-to split tasks in a way that allows unit testing of our plugin. This will make implementation of complex tasks easier to test.

