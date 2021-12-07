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
package com.anrisoftware.sscontrol.fail2ban.script.generic.centos.internal.centos_7

import javax.inject.Inject

import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.fail2ban.script.generic.fail2ban_0_x.Jail_0_x
import com.anrisoftware.sscontrol.utils.centos.external.CentosUtils
import com.anrisoftware.sscontrol.utils.centos.external.Centos_7_UtilsFactory

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
com.anrisoftware.sscontrol.fail2ban.script.genericds Jail_0_x {

    @Inject
    IptablesMultiportJail_Centos_7_Properties centosPropertiesProvider

    @Inject
    TemplatesFactory templatesFactory

    CentosUtils centos

    @Inject
    void setCentos(Centos_7_UtilsFactory factory) {
        this.centos = factory.create this
    }

    @Override
    def run() {
        configureJail()
    }

    @Override
    Properties getDefaultProperties() {
        centosPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }
}
