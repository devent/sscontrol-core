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

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

import groovy.util.logging.Slf4j

/**
 * K8s-Node Debian.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class K8sNodeDebian extends ScriptBase {

    @Inject
    K8sNodeDebianProperties debianPropertiesProvider

    K8sNodeUpstreamDebian upstream

    @Inject
    K8sNodeUfwDebianFactory ufwFactory

    DebianUtils debian

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Inject
    void setK8sNodeUpstreamDebianFactory(K8sNodeUpstreamDebianFactory factory) {
        this.upstream = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Override
    def run() {
        debian.installPackages()
        debian.enableModules()
        upstream.setupDefaults()
        ufwFactory.create(scriptsRepository, service, target, threads, scriptEnv).run()
        upstream.installKubeadm()
        upstream.createConfig()
        upstream.setupSwap()
        upstream.joinNode()
        upstream.postInstall()
    }

    @Override
    ContextProperties getDefaultProperties() {
        debianPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }
}
