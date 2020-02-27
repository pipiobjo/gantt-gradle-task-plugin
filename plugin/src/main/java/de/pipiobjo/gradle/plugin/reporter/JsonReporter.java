package de.pipiobjo.gradle.plugin.reporter;

import com.google.gson.Gson;
import de.pipiobjo.gradle.plugin.GanttGradleTaskPlugin;
import de.pipiobjo.gradle.plugin.TaskTimeRecord;
import groovy.lang.Writable;
import groovy.text.StreamingTemplateEngine;
import groovy.text.Template;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonReporter {
    private final Logger logger;
    private final String chartTemplateResourcePath;
    private final File buildDir;
    private final GanttGradleTaskPlugin plugin;
    private Gson gson = new Gson();

    public JsonReporter(Logger logger, String chartTemplateResourcePath, File buildDir, GanttGradleTaskPlugin plugin) {
        this.logger = logger;
        this.chartTemplateResourcePath = chartTemplateResourcePath;
        this.buildDir = buildDir;
        this.plugin = plugin;
    }

    public void run(Map<String, TaskTimeRecord> records) {

        Collection<TaskTimeRecord> values = records.values();
        String json = gson.toJson(values);
        logger.debug("Reporter json {}", json);

        if (buildDir.exists()) {
            File folder = new File(buildDir.toString() + "/reports/gantt");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            Path chartPath = new File(buildDir.toString() + "/reports/gantt/gantt.html").toPath();

            try {
                createChartFile(
                        chartPath,
                        json,
                        "GANTT CHART"
                );
                doAdditionalJSONHandling(values);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error while writing gantt report ", e);
            }

        }


    }

    private void doAdditionalJSONHandling(Collection<TaskTimeRecord> obj) throws IOException {
        if(plugin.getExtension().isJsonToFile()){

            File jsonPath = new File(buildDir.toString() + plugin.getExtension().getJsonOutputLocation());
            if(!jsonPath.exists()){
                jsonPath.createNewFile();
            }
            gson.toJson(obj, new FileWriter(jsonPath));

        }
    }

    private void createChartFile(Path chartPath, String json, String projectName) throws IOException, ClassNotFoundException {
        Template template = new StreamingTemplateEngine().createTemplate(readResource(chartTemplateResourcePath));


        Map<String, Object> templateData = new HashMap<>();
        templateData.put("data", json);
        templateData.put("projectName", projectName);


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
        byte[] bytes = this.getClass().getResourceAsStream(path).readAllBytes();
        return new String(bytes);

    }
}
