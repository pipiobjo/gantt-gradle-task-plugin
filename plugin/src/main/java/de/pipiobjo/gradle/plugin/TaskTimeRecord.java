package de.pipiobjo.gradle.plugin;

import java.util.List;

public class TaskTimeRecord {
    private String taskPath;
    private long startTimeMillis;
    private long endTimeMillis;
    private List<String> taskDependencies;

    public String getTaskPath() {
        return taskPath;
    }

    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public List<String> getTaskDependencies() {
        return taskDependencies;
    }

    public void setTaskDependencies(List<String> taskDependencies) {
        this.taskDependencies = taskDependencies;
    }
}
