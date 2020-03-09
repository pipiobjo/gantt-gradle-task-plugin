package de.pipiobjo.gradle.plugin.tracker;

import de.pipiobjo.gradle.plugin.GanttGradleTaskPlugin;
import de.pipiobjo.gradle.plugin.TaskTimeRecord;
import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeTracker implements BuildListener, TaskExecutionListener {
    private final GanttGradleTaskPlugin plugin;
    private long globalStartTime;
    private Map<String, TaskTimeRecord> records = new HashMap<>();
    private boolean dataDumped = false;


    public TimeTracker(GanttGradleTaskPlugin plugin) {
        globalStartTime = System.currentTimeMillis();
        this.plugin = plugin;
    }

    @Override
    public void buildStarted(Gradle gradle) {
        // nothing to do
    }

    @Override
    public void settingsEvaluated(Settings settings) {
        // nothing to do
    }

    @Override
    public void projectsLoaded(Gradle gradle) {
        // nothing to do
    }

    @Override
    public void projectsEvaluated(Gradle gradle) {
        // nothing to do
    }

    @Override
    public void buildFinished(BuildResult result) {
        this.dumpData();
    }

    @Override
    public void beforeExecute(Task task) {
        List<String> taskDepList = new ArrayList<>();
        task.getTaskDependencies().getDependencies(task).forEach(depTask -> taskDepList.add(depTask.getPath()));

        TaskTimeRecord record = new TaskTimeRecord();
        record.setTaskPath(task.getPath());
        record.setTaskDependencies(taskDepList);
        record.setStartTimeMillis(System.currentTimeMillis() - globalStartTime);
        records.put(task.getPath(), record);
    }

    @Override
    public void afterExecute(Task task, TaskState state) {
        TaskTimeRecord record = records.get(task.getPath());
        record.setEndTimeMillis(System.currentTimeMillis() - globalStartTime);
        record.setStatus(state);

        String stopWithTask = plugin.getExtension().getStopTracingWithTask();
        if (stopWithTask != null && task.getPath().equals(stopWithTask)) {
            dumpData();
        }
    }

    private void dumpData() {
        if (!dataDumped) {
            plugin.getReporter().run(records);
        }
    }
}
