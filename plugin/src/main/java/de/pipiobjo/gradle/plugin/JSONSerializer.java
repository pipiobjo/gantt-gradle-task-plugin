package de.pipiobjo.gradle.plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JSONSerializer {
//    public String toJson(Map<String, TaskTimeRecord> recordSet) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[");
//
//        AtomicInteger counter = new AtomicInteger();
//        recordSet.forEach((key, record) -> {
//            boolean isLast = counter.get() == recordSet.size() - 1;
//            counter.getAndIncrement();
//            sb.append("{");
//            sb.append("\"name\":" + "\"" + record.getTaskPath() + "\",");
//            sb.append("\"start\": " + record.getStartTimeMillis() + ",");
//            sb.append("\"end\": " + record.getEndTimeMillis() + ",");
//
//            sb.append("\"deps\": " + buildJSONArray(record.getTaskDependencies()));
//
//            if (isLast) {
//                sb.append("}");
//            } else {
//                sb.append("},");
//            }
//        });
//
//
//        sb.append("]");
//        return sb.toString();
//    }

    private String buildJSONArray(List<String> taskDependencies) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        AtomicInteger counter = new AtomicInteger();
        taskDependencies.forEach(s -> {
            boolean isLast = counter.get() == taskDependencies.size() - 1;
            counter.getAndIncrement();

            sb.append("\"" + s);
            if (isLast) {
                sb.append("\"");
            } else {
                sb.append("\",");
            }

        });

        sb.append("]");
        return sb.toString();
    }
}
