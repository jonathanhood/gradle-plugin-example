# Gradle Plugin Example and Tutorial

## The Tutorial

1. [Your First Gradle Plugin](tutorial/1-your-first-gradle-plugin.md)
2. [Your First Plugin Test](tutorial/2-your-first-plugin-test.md)
3. [Declaring Tasks the Right Way](tutorial/3-declaring-tasks-the-right-way.md)
4. [Making Unit Testable Plugins](tutorial/4-making-unit-testable-plugins.md)
5. [Making Configurable Plugins](tutorial/5-making-configurable-plugins.md)

## What is a Gradle Plugin?

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

## Why would I want to write a Gradle plugin?
 
Custom gradle plugins are useful to add functionality to the build system in general. Rather than writing custom additions in your ``build.gradle`` you distribute your functionality as a plugin. This has a variety of benefits:

- Organizations can capture common practices (configuration, application packaging, code standards, etc) in a plugin that is shared among many projects.
- Plugins can be unit and integration tested seperately from a project. This increase the confidence of making build system changes.
- Plugins are versioned, which allows projects to control change to their build system while still sharing functionality between projects.
- Plugins allow you to add new functionality to the build system. Custom packaging formats, new ways of running tests, etc. 

In my workplace we use a gradle plugin to accomplish all of the above. Our project ``build.gradle`` files are typically only declare dependencies, but still enable a wide variety of functionality for code analysis, packaging, integration testing, and other items. This funcionality is shared among a great many projects.

