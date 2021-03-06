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
package com.anrisoftware.sscontrol.k8s.cluster.script.linux.cluster_1_2x

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.k8s.cluster.service.K8sClusterScript

import groovy.util.logging.Slf4j

/**
 * Configures the <i>K8s-Cluster</i> service for Linux.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class K8sClusterLinux extends ScriptBase implements K8sClusterScript {

    @Inject
    K8sClusterLinuxProperties linuxPropertiesProvider

    KubectlClusterLinux kubectlClusterLinux

    @Override
    def run() {
        kubectlClusterLinux.run()
    }

    @Inject
    void setKubectlClusterLinuxFactory(KubectlClusterLinuxFactory factory) {
        this.kubectlClusterLinux = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Override
    ContextProperties getDefaultProperties() {
        linuxPropertiesProvider.get()
    }

    @Override
    void runKubectl(Map<String, Object> vars) {
        kubectlClusterLinux.runKubectl vars
    }

    @Override
    def getLog() {
        log
    }
}
