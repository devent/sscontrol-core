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
package com.anrisoftware.sscontrol.k8s.script.debian.control_1_2x.debian_11

import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.debian.Debian_11_TestUtils.*
import static com.anrisoftware.sscontrol.shell.utils.LocalhostSocketCondition.*

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
class K8sControlScriptTest extends AbstractControlScriptTest {

    @Test
    void "script basic"() {
        def test = [
            name: "script_basic",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "k8s-cluster"
service "k8s-control", clusterName: "andrea-cluster-1"
''',
            scriptVars: [localhostSocket: localhostSocket],
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource K8sControlScriptTest, dir, "chmod.out", "${args.test.name}_chmod_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "cp.out", "${args.test.name}_cp_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "apt-get.out", "${args.test.name}_apt_get_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "apt-mark.out", "${args.test.name}_apt_mark_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "mkdir.out", "${args.test.name}_mkdir_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "scp.out", "${args.test.name}_scp_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "kubeadm.out", "${args.test.name}_kubeadm_expected.txt"
                assertFileResource K8sControlScriptTest, gen, "kubeadm.yaml", "${args.test.name}_kubeadm_yaml_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "etc/fstab", "${args.test.name}_fstab_expected.txt"
                assertFileResource K8sControlScriptTest, dir, "swapoff.out", "${args.test.name}_swapoff_expected.txt"
                //assertFileResource K8sControlScriptTest, dir, "kubectl.out", "${args.test.name}_kubectl_expected.txt"
            },
        ]
        doTest test
    }

    @Test
    void "open ports for control via ufw"() {
        def test = [
            name: "script_control_ufw",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "k8s-cluster"
service "k8s-control", clusterName: "andrea-cluster-1"
''',
            scriptVars: [localhostSocket: localhostSocket],
            before: { Map test ->
                createEchoCommand test.dir, 'which'
                createCommand ufwActiveCommand, test.dir, 'ufw'
            },
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource K8sControlScriptTest, dir, "ufw.out", "${args.test.name}_ufw_expected.txt"
            },
        ]
        doTest test
    }

    @Test
    void "open ports between nodes via ufw"() {
        def test = [
            name: "script_nodes_ufw",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "ssh", host: "localhost", socket: localhostSocket, group: "nodes"
service "k8s-cluster"
service "k8s-control", clusterName: "andrea-cluster-1" with {
    nodes << "default"
    nodes << "nodes"
}
''',
            scriptVars: [localhostSocket: localhostSocket],
            before: { Map test ->
                createEchoCommand test.dir, 'which'
                createCommand ufwActiveCommand, test.dir, 'ufw'
            },
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource K8sControlScriptTest, dir, "ufw.out", "${args.test.name}_ufw_expected.txt"
            },
        ]
        doTest test
    }

    @Test
    void "script_taints_labels"() {
        def test = [
            name: "script_taints_labels",
            script: '''
service "ssh", host: "localhost", socket: localhostSocket
service "k8s-cluster"
service "k8s-control", clusterName: "andrea-cluster-1" with {
    taint << "dedicated=mail:NoSchedule"
    label << "robobeerun.com/dns=true"
    label << "robobeerun.com/dashboard=true"
}
''',
            scriptVars: [localhostSocket: localhostSocket],
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource K8sControlScriptTest, gen, "kubeadm.yaml", "${args.test.name}_kubeadm_yaml_expected.txt"
                //assertFileResource K8sMasterScriptTest, dir, "kubectl.out", "${args.test.name}_kubectl_expected_0.txt"
            },
        ]
        doTest test
    }
}
