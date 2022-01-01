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
package com.anrisoftware.sscontrol.shell.script.linux

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.shell.service.Shell

/**
 * <i>Shell</i> service for Linux.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
class Shell_Linux extends ScriptBase {

    @Inject
    Shell_Linux_Properties linuxPropertiesProvider

    @Override
    def run() {
        Shell service = service
        service.scripts.each {
            shell it.vars call()
        }
    }

    @Override
    ContextProperties getDefaultProperties() {
        linuxPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }
}
