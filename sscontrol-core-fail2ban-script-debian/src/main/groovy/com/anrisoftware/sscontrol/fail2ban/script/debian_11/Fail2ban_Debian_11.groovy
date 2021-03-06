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
package com.anrisoftware.sscontrol.fail2ban.script.debian_11

import javax.inject.Inject

import com.anrisoftware.sscontrol.fail2ban.script.debian.Fail2ban_0_11_Debian
import com.anrisoftware.sscontrol.fail2ban.script.fail2ban_0_x.Jail_0_x
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

import groovy.util.logging.Slf4j

/**
 * Configures the <i>Fail2ban</i> service for Debian 11.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class Fail2ban_Debian_11 extends Fail2ban_0_11_Debian {

    @Inject
    Fail2ban_Debian_11_Properties debianPropertiesProvider

    @Inject
    Ufw_Jail_Debian_11_Factory jail

    DebianUtils debian

    @Inject
    void setDebian(Debian_11_UtilsFactory factory) {
        this.debian = factory.create this
    }

    @Override
    def run() {
        setupDefaults()
        stopService()
        installPackages()
        configureService()
        jailScript.run()
        enableService()
        startService()
    }

    Jail_0_x getUfwJailScript() {
        jail.create scriptsRepository, service, target, threads, scriptEnv
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
