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
package com.anrisoftware.sscontrol.k8s.script.control_1_2x.debian_11

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import com.anrisoftware.sscontrol.shell.utils.RobobeeSocketCondition

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@ExtendWith(RobobeeSocketCondition.class)
class K8sControlServerTest extends AbstractControlRunnerTest {

    @Test
    void "server basic"() {
        def test = [
            name: "server_basic",
            script: '''
service "ssh", host: "robobee@node-3.robobee-test.test", socket: robobeeSocket
service "ssh", group: "control-nodes" with {
    host "robobee@node-3.robobee-test.test", socket: robobeeSocket
}
service "k8s-cluster", target: "control-nodes" with {
}
service "k8s-control", clusterName: "andrea-cluster-1" with {
    nodes << "control-nodes"
    label << "robobeerun.com/heapster=required"
    label << "robobeerun.com/dashboard=required"
    label << "robobeerun.com/nfs=required"
}
''',
            scriptVars: [robobeeSocket: RobobeeSocketCondition.robobeeSocket],
            generatedDir: folder.newFolder(),
            expectedServicesSize: 3,
            expected: { Map args ->
            },
        ]
        doTest test
    }

    void createDummyCommands(File dir) {
    }

    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }
}
