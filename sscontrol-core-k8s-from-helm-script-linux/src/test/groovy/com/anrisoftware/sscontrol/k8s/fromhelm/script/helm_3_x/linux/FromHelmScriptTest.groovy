/**
 * Copyright © 2020 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anrisoftware.sscontrol.k8s.fromhelm.script.helm_3_x.linux

import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import com.anrisoftware.sscontrol.shell.utils.LocalhostSocketCondition

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@EnableRuleMigrationSupport
@EnabledIfSystemProperty(named = 'project.custom.local.tests.enabled', matches = 'true')
@ExtendWith(LocalhostSocketCondition.class)
class FromHelmScriptTest extends AbstractFromHelmScriptTest {

    static Stream<Arguments> script_chart() {
        def list = []
        list << of(
                "script_basic",
                """\
service "ssh", host: "localhost", socket: localhostSocket
service "from-helm", chart: "stable/mariadb"
""", {
                    File dir = it.dir
                    File gen = it.test.generatedDir
                    assertFileResource FromHelmScriptTest, dir, "sudo.out", "${it.test.name}_sudo_expected.txt"
                    assertFileResource FromHelmScriptTest, dir, "helm.out", "${it.test.name}_helm_expected.txt"
                })
        list << of(
                "script_config",
                """\
service "ssh", host: "localhost", socket: localhostSocket
service "from-helm", chart: "stable/mariadb" with {
    config << '''
image:
  registry: docker.io
  repository: bitnami/mariadb
  tag: 10.1.38
'''
}
""", {
                    File dir = it.dir
                    File gen = it.test.generatedDir
                    assertFileResource FromHelmScriptTest, dir, "sudo.out", "${it.test.name}_sudo_expected.txt"
                    assertFileResource FromHelmScriptTest, dir, "helm.out", "${it.test.name}_helm_expected.txt"
                })
        list.stream()
    }

    @ParameterizedTest
    @MethodSource
    void script_chart(def name, def script, def expected) {
        log.info "########### {}: {}\n###########", name, script
        doTest([name: name, script: script, expected: expected,
            expectedServicesSize: 2, generatedDir: folder.newFolder(),
            scriptVars: [localhostSocket: LocalhostSocketCondition.localhostSocket]])
    }

    static Stream<Arguments> script_repo() {
        def list = []
        list << of(
                "script_repo_basic",
                """\
service "ssh", host: "localhost", socket: localhostSocket
service "repo-helm", group: 'fantastic-charts' with {
    remote url: "https://fantastic-charts.storage.googleapis.com"
}
service "from-helm", repo: "fantastic-charts", chart: "mariadb"
""", {
                    File dir = it.dir
                    File gen = it.test.generatedDir
                    assertFileResource FromHelmScriptTest, dir, "sudo.out", "${it.test.name}_sudo_expected.txt"
                    assertFileResource FromHelmScriptTest, dir, "helm.out", "${it.test.name}_helm_expected.txt"
                })
        list.stream()
    }

    @ParameterizedTest
    @MethodSource
    void script_repo(def name, def script, def expected) {
        log.info "########### {}: {}\n###########", name, script
        doTest([name: name, script: script, expected: expected,
            expectedServicesSize: 3, generatedDir: folder.newFolder(),
            scriptVars: [localhostSocket: LocalhostSocketCondition.localhostSocket]])
    }
}
