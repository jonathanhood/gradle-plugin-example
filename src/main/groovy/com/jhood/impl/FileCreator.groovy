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
