/*-
 * #%L
 * sscontrol-osgi - hostname-script-debian
 * %%
 * Copyright (C) 2016 - 2018 Advanced Natural Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.anrisoftware.sscontrol.hostname.script.debian.external

import com.anrisoftware.sscontrol.hostname.script.systemd.external.Hostname_Systemd
import com.anrisoftware.sscontrol.utils.debian.external.DebianUtils

import groovy.util.logging.Slf4j

/**
 * Configures the <i>hostname</i> on Debian systems.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Hostname_Debian extends Hostname_Systemd {

    abstract DebianUtils getDebian()

    @Override
    def run() {
        installPackages()
        restartService()
    }

    void installPackages() {
        log.info "Installing packages {}.", packages
        debian.installPackages()
    }

    @Override
    def getLog() {
        log
    }
}
