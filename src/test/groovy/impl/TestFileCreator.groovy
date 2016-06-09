package impl

import com.jhood.impl.FileCreator

class TestFileCreator extends GroovyTestCase {
    void testCreatesFileWithContent() {
        def tempFile = File.createTempFile("temp", ".tmp")
        def creator = new FileCreator(tempFile)
        creator.create()
        assertTrue(tempFile.exists())
        assertEquals("HELLO FROM MY PLUGIN", tempFile.text)
    }

    void testCreatesFileIfParentDirMissing() {
        def tempDir = File.createTempDir()
        def tempFile = new File(tempDir, "testing.tmp")
        tempDir.delete()

        def creator = new FileCreator(tempFile)
        creator.create()
        assertTrue(tempFile.exists())
        assertEquals("HELLO FROM MY PLUGIN", tempFile.text)
    }
}
