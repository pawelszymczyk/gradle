package org.gradle.sonar.runner.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.sonar.runner.SonarRunnerExtension;
import org.gradle.util.GUtil;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public class GenerateSonarProperties extends DefaultTask {

    private String directory;

    @TaskAction
    public void run() {
        SonarRunner sonarRunner = (SonarRunner) getProject().getTasks().findByName(SonarRunnerExtension.SONAR_RUNNER_TASK_NAME);

        Map<String, Object> sonarProperties = sonarRunner.getSonarProperties();

        Properties propertiesObject = new Properties();
        propertiesObject.putAll(sonarProperties);

        File propertyFile = new File(directory != null ? new File(directory) : getTemporaryDir(), "sonar-project.properties");
        GUtil.saveProperties(propertiesObject, propertyFile);
    }

    @Input
    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
