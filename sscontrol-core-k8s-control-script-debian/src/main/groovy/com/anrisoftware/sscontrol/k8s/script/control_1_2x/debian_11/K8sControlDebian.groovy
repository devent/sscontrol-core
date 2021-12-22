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
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.k8s.script.control_1_2x.debian_11.K8sControlUfwDebianFactory
import com.anrisoftware.sscontrol.k8s.script.control_1_2x.debian_11.K8sControlUpstreamDebianFactory
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

import groovy.util.logging.Slf4j

/**
 * Kubernetes control pane for Debian.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class K8sControlDebian extends ScriptBase {

    @Inject
    K8sControlDebianProperties debianPropertiesProvider

    K8sControlUpstreamDebian upstream

    @Inject
    K8sControlUfwDebianFactory ufwFactory

    DebianUtils debian

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Inject
    void setK8sMasterUpstreamDebianFactory(K8sControlUpstreamDebianFactory factory) {
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
        if (disableSwap) {
            disableSwap()
        }
        upstream.installKube()
        upstream.setupKubectl()
        upstream.waitNodeAvailable()
        upstream.postInstall()
        def joinCommand = upstream.joinCommand
        states << [kubeadmJoinCommand: joinCommand]
        upstream.waitNodeReady()
    }

    @Override
    ContextProperties getDefaultProperties() {
        debianPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }

    /**
     * Disables the swap.
     */
    def disableSwap() {
        replace privileged: true, dest: fstabFile, escape: false, append: false with {
            line search: "((#?).*${fstabSwapName}.*)", replace: '#$3'
            it
        }()
        shell privileged: true, """
swapoff -a
""" call()
    }

    /**
     * Returns if swap should be disabled,
     * for example {@code true}
     *
     * <ul>
     * <li>profile property {@code disable_swap}</li>
     * </ul>
     */
    Boolean getDisableSwap() {
        getScriptBooleanProperty 'disable_swap'
    }

    /**
     * Returns the fstab file to disable swap,
     * for example {@code /etc/fstab}
     *
     * <ul>
     * <li>profile property {@code fstab_file}</li>
     * </ul>
     */
    File getFstabFile() {
        getScriptFileProperty 'fstab_file'
    }

    /**
     * Returns the fstab swap name,
     * for example {@code "swap"}
     *
     * <ul>
     * <li>profile property {@code fstab_swap_name}</li>
     * </ul>
     */
    String getFstabSwapName() {
        getScriptProperty 'fstab_swap_name'
    }
}
