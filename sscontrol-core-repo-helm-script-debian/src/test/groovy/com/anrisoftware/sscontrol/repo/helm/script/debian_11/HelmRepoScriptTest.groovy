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
package com.anrisoftware.sscontrol.repo.helm.script.debian_11

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.utils.LocalhostSocketCondition.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport

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
class HelmRepoScriptTest extends AbstractHelmScriptTest {

    @Test
    void "script_remote_url"() {
        def test = [
            name: "script_remote_url",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "repo-helm", group: 'fantastic-charts' with {
    remote url: "https://fantastic-charts.storage.googleapis.com"
}
''',
            scriptVars: [localhostSocket: localhostSocket],
            expectedServicesSize: 2,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource HelmRepoScriptTest, dir, "curl.out", "${args.test.name}_curl_expected.txt"
                assertFileResource HelmRepoScriptTest, dir, "sha256sum.out", "${args.test.name}_sha256sum_expected.txt"
                assertFileResource HelmRepoScriptTest, dir, "tar.out", "${args.test.name}_tar_expected.txt"
                assertFileResource HelmRepoScriptTest, dir, "cat.out", "${args.test.name}_cat_expected.txt"
                assertFileResource HelmRepoScriptTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource HelmRepoScriptTest, dir, "helm.out", "${args.test.name}_helm_expected.txt"
            },
        ]
        doTest test
    }

}
