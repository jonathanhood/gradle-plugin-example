package com.jhood

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {

		// The quick-n-dirty way
		project.task("dealwithit") {
			println("(•_•) ( •_•)>⌐■-■ (⌐■_■)")
		}

		// The "right" way
		project.task("mytask", type: MyTask) {
			group = "My Plugin Tasks"
			description = "Create a file in the build directory"
		}
    }
}
