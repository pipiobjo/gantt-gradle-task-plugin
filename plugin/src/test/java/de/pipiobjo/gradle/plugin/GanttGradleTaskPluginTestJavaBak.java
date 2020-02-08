package de.pipiobjo.gradle.plugin;

import org.gradle.api.Project;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;


public class GanttGradleTaskPluginTestJavaBak {

    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply( "de.pipiobjo.gradle.plugin.GanttGradleTaskPlugin");

//        assertTrue(project.tasks.hello instanceof GreetingTask)
    }
}
