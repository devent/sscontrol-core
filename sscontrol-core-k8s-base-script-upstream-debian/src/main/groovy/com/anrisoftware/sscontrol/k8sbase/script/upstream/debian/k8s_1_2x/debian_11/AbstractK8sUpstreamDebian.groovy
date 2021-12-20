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
package com.anrisoftware.sscontrol.k8sbase.script.upstream.debian.k8s_1_2x.debian_11

import javax.inject.Inject

import com.anrisoftware.sscontrol.k8sbase.script.upstream.k8s_1_2x.linux.AbstractK8sUpstreamLinux
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

import groovy.util.logging.Slf4j

/**
 * Configures the K8s service from the upstream sources.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class AbstractK8sUpstreamDebian extends AbstractK8sUpstreamLinux {

    DebianUtils debian

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    def installKubeadm() {
        withRemoteTempFile {
            shell privileged: false, timeout: timeoutLong, """
curl -fsSLo $it ${kubernetesRepositoryKey}
sudo mv $it ${kubernetesRepositoryKeyringFile}
echo "deb [signed-by=${kubernetesRepositoryKeyringFile}] ${kubernetesRepositoryUrl} ${kubernetesRepositoryDist} ${kubernetesRepositoryComponent}" | sudo tee ${kubernetesRepositoryListFile}
""" call()
        }
        debian.installPackages packages: kubeadmPackages, update: true, hold: true
    }

    @Override
    def getLog() {
        log
    }
}