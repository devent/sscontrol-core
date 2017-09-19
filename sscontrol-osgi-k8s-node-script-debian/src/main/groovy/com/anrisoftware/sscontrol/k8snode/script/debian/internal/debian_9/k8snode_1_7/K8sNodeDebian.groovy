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
package com.anrisoftware.sscontrol.k8snode.script.debian.internal.debian_9.k8snode_1_7

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.groovy.script.external.ScriptBase
import com.anrisoftware.sscontrol.utils.debian.external.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.external.Debian_9_UtilsFactory

import groovy.util.logging.Slf4j

/**
 * Kubernetes node.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class K8sNodeDebian extends ScriptBase {

    @Inject
    K8sNodeDebianProperties debianPropertiesProvider

    @Inject
    K8sNodeDockerDebianFactory dockerDebianFactory

    @Inject
    KubectlUpstreamDebianFactory kubectlUpstreamFactory

    @Inject
    K8sNodeUfwDebianFactory ufwFactory

    DebianUtils debian

    K8sNodeUpstreamDebian upstream

    K8sNodeSystemdDebian systemd

    @Inject
    void setDebian(Debian_9_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Inject
    void setK8sNodeUpstreamDebianFactory(K8sNodeUpstreamDebianFactory factory) {
        this.upstream = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Inject
    void setSystemdDebianFactory(K8sNodeSystemdDebianFactory factory) {
        this.systemd = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Override
    def run() {
        dockerDebianFactory.create(scriptsRepository, service, target, threads, scriptEnv).run()
        systemd.stopServices()
        debian.installPackages()
        kubectlUpstreamFactory.create(scriptsRepository, service, target, threads, scriptEnv).run()
        upstream.setupDefaults()
        ufwFactory.create(scriptsRepository, service, target, threads, scriptEnv).run()
        upstream.createService()
        systemd.startServices()
        systemd.enableServices()
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