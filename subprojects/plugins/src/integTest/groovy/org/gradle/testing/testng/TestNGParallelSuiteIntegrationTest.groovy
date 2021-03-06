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


package org.gradle.testing.testng

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.testing.fixture.TestNGCoverage
import spock.lang.Issue

public class TestNGParallelSuiteIntegrationTest extends AbstractIntegrationSpec {

    @Issue("GRADLE-3190")
    def "runs with multiple parallel threads"() {
        buildFile << """
            apply plugin: 'java'
            repositories { jcenter() }
            dependencies {
                testCompile 'org.testng:testng:$TestNGCoverage.NEWEST'
            }
            test {
              useTestNG {
                suites "suite.xml"
              }
            }
        """

        String suiteXml = ""
        200.times { x ->
            file("src/test/java/Foo${x}Test.java") << """
                public class Foo${x}Test {
                    @org.testng.annotations.Test public void test() {
                        for (int i=0; i<20; i++) {
                            System.out.println("" + i + " - foo ${x} - " + Thread.currentThread().getId());
                        }
                    }
                }
            """
            suiteXml += "<test name='t${x}'><classes><class name='Foo${x}Test'/></classes></test>\n"
        }

        file("suite.xml") << """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="AwesomeSuite" parallel="tests" thread-count="20">
  $suiteXml
</suite>"""


        expect:
        run("test")
    }
}