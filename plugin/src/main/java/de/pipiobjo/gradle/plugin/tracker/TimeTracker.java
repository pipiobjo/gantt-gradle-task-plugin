package de.pipiobjo.gradle.plugin.tracker;

import de.pipiobjo.gradle.plugin.GanttGradleTaskPlugin;
import de.pipiobjo.gradle.plugin.JSONSerializer;
import de.pipiobjo.gradle.plugin.TaskTimeRecord;
import org.gradle.BuildListener;
import org.gradle.BuildResult;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.TaskState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeTracker implements BuildListener, TaskExecutionListener {
    private final GanttGradleTaskPlugin plugin;
    private long globalStartTime;
    Map<String, TaskTimeRecord> records = new HashMap<>();

    public TimeTracker(GanttGradleTaskPlugin plugin) {
        globalStartTime = System.currentTimeMillis();
        this.plugin = plugin;
    }

    @Override
    public void buildStarted(Gradle gradle) {


    }

    @Override
    public void settingsEvaluated(Settings settings) {

    }

    @Override
    public void projectsLoaded(Gradle gradle) {

    }

    @Override
    public void projectsEvaluated(Gradle gradle) {
        gradle.getTaskGraph().whenReady(new Action<TaskExecutionGraph>() {
            @Override
            public void execute(TaskExecutionGraph taskExecutionGraph) {
                taskExecutionGraph.getAllTasks().forEach( task -> {
                    List<String> taskDepList = new ArrayList<>();
                    task.getTaskDependencies().getDependencies(task).forEach( depTask -> {
                        taskDepList.add(depTask.getPath());
                    });

                    TaskTimeRecord record = new TaskTimeRecord();
                    record.setTaskPath(task.getPath());
                    record.setTaskDependencies(taskDepList);

                    records.put(task.getPath(), record);
                });
            }

    });
    }

    @Override
    public void buildFinished(BuildResult result) {

        plugin.getReporter().run(records, result);
    }

    @Override
    public void beforeExecute(Task task) {
        records.get(task.getPath()).setStartTimeMillis(System.currentTimeMillis()- globalStartTime);
    }

    @Override
    public void afterExecute(Task task, TaskState state) {
        TaskTimeRecord record = records.get(task.getPath());
        record.setEndTimeMillis(System.currentTimeMillis()- globalStartTime);
        record.setStatus(state);

    }
}
