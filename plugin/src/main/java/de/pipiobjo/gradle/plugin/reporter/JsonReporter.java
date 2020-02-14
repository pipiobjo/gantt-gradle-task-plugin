package de.pipiobjo.gradle.plugin.reporter;

import com.google.gson.Gson;
import de.pipiobjo.gradle.plugin.TaskTimeRecord;
import groovy.json.JsonBuilder;
import groovy.lang.Writable;
import groovy.text.StreamingTemplateEngine;
import groovy.text.Template;
import org.gradle.BuildResult;
import org.gradle.api.logging.Logger;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class JsonReporter {
    private final Logger logger;
    private final String chartTemplateResourcePath;
    private final File buildDir;

    public JsonReporter(Logger logger, String chartTemplateResourcePath, File buildDir) {
        this.logger = logger;
        this.chartTemplateResourcePath = chartTemplateResourcePath;
        this.buildDir =  buildDir;
    }

    public void run(Map<String, TaskTimeRecord> records, BuildResult result) {

        Gson gson = new Gson();
        String json =        gson.toJson(records.values());

        logger.error("Reporter prints json {}", json);

        if(buildDir.exists()) {
            File folder = new File(buildDir.toString() + "/reports/gantt");
            if(!folder.exists()){
                folder.mkdirs();
            }
            Path chartPath = new File(buildDir.toString() + "/reports/gantt/gantt.html").toPath();

                try {
                    createChartFile(
                            chartPath,
                            json,
                            "GANTT CHART"
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

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
