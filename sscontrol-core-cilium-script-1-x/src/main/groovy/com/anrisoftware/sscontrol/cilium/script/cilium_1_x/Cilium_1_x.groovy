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
package com.anrisoftware.sscontrol.cilium.script.cilium_1_x

import javax.inject.Inject

import com.anrisoftware.sscontrol.cilium.service.Cilium
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.utils.iniconfig.external.InitSectionConfigurer

import groovy.util.logging.Slf4j

/**
 * Configures the <i>Cilium 1.x</i> service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Cilium_1_x extends ScriptBase {

    @Inject
    InitSectionConfigurer initSection

    def setupDefaults() {
        Cilium service = service
    }

    def installCilium() {
        shell privileged: false, timeout: timeoutLong, """
cilium install
""" call()
    }

    boolean getUseTransparentEncryption() {
        properties.getProperty "use_transparent_encryption", defaultProperties
    }

    @Override
    def getLog() {
        log
    }
}
