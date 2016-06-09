# Gradle Plugin Example and Tutorial

## 1: What is a Gradle Plugin?

Most fundamentally, a gradle plugin is library of code that, when loaded by Gradle build script, adds new functionality and capabilities to the build system.

Gradle ships with a lot of built in plugins. Most users are probably aware of the ``java`` for instance.

A typical ``build.gradle`` for java projects using the built in plugins might look like:

``` groovy
apply plugin: "java"

repositories {
	mavenCentral()
}

dependencies {
	testCompile "junit:junit:4.+"
}
```

In this case the ``java`` plugin adds some capabilities such as :

- Compiling of java source code.
- Running of java unit tests with junit.
- Packaging of a java library in a JAR file.

## 2: Why would I want to write a Gradle plugin?
 
Custom gradle plugins are useful to add functionality to the build system in general. Rather than writing custom additions in your ``build.gradle`` you distribute your functionality as a plugin. This has a variety of benefits:

- Organizations can capture common practices (configuration, application packaging, code standards, etc) in a plugin that is shared among many projects.
- Plugins can be unit and integration tested seperately from a project. This increase the confidence of making build system changes.
- Plugins are versioned, which allows projects to control change to their build system while still sharing functionality between projects.
- Plugins allow you to add new functionality to the build system. Custom packaging formats, new ways of running tests, etc. 

In my workplace we use a gradle plugin to accomplish all of the above. Our project ``build.gradle`` files are typically only declare dependencies, but still enable a wide variety of functionality for code analysis, packaging, integration testing, and other items. This funcionality is shared among a great many projects.

## 3: My first Gradle plugin

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

In the real world we'd want to use a real maven repository. For now we'll use the maven local repository to keep things simple.

Once this is done, you can use the ``myplugin`` functionality:

``` bash
$> gradle dealwithit
(•_•) ( •_•)>⌐■-■ (⌐■_■)
:dealwithit UP-TO-DATE

BUILD SUCCESSFUL

Total time: 0.503 secs
```

That's it. With some very simple code, we now have a basic functioning plugin. From here, we'll talk about how to add more complex tasks and test the plugin itself.

 