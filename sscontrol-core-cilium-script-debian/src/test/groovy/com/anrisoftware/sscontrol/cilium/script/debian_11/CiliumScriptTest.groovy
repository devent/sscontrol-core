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
package com.anrisoftware.sscontrol.cilium.script.debian_11

import static com.anrisoftware.sscontrol.shell.utils.LocalhostSocketCondition.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.debian.Debian_11_TestUtils.*

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
class CiliumScriptTest extends AbstractCiliumScriptTest {

    @Test
    void "script_basic"() {
        def test = [
            name: "script_basic",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "cilium"
''',
            scriptVars: [localhostSocket: localhostSocket],
            generatedDir: folder.newFolder(),
            expectedServicesSize: 2,
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource CiliumScriptTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource CiliumScriptTest, dir, "apt-get.out", "${args.test.name}_apt_get_expected.txt"
                assertFileResource CiliumScriptTest, dir, "cilium.out", "${args.test.name}_cilium_expected.txt"
                assertFileResource CiliumScriptTest, dir, "kubectl.out", "${args.test.name}_kubectl_expected.txt"
            }
        ]
        doTest test
    }

    @Test
    void "script open ports ufw"() {
        def test = [
            name: "script_ufw",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "ssh", group: "control-nodes", host: "localhost", socket: localhostSocket
service "ssh", group: "worker-nodes", host: "localhost", socket: localhostSocket
service "cilium" with {
    node << "control-nodes"
    node << "worker-nodes"
}
''',
            scriptVars: [localhostSocket: localhostSocket],
            before: { Map test ->
                createEchoCommand test.dir, 'which'
                createCommand ufwActiveCommand, test.dir, 'ufw'
            },
            generatedDir: folder.newFolder(),
            expectedServicesSize: 2,
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource CiliumScriptTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource CiliumScriptTest, dir, "apt-get.out", "${args.test.name}_apt_get_expected.txt"
                assertFileResource CiliumScriptTest, dir, "cilium.out", "${args.test.name}_cilium_expected.txt"
                assertFileResource CiliumScriptTest, dir, "kubectl.out", "${args.test.name}_kubectl_expected.txt"
                assertFileResource CiliumScriptTest, dir, "ufw.out", "${args.test.name}_ufw_expected.txt"
            }
        ]
        doTest test
    }

}
