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
import static com.anrisoftware.sscontrol.shell.utils.LocalhostSocketCondition.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.debian.Debian_11_TestUtils.*
import static org.junit.jupiter.api.Assertions.assertThrows

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
class K8sNodeScriptTest extends AbstractNodeScriptTest {

    @Test
    void "script_basic"() {
        def test = [
            name: "script_basic",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "ssh", group: "control-nodes" with {
    host "localhost", socket: localhostSocket
}
service "k8s-cluster", target: "control-nodes" with {
    caCertHash "sha256:7501bc596d3dce2f88ece232d3454876293bea94884bb19f90f2ebc6824e845f"
    token "34f578.e9676c9fc49544bb"
}
service "k8s-node", clusters: "k8s-cluster"
''',
            scriptVars: [localhostSocket: localhostSocket, certs: certs],
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource K8sNodeScriptTest, dir, "chmod.out", "${args.test.name}_chmod_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "cp.out", "${args.test.name}_cp_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "apt-get.out", "${args.test.name}_apt_get_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "apt-mark.out", "${args.test.name}_apt_mark_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "mkdir.out", "${args.test.name}_mkdir_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "scp.out", "${args.test.name}_scp_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "kubeadm.out", "${args.test.name}_kubeadm_expected.txt"
                assertFileResource K8sNodeScriptTest, gen, "kubeadm.yaml", "${args.test.name}_kubeadm_yaml_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "etc/fstab", "${args.test.name}_fstab_expected.txt"
                assertFileResource K8sNodeScriptTest, dir, "swapoff.out", "${args.test.name}_swapoff_expected.txt"
            },
        ]
        doTest test
    }

    @Test
    void "open ports for node via ufw"() {
        def test = [
            name: "script_nodes_ufw",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "ssh", group: "control-nodes" with {
    host "localhost", socket: localhostSocket
}
service "k8s-cluster", target: "control-nodes" with {
    caCertHash "sha256:7501bc596d3dce2f88ece232d3454876293bea94884bb19f90f2ebc6824e845f"
    token "34f578.e9676c9fc49544bb"
}
service "k8s-node", clusters: "k8s-cluster"
''',
            scriptVars: [localhostSocket: localhostSocket, certs: certs],
            before: { Map test ->
                createEchoCommand test.dir, 'which'
                createCommand ufwActiveCommand, test.dir, 'ufw'
            },
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource K8sNodeScriptTest, dir, "ufw.out", "${args.test.name}_ufw_expected.txt"
            },
        ]
        doTest test
    }

}
