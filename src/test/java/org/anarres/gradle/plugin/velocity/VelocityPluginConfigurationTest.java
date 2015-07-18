package org.anarres.gradle.plugin.velocity;

import java.io.File;
import java.util.Collections;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class for testing configurations in VelocityPluginExtension
 */
public class VelocityPluginConfigurationTest
{
    Project project;

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build();
    }

    @Test
    public void setCustomFilter() {
        project.apply(Collections.singletonMap("plugin", "java"));
        project.apply(Collections.singletonMap("plugin", "velocity"));

        ExtensionContainer extensions = project.getExtensions();
        VelocityPluginExtension dsl = (VelocityPluginExtension) extensions.getByName("velocity");

        // Retrieve plugin task and verify default values
        VelocityTask task = (VelocityTask) project.getTasks().findByName("velocityVpp");
        String filter = task.getFilter();
        assertEquals(VelocityPluginExtension.DEFAULT_FILTER_REGEX, filter);

        // Modify the PluginExtensions filter
        String testValue = "myFilter";
        dsl.filter = testValue;

        // Verify that plugin task has the new value for filter
        task = (VelocityTask) project.getTasks().findByName("velocityVpp");
        filter = task.getFilter();

        assertEquals(testValue, filter);
    }

    @Test
    public void testRemoveFileExtension() throws Exception {
        project.apply(Collections.singletonMap("plugin", "java"));
        project.apply(Collections.singletonMap("plugin", "velocity"));

        ExtensionContainer extensions = project.getExtensions();
        VelocityPluginExtension dsl = (VelocityPluginExtension) extensions.getByName("velocity");

        // Create output path and file for test
        File path = new File(project.getProjectDir().getAbsoluteFile(), "velocity-test");
        path.mkdirs();
        new File(path, "velocity.txt.vtl").createNewFile();

        // Adjust configuration
        dsl.inputDir = path;
        dsl.filter = "**/*.vtl";
        dsl.removeFileExtension = ".vtl";

        // Retrieve plugin task and verify default values
        VelocityTask task = (VelocityTask) project.getTasks().findByName("velocityVpp");
        assertEquals(".vtl", task.getRemoveFileExtension());

        // Execute task
        task.execute();

        // verify that output file has been created with stripped extension
        String parent = project.getProjectDir() + "/" + VelocityPluginExtension.DEFAULT_OUTPUT_DIR;
        File outputFile = new File(parent, "velocity.txt");

        assertTrue("Output file has not been created correctly", outputFile.exists());
    }

    @Test
    public void testRemoveFileExtensionNotUsed() throws Exception {
        project.apply(Collections.singletonMap("plugin", "java"));
        project.apply(Collections.singletonMap("plugin", "velocity"));

        ExtensionContainer extensions = project.getExtensions();
        VelocityPluginExtension dsl = (VelocityPluginExtension) extensions.getByName("velocity");

        // Create output path and file for test
        File path = new File(project.getProjectDir().getAbsoluteFile(), "velocity-test");
        path.mkdirs();
        new File(path, "velocity.txt.vtl").createNewFile();

        // Adjust configuration
        dsl.inputDir = path;
        dsl.filter = "**/*.vtl";

        // Retrieve plugin task and verify default values
        VelocityTask task = (VelocityTask) project.getTasks().findByName("velocityVpp");
        assertNull("Property removeFileExtension should be null", task.getRemoveFileExtension());

        // Execute task
        task.execute();

        // verify that output file has been created with stripped extension
        String parent = project.getProjectDir() + "/" + VelocityPluginExtension.DEFAULT_OUTPUT_DIR;
        File outputFile = new File(parent, "velocity.txt.vtl");

        assertTrue("Output file has not been created correctly", outputFile.exists());
    }
}
