package de.pipiobjo.gradle.plugin;

import de.pipiobjo.gradle.plugin.reporter.JsonReporter;
import de.pipiobjo.gradle.plugin.tracker.TimeTracker;
import groovy.json.JsonOutput;
import groovy.lang.Writable;
import groovy.text.StreamingTemplateEngine;
import groovy.text.Template;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GanttGradleTaskPlugin implements Plugin<Project> {
    private Map<String, TaskTimeRecord> taskRecords = new ConcurrentHashMap<>();
    private GanttGradleTaskExtension extension = null;
    private JsonReporter reporter = null;

    public void apply(Project project) {

        extension = project.getExtensions().create("gantt", GanttGradleTaskExtension.class);
        project.getGradle().addBuildListener(new TimeTracker(this));
        reporter = new JsonReporter(project.getLogger(), extension.CHART_TEMPLATE_RESOURCE_PATH, project.getBuildDir());

        //todo  better move to task after build
        Path ganttTargetDir = Paths.get(project.getBuildDir().toPath().toString(), "reports", "gantt");
        try {
            Files.createDirectories(ganttTargetDir);
            InputStream resourceAsStreamJS = this.getClass().getClassLoader().getResourceAsStream("static/app.bundle.js");

            Path targetFile = Paths.get(ganttTargetDir.toFile().toString(),  "gantt.js");
            Files.copy(resourceAsStreamJS, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            project.getLogger().error("Can not create target directory {}", ganttTargetDir);
            throw new RuntimeException(e);
        }



    }

    public JsonReporter getReporter() {
        return reporter;
    }
}