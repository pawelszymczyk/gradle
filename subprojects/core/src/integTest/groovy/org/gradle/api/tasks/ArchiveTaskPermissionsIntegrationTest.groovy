/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.api.tasks

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.util.Requires
import org.gradle.util.TestFile
import org.gradle.util.TestPrecondition
import spock.lang.Unroll

class ArchiveTaskPermissionsIntegrationTest extends AbstractIntegrationSpec {

    @Requires(TestPrecondition.FILE_PERMISSIONS)
    @Unroll
    def "file and directory permissions are preserved when using #taskName task"() {
        given:
        createDir('parent') {
            child {
                mode = dirMode
                file('reference.txt').mode = fileMode
            }
        }
        def archName = "test.${taskName.toLowerCase()}"
        and:
        buildFile << """
            task pack(type: $taskName) {
                archiveName = "$archName"
                from 'parent'
            }
            """
        when:
        run "pack"
        file(archName).usingNativeTools()."$unpackMethod"(file("build"))
        then:
        file("build/child").mode == dirMode
        file("build/child/reference.txt").mode == fileMode
        where:
        taskName | fileMode | dirMode | unpackMethod
        "Zip"    | 0746     | 0777    | "unzipTo"
        "Tar"    | 0746     | 0777    | "untarTo"
    }

    @Requires(TestPrecondition.FILE_PERMISSIONS)
    @Unroll
    def "file and directory permissions can be overridden in #taskName task"() {
        given:
        createDir('parent') {
            child {
                mode = 0766
                file('reference.txt').mode = 0777
            }
        }
        def archName = "test.${taskName.toLowerCase()}"

        and:
        buildFile << """
                task pack(type: $taskName) {
                    archiveName = "$archName"
                    fileMode = $fileMode
                    dirMode = $dirMode
                    from 'parent'
                }
                """
        when:
        run "pack"
        and:
        file(archName).usingNativeTools()."$unpackMethod"(file("build"))
        then:
        file("build/child").mode == dirMode
        file("build/child/reference.txt").mode == fileMode
        where:
        taskName | fileMode | dirMode | unpackMethod
        "Zip"    | 0774     | 0756    | "unzipTo"
        "Tar"    | 0774     | 0756    | "untarTo"

    }

    @Requires(TestPrecondition.FILE_PERMISSIONS)
    @Unroll
    def "file and directory permissions are preserved for unpacked #taskName archives"() {
        given:
        TestFile testDir = createDir('testdir') {
            mode = dirMode
            file('reference.txt').mode = fileMode
        }
        def archName = "test.${taskName.toLowerCase()}"
        testDir.usingNativeTools()."$packMethod"(file(archName))
        and:
        buildFile << """
            task unpack(type: Copy) {
                from $treeMethod("$archName")
                into 'unpacked'
            }
            """

        when:
        run "unpack"
        and:
        then:
        file("unpacked/testdir").mode == dirMode
        file("unpacked/testdir/reference.txt").mode == fileMode
        where:
        taskName | fileMode | dirMode | packMethod | treeMethod
        "Zip"    | 0762     | 0753    | "zipTo"    | "zipTree"
        "Tar"    | 0762     | 0753    | "tarTo"    | "tarTree"
    }

    private def createDir(String name, Closure cl) {
        TestFile root = file(name)
        root.create(cl)
    }
}