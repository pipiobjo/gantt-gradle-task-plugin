# gantt-gradle-task-plugin


![Develop Build](https://github.com/pipiobjo/gantt-gradle-task-plugin/workflows/Develop%20Build/badge.svg?branch=develop)


Plugin to trace all executed gradle tasks with start, end time and their dependencies. 
Finally they are rendered in gantt chart

The plugin is available via the official repos https://plugins.gradle.org/plugin/de.pipiobjo.gradle.plugin.GanttGradleTaskPlugin

```groovy

plugins {
  id "de.pipiobjo.gradle.plugin.GanttGradleTaskPlugin" version "0.1.0-dev.0+34857a5"
}


```


```groovy

buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "de.pipiobjo.gradle:plugin:0.1.0-dev.0+34857a5"
  }
}

apply plugin: "de.pipiobjo.gradle.plugin.GanttGradleTaskPlugin"

```

## Configuration


There are a couple of configuration options all of them are bundled via the configuration closure

```
gantt{
    jsonToFile = true
}
```


|  Property 	                |  Sample Value 	|  Default Value 	| Description  	|
|---	                        |---	            |---	|---	|
| chartTemplateResourcePath  	|  /gantt-custom.html  |  /gantt.html  	|  Overwrite the base html template  	|
| stopTracingWithTask         	|  projectA:task1  	|  null 	|  If you want to stop tracing with a specific task  	|
| jsonToFile                  	|  true         	|  false  	| If you want to dump the json also to the filesystem  	|
| jsonOutputLocation  	        |  /reports/gantt/gantt-custom.json 	| /reports/gantt/gantt.json  	| Writes the json file to another location, but its not longer loaded via the ui, so the template embedded values are used for rendering   	|