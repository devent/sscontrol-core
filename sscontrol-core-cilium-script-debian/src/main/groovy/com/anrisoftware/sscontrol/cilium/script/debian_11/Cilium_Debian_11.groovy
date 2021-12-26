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
package com.anrisoftware.sscontrol.cilium.script.debian_11

import javax.inject.Inject

import com.anrisoftware.sscontrol.cilium.script.debian.Cilium_1_x_Debian
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

/**
 * Configures the <i>Cilium</i> service for Debian 11.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
class Cilium_Debian_11 extends Cilium_1_x_Debian {

    @Inject
    Cilium_Debian_11_Properties debianPropertiesProvider

    Upstream_CiliumCli_1_x_Debian_11 ciliumCliUpstreamScript

    DebianUtils debian

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Inject
    void setCiliumCliUpstreamScript(Upstream_CiliumCli_1_x_Debian_11_Factory factory) {
        ciliumCliUpstreamScript = factory.create scriptsRepository, service, target, threads, scriptEnv
    }

    @Override
    def run() {
        setupDefaults()
        installPackages()
        ciliumCliUpstreamScript.run()
        installCilium()
    }

    @Override
    Properties getDefaultProperties() {
        debianPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }
}
