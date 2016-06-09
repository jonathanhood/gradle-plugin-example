import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class TestRealBuild extends GroovyTestCase {
    def projectDir = new File(System.getProperty("user.dir") + "/testProjects/simpleProject")
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

    void testDealWithIt() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("dealwithit")
                .build()

        assertEquals(UP_TO_DATE, result.task(":dealwithit").getOutcome())
        assertTrue(result.output.contains("(•_•) ( •_•)>⌐■-■ (⌐■_■)"))
    }
}
