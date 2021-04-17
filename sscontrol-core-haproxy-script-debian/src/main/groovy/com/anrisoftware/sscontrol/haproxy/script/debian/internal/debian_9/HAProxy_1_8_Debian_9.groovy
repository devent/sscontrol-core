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
package com.anrisoftware.sscontrol.haproxy.script.debian.internal.debian_9

import static com.anrisoftware.sscontrol.haproxy.script.debian.internal.debian_9.HAProxy_1_8_Debian_9_Service.*

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.haproxy.script.haproxy_1_8.external.HAProxy_1_8
import com.anrisoftware.sscontrol.haproxy.script.haproxy_1_8.external.HAProxy_1_8_Ufw

import groovy.util.logging.Slf4j

/**
 * HAProxy 1.8 for Debian 9.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class HAProxy_1_8_Debian_9 extends HAProxy_Debian_9 {

    @Inject
    HAProxy_1_8_Debian_9_Properties propertiesProvider

    HAProxy_1_8 haproxy

    HAProxy_1_8_Ufw ufw

    @Inject
    void setNfsFactory(HAProxy_1_8_Factory factory) {
        this.haproxy = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Inject
    void setNfsFirewalldFactory(HAProxy_1_8_Ufw_Factory factory) {
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
