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

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.k8s.base.script.upstream.k8s_1_2x.debian_11.AbstractK8sUpstreamDebian
import com.anrisoftware.sscontrol.k8s.kubectl.linux.kubectl_1_2x.AbstractKubectlLinux
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

import groovy.util.logging.Slf4j


/**
 * Configures the Kubernetes control pane from the upstream sources Debian.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class K8sControlUpstreamDebian extends AbstractK8sUpstreamDebian {

    @Inject
    K8sControlDebianProperties debianPropertiesProvider

    KubectlClusterDebian kubectlClusterLinux

    DebianUtils debian

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Override
    Object run() {
    }

    def setupDefaults() {
        setupMiscDefaults()
        setupClusterDefaults()
        setupControlDefaults()
        setupLabelsDefaults()
    }

    def createConfig() {
        createKubeadmControlConfig()
    }

    def postInstall() {
        applyLabels()
        applyTaints()
    }

    @Override
    def disableSwap() {
        debian.disableSwap()
    }

    @Inject
    void setKubectlClusterLinuxFactory(KubectlClusterDebianFactory factory) {
        this.kubectlClusterLinux = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Override
    ContextProperties getDefaultProperties() {
        debianPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }

    @Override
    AbstractKubectlLinux getKubectlCluster() {
        kubectlClusterLinux
    }
}
