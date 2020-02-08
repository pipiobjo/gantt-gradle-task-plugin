package de.pipiobjo.gradle.plugin;

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

class GanttGradleTaskPlugin implements Plugin<Project> {
    private Map<String, TaskTimeRecord> taskRecords = new ConcurrentHashMap<>();
    private GanttGradleTaskExtension extension = null;
    private long pluginAttachedTime;

    public void apply(Project project) {
        extension = project.getExtensions().create("gantt", GanttGradleTaskExtension.class);

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

        pluginAttachedTime = System.currentTimeMillis();
        project.getGradle().getTaskGraph().whenReady(new Action<TaskExecutionGraph>() {
            @Override
            public void execute(TaskExecutionGraph taskExecutionGraph) {
                taskExecutionGraph.getAllTasks().forEach( task -> {
                    addTimeTrackingAspectToTask(task);
                });
            }
        }) ;

        project.getGradle().buildFinished(buildResult -> {
            File buildDir = project.getBuildDir();
            if(buildDir.exists()){
                project.getLogger().debug("After Build and with records: ", taskRecords);
                String json = new JSONSerializer().toJson(taskRecords);
                System.out.println(json);

//                Path chartPath = buildDir.toPath().ganttTargetDir("gantt.html");
                Path chartPath = null;

                try {
                    chartPath = File.createTempFile("gantt2", ".html").toPath();
                    createChartFile(
                            chartPath,
                            json,
                            project.getName()
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                project.getLogger().info("Generated gantt file {}", chartPath);

            }
        });

    }




    void addTimeTrackingAspectToTask(Task task){

        task.doFirst(action -> {
           List<String> taskDepList = new ArrayList<>();
           task.getTaskDependencies().getDependencies(task).forEach( depTask -> {
               taskDepList.add(depTask.getPath());
           });

           TaskTimeRecord record = new TaskTimeRecord();
            record.setTaskPath(task.getPath());
            record.setStartTimeMillis(System.currentTimeMillis() - pluginAttachedTime);
            record.setTaskDependencies(taskDepList);

            taskRecords.put(task.getPath(), record);
        });

        task.doLast("stop trace", action -> {
            taskRecords.get(task.getPath()).setEndTimeMillis(System.currentTimeMillis()- pluginAttachedTime);
        });



    }


    private void createChartFile(Path chartPath, String json, String projectName) throws IOException, ClassNotFoundException {
        Template template = new StreamingTemplateEngine().createTemplate(readResource(extension.CHART_TEMPLATE_RESOURCE_PATH));


        Map<String, Object> templateData = new HashMap<>();
        templateData.put("data", json);
        templateData.put("projectName", projectName);


//        templateData.put("d3js", readResource(extension.D3JS_RESOURCE_PATH)); // TODO use file or https lookup path via extension

        Writable templateWithData = template.make(templateData);


        templateWithData.writeTo(Files.newBufferedWriter(
                chartPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE
        )).flush();

    }

    private String readResource(String path) throws IOException {

        URL resource = this.getClass().getResource(path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);

    }

}