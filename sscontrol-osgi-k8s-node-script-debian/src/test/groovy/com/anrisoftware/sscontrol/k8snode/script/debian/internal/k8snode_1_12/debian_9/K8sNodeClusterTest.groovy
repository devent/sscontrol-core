/*-
 * #%L
 * sscontrol-osgi - k8s-node-script-debian
 * %%
 * Copyright (C) 2016 - 2018 Advanced Natural Research Institute
 * %%
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
 * #L%
 */
package com.anrisoftware.sscontrol.k8snode.script.debian.internal.k8snode_1_12.debian_9

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.external.utils.Nodes3AvailableCondition.*
import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.debian.external.Debian_9_TestUtils.*
import static org.junit.jupiter.api.Assumptions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import com.anrisoftware.sscontrol.shell.external.utils.Nodes3AvailableCondition

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@ExtendWith(Nodes3AvailableCondition.class)
class K8sNodeClusterTest extends AbstractNodeRunnerTest {

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
        kubelet address: host.hostAddress
        cluster host: "masters", join: joinCommand
        nodes.labels[i].each { label << it }
        nodes.taints[i].each { taint << it }
    }
}
''',
            scriptVars: [sockets: nodesSockets, certs: robobeetestCerts, nodes: nodes, joinCommand: joinCommand],
            expectedServicesSize: 3,
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
            },
        ]
        doTest test
    }

    static final Map robobeetestCerts = [
        etcd: [
            ca: K8sNodeClusterTest.class.getResource('robobee_test_etcd_ca.pem'),
            cert: K8sNodeClusterTest.class.getResource('robobee_test_etcd_kube_0_cert.pem'),
            key: K8sNodeClusterTest.class.getResource('robobee_test_etcd_kube_0_key.pem'),
        ]
    ]

    /**
     * <pre>
     * token=$(kubeadm token generate)
     * kubeadm token create $token --print-join-command --ttl=24h
     * <pre>
     */
    static final String joinCommand = 'kubeadm join 192.168.56.200:6443 --token peavvh.11etbm5zlp2yehof --discovery-token-ca-cert-hash sha256:0209e5c2538bd7e2ac5a0b91fc04e0d510d3b7260a44caff4a3c83dd6b42bcca'

    static final Map nodes = [
        labels: [
            [
                "robobeerun.com/role=edge-router",
                "muellerpublic.de/role=web",
                "robobeerun.com/heapster=required",
                "robobeerun.com/cert-manager=required"
            ],
            [
                "muellerpublic.de/role=dev",
                "robobeerun.com/dashboard=required"
            ]
        ],
        taints: [[], []]]

    void createDummyCommands(File dir) {
    }

    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }
}
