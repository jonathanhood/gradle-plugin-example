# 3: Declaring Tasks the Right Way

- [Home](../README.md)
- [Previous](2-your-first-plugin-test.md)

Implement tasks directly in our top-level plugin class can rapidly grow out of control. As you add tasks to the plugin, it becomes beneficial to declare your tasks in their own files. 

In this tutorial we will cover:

- How to define a task in its own file.
- How to add the task to your project.
- How to add categories and descriptions to your task.

## Declaring a Task

Declaring a task in your plugin as a seperate file is easy. You simply inherit from the ``DefaultTask`` class and implement an ``@TaskAction`` handler.

We'll create a simple tasks which creates a file in the project every time it is run.

```groovy
package com.jhood

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MyTask extends DefaultTask {
    @TaskAction
    def action() {
        def file = new File(project.buildDir, "myfile.txt")
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

Another benefit to declaring tasks this way is we can now make them configurable. For instance, we'll modify ``MyTask`` above to make the file path written configurable.

```groovy
package com.jhood

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MyTask extends DefaultTask {
	String outputFile = new File(project.buildDir, "myfile.txt")

    @TaskAction
    def action() {
        outputFile.text = "HELLO FROM MY PLUGIN"
    }
}
```

Now we've made ``outputFile`` configurable at task creation time. We can use this in our ``MyPlugin`` definition to reuse a task multiple times with different outcomes.

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

