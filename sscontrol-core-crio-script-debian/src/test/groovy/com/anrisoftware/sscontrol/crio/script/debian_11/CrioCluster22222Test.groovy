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
package com.anrisoftware.sscontrol.crio.script.debian_11

import static com.anrisoftware.sscontrol.shell.utils.Nodes3Port22222AvailableCondition.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import com.anrisoftware.sscontrol.shell.utils.Nodes3Port22222AvailableCondition

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@ExtendWith(Nodes3Port22222AvailableCondition.class)
class CrioCluster22222Test extends AbstractCrioRunnerTest {

    @Test
    void "cluster_22222_basic"() {
        def test = [
            name: "cluster_22222_basic",
            script: '''
service "ssh" with {
    host "robobee@node-0.robobee-test.test", socket: sockets.nodes[0]
    host "robobee@node-1.robobee-test.test", socket: sockets.nodes[1]
    host "robobee@node-2.robobee-test.test", socket: sockets.nodes[2]
}
service "crio", version: "1.22"
''',
            scriptVars: [sockets: nodesSockets],
            expectedServicesSize: 2,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                assertStringStartsWithResource CrioClusterTest, remoteCommand('sudo systemctl status crio', 'node-0.robobee-test.test'), "${args.test.name}_systemctl_status_crio_node_0_expected.txt"
                assertStringStartsWithResource CrioClusterTest, remoteCommand('sudo systemctl status crio', 'node-1.robobee-test.test', 22222), "${args.test.name}_systemctl_status_crio_node_1_expected.txt"
                assertStringStartsWithResource CrioClusterTest, remoteCommand('sudo systemctl status crio', 'node-2.robobee-test.test', 22222), "${args.test.name}_systemctl_status_crio_node_2_expected.txt"
            },
        ]
        doTest test
    }

    @Override
    void createDummyCommands(File dir) {
    }

    @Override
    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }
}
