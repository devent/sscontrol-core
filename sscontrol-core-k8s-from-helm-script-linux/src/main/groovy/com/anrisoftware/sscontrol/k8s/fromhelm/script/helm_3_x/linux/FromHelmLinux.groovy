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
package com.anrisoftware.sscontrol.k8s.fromhelm.script.helm_3_x.linux

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.k8s.fromhelm.service.FromHelm
import com.anrisoftware.sscontrol.repo.helm.script.linux.Helm_3_RepoUpstreamLinux

/**
 * From Helm service for Kubernetes.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
class FromHelmLinux extends AbstractFromHelmLinux {

    @Inject
    FromHelmLinuxProperties linuxPropertiesProvider

    @Inject
    HelmRepoUpstreamLinuxFactory helmRepoUpstreamLinuxFactory

    @Override
    Helm_3_RepoUpstreamLinux getHelmRepoUpstream() {
        helmRepoUpstreamLinuxFactory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Override
    def run() {
        FromHelm service = service
        setupDefaults()
        installHelm()
        def file = createConfig()
        try {
            if (service.useRepo) {
                fromRepo(config: file)
            } else {
                fromChart(config: file)
            }
        } finally {
            deleteTmpFile file
        }
    }

    @Override
    ContextProperties getDefaultProperties() {
        linuxPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }
}
