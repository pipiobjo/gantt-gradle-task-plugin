package de.pipiobjo.gradle.plugin;


public class GanttGradleTaskExtension {
    private String chartTemplateResourcePath = "/gantt.html";
    private String stopTracingWithTask;
    private boolean jsonToFile = false;
    private String jsonOutputLocation = "/reports/gantt/gantt.json";

    public String getChartTemplateResourcePath() {
        return chartTemplateResourcePath;
    }

    public void setChartTemplateResourcePath(String chartTemplateResourcePath) {
        this.chartTemplateResourcePath = chartTemplateResourcePath;
    }

    public String getStopTracingWithTask() {
        return stopTracingWithTask;
    }

    public void setStopTracingWithTask(String stopTracingWithTask) {
        this.stopTracingWithTask = stopTracingWithTask;
    }

    public boolean isJsonToFile() {
        return jsonToFile;
    }

    public void setJsonToFile(boolean jsonToFile) {
        this.jsonToFile = jsonToFile;
    }

    public String getJsonOutputLocation() {
        return jsonOutputLocation;
    }

    public void setJsonOutputLocation(String jsonOutputLocation) {
        this.jsonOutputLocation = jsonOutputLocation;
    }
}
