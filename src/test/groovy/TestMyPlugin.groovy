import com.jhood.MyPluginExtension
import com.jhood.MyTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import com.jhood.MyPlugin

class TestMyPlugin extends GroovyTestCase {
    void testDealWithIt() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin

        assertNotNull(project.tasks.dealwithit)
    }

    void testMyTask() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin

        assertTrue(project.tasks.mytask instanceof MyTask)
        assertEquals(new File(project.buildDir, "myfile.txt"), project.tasks.mytask.outputFile)
    }

    void testMyOtherTask() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin

        assertTrue(project.tasks.myothertask instanceof MyTask)
        assertEquals(new File(project.buildDir, "otherfile.txt"), project.tasks.myothertask.outputFile)
    }

    void testHasExtension() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply MyPlugin
        assertTrue(project.extensions.myplugin instanceof MyPluginExtension)
        assertEquals("¯\\_(ツ)_/¯", project.extensions.myplugin.fileContent)
    }
}
