/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.artifacts.ivyservice;

import org.apache.ivy.Ivy;
import org.gradle.api.Action;
import org.gradle.api.artifacts.resolution.SoftwareArtifact;
import org.gradle.api.internal.artifacts.metadata.ModuleVersionArtifactMetaData;
import org.gradle.api.internal.artifacts.metadata.ModuleVersionMetaData;

public class ContextualArtifactResolver implements ArtifactResolver {
    private final CacheLockingManager lockingManager;
    private final IvyContextManager ivyContextManager;
    private final ArtifactResolver delegate;

    public ContextualArtifactResolver(CacheLockingManager lockingManager, IvyContextManager ivyContextManager, ArtifactResolver delegate) {
        this.lockingManager = lockingManager;
        this.ivyContextManager = ivyContextManager;
        this.delegate = delegate;
    }

    public void resolveArtifact(final ModuleVersionMetaData moduleMetaData, final ModuleVersionArtifactMetaData artifact, final BuildableArtifactResolveResult result) {
        lockingManager.useCache(String.format("resolve %s", artifact), new Runnable() {
            public void run() {
                ivyContextManager.withIvy(new Action<Ivy>() {
                    public void execute(Ivy ivy) {
                        delegate.resolveArtifact(moduleMetaData, artifact, result);
                    }
                });
            }
        });
    }

    public void resolveArtifactSet(final ModuleVersionMetaData moduleMetaData, final Class<? extends SoftwareArtifact> artifactType, final BuildableArtifactSetResolveResult result) {
        lockingManager.useCache(String.format("Resolve %s from %s", artifactType, moduleMetaData), new Runnable() {
            public void run() {
                ivyContextManager.withIvy(new Action<Ivy>() {
                    public void execute(Ivy ivy) {
                        delegate.resolveArtifactSet(moduleMetaData, artifactType, result);
                    }
                });
            }
        });
    }
}