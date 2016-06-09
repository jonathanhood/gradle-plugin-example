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
