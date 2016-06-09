package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {

		project.extensions.add("myplugin", MyPluginExtension)

		// The quick-n-dirty way
		project.task("dealwithit") {
			println("(•_•) ( •_•)>⌐■-■ (⌐■_■)")
		}

		// The "right" way
		project.task("mytask", type: MyTask) {
			group = "MyPlugin"
			description = "Create myfile.txt in the build directory"
		}

		// The "right" way with configuration
		project.task("myothertask", type: MyTask) {
			group = "MyPlugin"
			description = "Create otherfile.txt in the build directory"

			outputFile = new File(project.buildDir, "otherfile.txt")
		}

		project.afterEvaluate {
			println(project.extensions.myplugin.fileContent)
		}
    }
}
