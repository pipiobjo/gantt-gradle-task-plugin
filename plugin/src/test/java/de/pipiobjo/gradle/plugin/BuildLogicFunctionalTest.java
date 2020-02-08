package de.pipiobjo.gradle.plugin;


import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import static org.assertj.core.api.Assertions.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.gradle.testkit.runner.TaskOutcome.*;



public class BuildLogicFunctionalTest {
    @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File settingsFile;
    private File buildFile;

    @Before
    public void setup() throws IOException {
        settingsFile = testProjectDir.newFile("settings.gradle");
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @Test
    public void testHelloWorldTask() throws IOException {
        copyFile("functionalTest/settings.gradle", settingsFile);
        copyFile("functionalTest/build.gradle", buildFile);



        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--stacktrace")

                .withArguments("build")
                .build();
        System.out.println(result.getOutput());
//        assertTrue(result.getOutput().contains("Hello world!"));
        assertEquals(SUCCESS, result.task(":build").getOutcome());

        // check js is placed in build/reports/gantt/gantt.js
        Path copiedGanttJSFile = Paths.get(testProjectDir.getRoot().getPath(),  "build", "reports", "gantt","gantt.js");
        assertThat(Files.exists(copiedGanttJSFile)).isTrue();
    }

    private void copyFile(String source, File target) throws IOException {
        String content = Files.readString(Paths.get(this.getClass().getClassLoader().getResource(source).getPath()), StandardCharsets.UTF_8);
        writeFile(target, content);
    }

    private void writeFile(File destination, String content) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(destination));
            output.write(content);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
}

