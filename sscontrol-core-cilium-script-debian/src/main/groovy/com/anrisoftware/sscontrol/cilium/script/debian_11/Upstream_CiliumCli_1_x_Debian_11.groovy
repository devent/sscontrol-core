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

import com.anrisoftware.sscontrol.cilium.script.cilium_1_x.Upstream_CiliumCli_1_x
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

/**
 * Installs <i>Cilium-CLI 1.x</i> service from the upstream source for Debian 11.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
class Upstream_CiliumCli_1_x_Debian_11 extends Upstream_CiliumCli_1_x {

    @Inject
    Cilium_Debian_11_Properties debianPropertiesProvider

    DebianUtils debian

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Override
    def run() {
        installCiliumCli()
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
