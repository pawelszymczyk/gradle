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

package org.gradle.play.internal.run;

import org.gradle.api.tasks.compile.BaseForkOptions;
import org.gradle.play.platform.PlayPlatform;

import java.io.File;

public class PlayRunSpecV23X extends DefaultVersionedPlayRunSpec {
    public PlayRunSpecV23X(Iterable<File> classpath, File projectPath, BaseForkOptions forkOptions, int httpPort, PlayPlatform playPlatform) {
        super(classpath, projectPath, forkOptions, httpPort, playPlatform);
    }

    @Override
    protected Class<?> getBuildLinkClass(ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader.loadClass("play.core.BuildLink");
    }

    @Override
    protected Class<?> getBuildDocHandlerClass(ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader.loadClass("play.core.BuildDocHandler");
    }

    @Override
    protected Class<?> getDocHandlerFactoryClass(ClassLoader docsClassLoader) throws ClassNotFoundException {
        return docsClassLoader.loadClass("play.docs.BuildDocHandlerFactory");
    }

}
