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
package com.anrisoftware.sscontrol.k8s.node.script.node_1_2x.debian_11

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.utils.Nodes3Port22222AvailableCondition.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.debian.Debian_11_TestUtils.*
import static org.junit.jupiter.api.Assumptions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import com.anrisoftware.sscontrol.shell.utils.Nodes3Port22222AvailableCondition

import groovy.util.logging.Slf4j

/**
 *
 * <pre>
 * token=$(kubeadm token generate)
 * kubeadm token create $token --print-join-command --ttl=24h
 * <pre>
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@ExtendWith(Nodes3Port22222AvailableCondition.class)
class K8sNodeCluster22222Test extends AbstractNodeRunnerTest {

    @Test
    void "cluster_basic"() {
        def test = [
            name: "cluster_basic",
            script: '''
service "ssh", group: "control-nodes" with {
    host "robobee@node-3.robobee-test.test", socket: sockets.controls[0]
}
service "ssh", group: "worker-nodes" with {
    host "robobee@node-4.robobee-test.test", socket: sockets.workers[0]
}
service "k8s-cluster", target: "control-nodes" with {
    caCertHash "sha256:c7c5a10534b966da79df9854201242090083520f5988ff6f70f54e191fd11fb1"
    token "2bfhqk.ol099spevtrhpvml"
}
service "k8s-node", clusters: "k8s-cluster", target: "worker-nodes"
''',
            scriptVars: [sockets: nodesSockets],
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
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
