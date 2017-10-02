/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
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
/*
 l * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.k8smaster.script.debian.internal.k8smaster_1_8.debian_9

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*
import static org.junit.Assume.*

import org.junit.Before
import org.junit.Test

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
class K8sMasterClusterTest extends AbstractMasterRunnerTest {

    @Test
    void "cluster_tls"() {
        def test = [
            name: "cluster_tls",
            script: '''
service "ssh", host: "robobee@andrea-master.robobee-test.test", socket: sockets.masters[0]
service "ssh", group: "masters" with {
    host "robobee@andrea-master.robobee-test.test", socket: sockets.masters[0]
}
service "ssh", group: "nodes" with {
    host "robobee@node-1.robobee-test.test", socket: sockets.nodes[1]
    host "robobee@node-2.robobee-test.test", socket: sockets.nodes[2]
}
service "k8s-cluster", target: 'masters' with {
    credentials type: 'cert', name: 'robobee-admin', ca: certs.admin.ca, cert: certs.admin.cert, key: certs.admin.key
}
service "k8s-master", name: "andrea-master-0-test", advertise: targets.masters[0] with {
    bind secure: "192.168.56.200"
    nodes << "masters"
    nodes << "nodes"
    tls certs.tls
    authentication "cert", ca: certs.tls.ca
    plugin "etcd", endpoint: "https://10.10.10.7:22379" with {
        tls certs.etcd
    }
    plugin "flannel"
    plugin "calico"
    kubelet.with {
        tls certs.tls
    }
    label << "robobeerun.com/dns"
    label << "robobeerun.com/dashboard"
    label << "robobeerun.com/calico"
    label << "robobeerun.com/cluster-monitoring-heapster=required"
    label << "robobeerun.com/cluster-monitoring-influxdb-grafana=required"
}
''',
            scriptVars: [sockets: sockets, certs: robobeetestCerts],
            generatedDir: folder.newFolder(),
            expectedServicesSize: 3,
            expected: { Map args ->
            },
        ]
        doTest test
    }

    static final Map sockets = [
        masters: [
            "/tmp/robobee@robobee-test:22"
        ],
        nodes: [
            "/tmp/robobee@robobee-test:22",
            "/tmp/robobee@robobee-1-test:22",
            "/tmp/robobee@robobee-2-test:22",
        ]
    ]

    @Before
    void beforeMethod() {
        assumeSocketsExists sockets.nodes
    }

    void createDummyCommands(File dir) {
    }

    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }
}