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
package com.anrisoftware.sscontrol.haproxy.script.haproxy_2_x

import javax.inject.Inject

import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.haproxy.service.HAProxy
import com.anrisoftware.sscontrol.haproxy.service.Proxy
import com.anrisoftware.sscontrol.utils.ufw.linux.UfwLinuxUtilsFactory
import com.anrisoftware.sscontrol.utils.ufw.linux.UfwUtils

import groovy.util.logging.Slf4j

/**
 * HAProxy 2.x Ufw.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class HAProxy_2_x_Ufw extends ScriptBase {

    UfwUtils ufw

    @Inject
    void setUfwLinuxUtilsFactory(UfwLinuxUtilsFactory factory) {
        this.ufw = factory.create(this)
    }

    /**
     * Configures Ufw to allow connections to the frontends.
     */
    def configureFilewall() {
        HAProxy service = this.service
        if (!ufw.active) {
            return
        }
        service.proxies.each { Proxy proxy ->
            def address = proxy.frontend.address == "*" ? "any" : proxy.frontend.address
            shell privileged: true, """
ufw allow from any to ${address} port ${proxy.frontend.port}
""" call()
        }
    }

    @Override
    def getLog() {
        log
    }
}
