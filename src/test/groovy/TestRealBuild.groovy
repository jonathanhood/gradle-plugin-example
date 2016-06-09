import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestRealBuild extends GroovyTestCase {
    def projectDir = new File(System.getProperty("user.dir") + "/testProjects/simpleProject")
    def configuredProjectDir = new File(System.getProperty("user.dir") + "/testProjects/configuredProject")
    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

    void setUp() {
        def buildDir = new File(projectDir, "build")
        def configuredBuildDir = new File(configuredProjectDir, "build")
        if(buildDir.exists()) buildDir.deleteDir()
        if(configuredBuildDir.exists()) configuredBuildDir.deleteDir()
        buildDir.delete()
        configuredBuildDir.delete()
    }

    void tearDown() {
        setUp()
    }

    void testDealWithIt() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("dealwithit")
                .build()

        assertEquals(UP_TO_DATE, result.task(":dealwithit").getOutcome())
        assertTrue(result.output.contains("(•_•) ( •_•)>⌐■-■ (⌐■_■)"))
    }

    void testMyTask() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("mytask")
                .build()

        assertEquals(SUCCESS, result.task(":mytask").getOutcome())
        assertTrue((new File(projectDir, "build/myfile.txt")).exists())
    }

    void testMyOtherTask() {
        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("myothertask")
                .build()

        assertEquals(SUCCESS, result.task(":myothertask").getOutcome())
        assertTrue((new File(projectDir, "build/otherfile.txt")).exists())
    }

    void testConfiguration() {
        def testFile = new File(configuredProjectDir, "build/myfile.txt")

        def result = GradleRunner.create()
                .withProjectDir(configuredProjectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("mytask")
                .build()

        assertEquals(SUCCESS, result.task(":mytask").getOutcome())
        assertTrue(testFile.exists())
        assertEquals("CONFIGURED", testFile.text)
    }
}
