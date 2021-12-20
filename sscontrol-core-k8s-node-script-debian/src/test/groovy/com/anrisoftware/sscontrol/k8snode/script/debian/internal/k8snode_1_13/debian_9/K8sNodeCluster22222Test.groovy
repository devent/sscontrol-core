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
package com.anrisoftware.sscontrol.k8snode.script.debian.internal.k8snode_1_13.debian_9

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.utils.Nodes3Port22222AvailableCondition.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.debian.Debian_9_TestUtils.*
import static org.junit.jupiter.api.Assumptions.*

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
class K8sNodeCluster22222Test extends AbstractNodeRunnerTest {

    @Test
    void "nodes_tls"() {
        def test = [
            name: "nodes_tls",
            script: '''
service "ssh", host: "robobee@node-0.robobee-test.test", socket: sockets.masters[0]
service "ssh", group: "masters" with {
    host "robobee@node-0.robobee-test.test", socket: sockets.masters[0]
}
service "ssh", group: "nodes" with {
    host "robobee@node-1.robobee-test.test", socket: sockets.nodes[1]
    host "robobee@node-2.robobee-test.test", socket: sockets.nodes[2]
}
service "k8s-cluster", target: "masters"
targets['nodes'].eachWithIndex { host, i ->
    service "k8s-node", target: host, name: "node-${i+1}" with {
        plugin "nfs-client"
        kubelet address: host.hostAddress
        cluster host: "masters", join: joinCommand
        nodes.labels[i].each { label << it }
        nodes.taints[i].each { taint << it }
    }
}
''',
            scriptVars: [sockets: nodesSockets, nodes: nodes, joinCommand: joinCommand],
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
            },
        ]
        doTest test
    }

    /**
     * <pre>
     * token=$(kubeadm token generate)
     * kubeadm token create $token --print-join-command --ttl=24h
     * <pre>
     */
    static final String joinCommand = 'kubeadm join 192.168.56.200:6443 --token d7tjs4.tfdwugtlai2j5qf9 --discovery-token-ca-cert-hash sha256:953a58596463f2f1bda76c0de2ea9621213a688c2814aa26b06c3762ff1b404e'

    static final Map nodes = [
        labels: [
            [
                "robobeerun.com/ingress-nginx=required",
                "robobeerun.com/cert-manager=required",
                "robobeerun.com/grafana=required",
                "robobeerun.com/prometheus=required",
                "robobeerun.com/mariadb=required",
                "muellerpublic.de/role=web",
            ],
            [
                "robobeerun.com/prometheus=required",
                "robobeerun.com/mariadb=required",
                "muellerpublic.de/role=dev",
            ]
        ],
        taints: [[], []]]

    void createDummyCommands(File dir) {
    }

    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }
}