package de.pipiobjo.gradle.plugin;


import com.jayway.jsonpath.JsonPath;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public class BuildLogicFunctionalTest {
    @Parameters
    public static Object[] data() throws IOException {
        return getLatestGradleVersion();
    }

    private static Object[] getLatestGradleVersion() throws IOException {
        URL url = new URL("https://services.gradle.org/versions/all");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("GET");
        String responseMessage = con.getResponseMessage();
        String content = new String (con.getInputStream().readAllBytes());
        List<String> versions = JsonPath.parse(content).read("$[?(@.downloadUrl =~ /.*distributions\\/.*/i)].version");
        return new Object[] {
                versions.get(0), versions.get(1), versions.get(2), "6.2.1"
        };
    }

    @Parameter
    public String gradleVersion;

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File settingsFile;
    private File buildFile;

    @Before
    public void setup() throws IOException {
        settingsFile = testProjectDir.newFile("settings.gradle");
        buildFile = testProjectDir.newFile("build.gradle");
    }

    @Test
    public void testGanttPluginAssignment() throws IOException {
        copyFile("functionalTest/settings.gradle", settingsFile);
        copyFile("functionalTest/build.gradle", buildFile);

        System.out.println("gradleVersion " + gradleVersion);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--stacktrace")
                .withGradleVersion(gradleVersion)
                .withArguments("build")
                .build();
        System.out.println(result.getOutput());
//        assertTrue(result.getOutput().contains("Hello world!"));
        assertEquals(SUCCESS, result.task(":build").getOutcome());

        // check js is placed in build/reports/gantt/gantt.js
        Path copiedGanttJSFile = Paths.get(testProjectDir.getRoot().getPath(), "build", "reports", "gantt", "gantt.js");
        assertThat(Files.exists(copiedGanttJSFile)).isTrue();

        Path generatedGnttJSONFile = Paths.get(testProjectDir.getRoot().getPath(), "build", "reports", "gantt", "gantt.json");
        assertThat(Files.exists(generatedGnttJSONFile)).isTrue();
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

