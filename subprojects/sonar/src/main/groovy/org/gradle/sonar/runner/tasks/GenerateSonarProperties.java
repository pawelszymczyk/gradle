/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.sonar.runner.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.sonar.runner.GenerateSonarPropertiesExtension;
import org.gradle.sonar.runner.SonarRunnerExtension;
import org.gradle.util.GUtil;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * Generates sonar-project.properties file in a specified directory.
 */
public class GenerateSonarProperties extends DefaultTask {

    @TaskAction
    public void run() {
        SonarRunner sonarRunner = (SonarRunner) getProject().getTasks().findByName(SonarRunnerExtension.SONAR_RUNNER_TASK_NAME);

        Map<String, Object> sonarProperties = sonarRunner.getSonarProperties();

        Properties propertiesObject = new Properties();
        propertiesObject.putAll(sonarProperties);

        GenerateSonarPropertiesExtension extenstion = getProject().getExtensions().findByType(GenerateSonarPropertiesExtension.class);

        File propertyFile = new File(extenstion.getDirectory() != null
                ? new File(extenstion.getDirectory()) : getTemporaryDir(), "sonar-project.properties");
        GUtil.saveProperties(propertiesObject, propertyFile);
    }
}
