# 1: Your first Gradle plugin

- [Home](../README.md)
- [Next](2-your-first-plugin-test.md)



Gradle plugins start simple. You can define some groovy code for the plugin, and add tasks to a given project. In this tutorial we'll cover:

- How to create a simple plugin.
- How to add a task to the project.
- How to tell gradle about your plugin.
- How to use your plugin in a project.

## Basic Plugin Structure

At their core, a plugin is just a JAR file with some code and a properties file.  The contents of a simple plugin JAR file looks something like:

```
.
├── com
│   └── jhood
│       └── MyPlugin.class
└── META-INF
    ├── gradle-plugins
    │   └── myplugin.properties
    └── MANIFEST.MF

```

## Defining a Plugin Implementation

The plugin code itself starts really simple. Just inherit from the ``Plugin<Project>`` and implement the ``apply`` method.

The following example adds the ``dealwithit`` task to gradle project it's applied to.

```groovy
package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
		project.task("dealwithit") {
			println("(•_•) ( •_•)>⌐■-■ (⌐■_■)")
		}
    }
}
```

## Telling Gradle How-To Load Your Plugin

We must also tell Gradle that the ``MyPlugin`` class is a plugin applied with the ``myplugin`` name. You do that by editing the ``<pluginname>.properties `` file. 

The example ``myplugin.properties`` file includes a simple declaration of the implementation class.

```
implementation-class=com.jhood.MyPlugin
```


## Using the Plugin in a Project

You can publish this plugin to your local maven repository (using ``gradle install``), and use it in another example project with:

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
```

In the real world we'd want to use a real maven repository. For this tutorial we'll use the maven local repository to keep things simple.

Once this is done, you can use the ``myplugin`` functionality:

``` bash
$> gradle dealwithit
(•_•) ( •_•)>⌐■-■ (⌐■_■)
:dealwithit UP-TO-DATE

BUILD SUCCESSFUL

Total time: 0.503 secs
```

## Next Steps

That's it. With some very simple code, we now have a basic functioning plugin. We've written a plugin implementation by inheriting from ``Plugin<Project>`` and used the plugin to implement the ``dealwithit`` task. We've also informed gradle of our plugin via the ``myplugin.properties`` file.

In the next steps we will talk about how-to write a test for this simple plugin and its one task. Testing is one of the primary benefits to using Gradle plugins, and we'll repeatedly touch on this in future steps.
 
