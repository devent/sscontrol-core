/*-
 * #%L
 * sscontrol-osgi - k8s-from-repository-script-linux
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
package com.anrisoftware.sscontrol.k8s.fromrepository.script.linux.internal.script_1_13

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.external.utils.Nodes3Port22222AvailableCondition.*
import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*
import static org.junit.jupiter.api.Assumptions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import com.anrisoftware.sscontrol.shell.external.utils.Nodes3Port22222AvailableCondition
import com.anrisoftware.sscontrol.types.host.external.HostServiceScript

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@ExtendWith(Nodes3Port22222AvailableCondition.class)
class FromRepositoryNginxIngressClusterTest extends AbstractFromRepositoryRunnerTest {

    @Test
    void "nginx ingress addon to cluster"() {
        def test = [
            name: "cluster_nginx_ingress_addon_cluster",
            script: '''
def edgeNodeTargetHttpPort = 30000
def edgeNodeTargetHttpsPort = 30001

service "ssh" with {
    host "robobee@node-0.robobee-test.test", socket: sockets.nodes[0]
}

service "ssh", group: "edge-nodes" with {
    host "robobee@node-1.robobee-test.test", socket: sockets.nodes[1]
}

service "k8s-cluster" with {
}

service "repo-git", group: "nginx-ingress" with {
    remote url: "git@github.com:robobee-repos/kube-nginx-ingress.git"
    credentials "ssh", key: robobeeKey
    checkout branch: "feature/4155-Update-to-nginx-0.20.0"
}

service "from-repository", repo: "nginx-ingress", dest: "/etc/kubernetes/addons/ingress-nginx" with {
    vars << [
        nginxIngressController: [
            image: [name: 'quay.io/kubernetes-ingress-controller/nginx-ingress-controller', version: '0.20.0'],
            limits: [cpu: '0', memory: '200Mi'],
            requests: [cpu: '0', memory: '200Mi'],
            affinity: [key: "robobeerun.com/ingress-nginx", name: "required", required: true],
            nodePort: [http: edgeNodeTargetHttpPort, https: edgeNodeTargetHttpsPort],
            config: [
                useProxyProtocol: true,
            ]
        ]
    ]
    vars << [
        defaultHttpBackend: [
            image: [name: 'gcr.io/google_containers/defaultbackend', version: '1.4'],
            limits: [cpu: '10m', memory: '20Mi'],
            requests: [cpu: '10m', memory: '20Mi'],
            affinity: [key: "robobeerun.com/ingress-nginx", name: "required", required: true],
        ]
    ]
}
''',
            scriptVars: [sockets: nodesSockets, robobeeKey: robobeeKey],
            expectedServicesSize: 4,
            expected: { Map args ->
            },
        ]
        doTest test
    }

    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }

    void createDummyCommands(File dir) {
    }

    def setupServiceScript(Map args, HostServiceScript script) {
        return script
    }
}
