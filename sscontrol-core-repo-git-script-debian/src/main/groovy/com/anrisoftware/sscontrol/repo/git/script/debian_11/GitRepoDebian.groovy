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
package com.anrisoftware.sscontrol.repo.git.script.debian_11

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.repo.git.script.linux.GitRepoLinux
import com.anrisoftware.sscontrol.utils.debian.DebianUtils
import com.anrisoftware.sscontrol.utils.debian.Debian_11_UtilsFactory

import groovy.util.logging.Slf4j

/**
 * <i>Git</i> code repository service for Debian.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class GitRepoDebian extends GitRepoLinux {

    @Inject
    GitRepoDebianProperties debianPropertiesProvider

    DebianUtils debian

    @Inject
    void setDebianFactory(Debian_11_UtilsFactory factory) {
        this.debian = factory.create(this)
    }

    @Override
    def run() {
        debian.installPackages()
        super.run()
    }

    @Override
    ContextProperties getDefaultProperties() {
        debianPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }
}
