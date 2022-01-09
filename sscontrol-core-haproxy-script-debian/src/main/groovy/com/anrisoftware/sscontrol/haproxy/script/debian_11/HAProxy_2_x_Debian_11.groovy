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
package com.anrisoftware.sscontrol.haproxy.script.debian_11

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.haproxy.script.haproxy_2_x.HAProxy_2_x
import com.anrisoftware.sscontrol.haproxy.script.haproxy_2_x.HAProxy_2_x_Ufw

import groovy.util.logging.Slf4j

/**
 * HAProxy 2.x for Debian 11.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class HAProxy_2_x_Debian_11 extends HAProxy_Debian_11 {

    static final String SYSTEM_VERSION = "11";

    static final String SYSTEM_NAME = "debian";

    @Inject
    HAProxy_2_x_Debian_11_Properties propertiesProvider

    HAProxy_2_x haproxy

    HAProxy_2_x_Ufw ufw

    @Inject
    void setHaproxyFactory(HAProxy_2_x_Factory factory) {
        this.haproxy = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Inject
    void setUfwFactory(HAProxy_2_x_Ufw_Factory factory) {
        this.ufw = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Override
    ContextProperties getDefaultProperties() {
        propertiesProvider.get()
    }

    @Override
    def run() {
        haproxy.setupDefaultOptions()
        installPackages()
        ufw.configureFilewall()
        stopServices()
        haproxy.deployConfig()
        haproxy.deployDhparam()
        startServices()
        enableServices()
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
