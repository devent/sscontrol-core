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
package com.anrisoftware.sscontrol.k8snode.debian.internal.k8snode_1_5

import static com.anrisoftware.sscontrol.k8snode.debian.internal.k8snode_1_5.K8sNode_1_5_Debian_8_Service.*

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.k8snode.upstream.external.K8sNode_1_5_Upstream_Systemd

import groovy.util.logging.Slf4j

/**
 * Configures the K8s-Node 1.5 service from the upstream sources
 * for Systemd and Debian 8.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class K8sNode_1_5_Upstream_Systemd_Debian_8 extends K8sNode_1_5_Upstream_Systemd {

    @Inject
    K8sNode_1_5_Debian_8_Properties debianPropertiesProvider

    @Override
    Object run() {
        stopServices()
        setupMiscDefaults()
        setupClusterDefaults()
        setupKubeletDefaults()
        setupPluginsDefaults()
        createDirectories()
        uploadK8sCertificates()
        uploadEtcdCertificates()
        createKubeletService()
        createKubeletConfig()
        createKubeletManifests()
        createRkt()
        createFlannelCni()
        startServices()
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
    String getSystemName() {
        SYSTEM_NAME
    }

    @Override
    String getSystemVersion() {
        SYSTEM_VERSION
    }
}