package de.pipiobjo.gradle.plugin;

import de.pipiobjo.gradle.plugin.reporter.JsonReporter;
import de.pipiobjo.gradle.plugin.tracker.TimeTracker;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class GanttGradleTaskPlugin implements Plugin<Project> {
    private GanttGradleTaskExtension extension = null;
    private JsonReporter reporter = null;

    public void apply(Project project) {

        extension = project.getExtensions().create("gantt", GanttGradleTaskExtension.class);
        project.getGradle().addBuildListener(new TimeTracker(this));
        reporter = new JsonReporter(project.getLogger(), extension.getChartTemplateResourcePath(), project.getBuildDir(), this);

        Path ganttTargetDir = Paths.get(project.getBuildDir().toPath().toString(), "reports", "gantt");
        try {
            Files.createDirectories(ganttTargetDir);
            InputStream resourceAsStreamJS = this.getClass().getClassLoader().getResourceAsStream("static/app.bundle.js");

            Path targetFile = Paths.get(ganttTargetDir.toFile().toString(), "gantt.js");
            Files.copy(resourceAsStreamJS, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            project.getLogger().error("Can not create target directory {}", ganttTargetDir);
            throw new InvalidUserDataException("Error while applying gantt plugin", e);
        }
    }

    public JsonReporter getReporter() {
        return reporter;
    }

    public GanttGradleTaskExtension getExtension() {
        return extension;
    }
}