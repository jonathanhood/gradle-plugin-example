# 1: My first Gradle plugin

- [Home](../README.md)
- [Next](2-your-first-plugin-test.md)

Gradle plugins start simple. At their core, they are just a JAR file with some code and a properties file.  The contents of a simple plugin JAR file looks something like:

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

We must also tell Gradle that the ``MyPlugin`` class is a plugin applied with the ``myplugin`` name. You do that by editing the ``<pluginname>.properties `` file. 

The example ``myplugin.properties`` file includes a simple declaration of the implementation class.

```
implementation-class=com.jhood.MyPlugin
```

Pretty simple right? You can publish this plugin to your local maven repository (using ``gradle install``, and use it in another example project with:

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

That's it. With some very simple code, we now have a basic functioning plugin. From here, we'll talk about how to add more complex tasks and test the plugin itself.

