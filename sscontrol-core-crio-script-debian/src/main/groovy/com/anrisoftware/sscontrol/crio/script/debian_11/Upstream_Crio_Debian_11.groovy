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
package com.anrisoftware.sscontrol.crio.script.debian_11

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.crio.script.debian.Upstream_Crio_Debian
import com.anrisoftware.sscontrol.crio.service.Crio
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

import groovy.util.logging.Slf4j

/**
 * CRI-O from upstream.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class Upstream_Crio_Debian_11 extends Upstream_Crio_Debian {

    @Inject
    Crio_1_22_Debian_11_Properties propertiesProvider

    DebianUtils debian

    @Override
    def run() {
    }

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Override
    ContextProperties getDefaultProperties() {
        propertiesProvider.get()
    }

    String getCrioFullVersion() {
        def service = (Crio) service
        switch (service.version) {
            case ~/^1.22.*/:
                return "1.22.1"
        }
        throw new IllegalArgumentException("Unkown version ${service.version}")
    }

    @Override
    def getLog() {
        log
    }
}
