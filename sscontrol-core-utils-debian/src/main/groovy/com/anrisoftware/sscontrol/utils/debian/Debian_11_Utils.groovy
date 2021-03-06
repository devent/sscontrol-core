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
package com.anrisoftware.sscontrol.utils.debian

import javax.inject.Inject

import com.anrisoftware.resources.templates.external.TemplateResource
import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.types.host.HostServiceScript
import com.google.inject.assistedinject.Assisted

/**
 * Debian 11 utilities.
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
class Debian_11_Utils extends DebianUtils {

    @Inject
    Debian_11_Properties propertiesProvider

    TemplateResource commandsTemplate

    @Inject
    Debian_11_Utils(@Assisted HostServiceScript script) {
        super(script)
    }

    @Inject
    void loadTemplates(TemplatesFactory templatesFactory) {
        def templates = templatesFactory.create('DebianUtils')
        this.commandsTemplate = templates.getResource('debian_11_commands')
    }

    @Override
    TemplateResource getCommandsTemplate() {
        commandsTemplate
    }

    @Override
    public Properties getDefaultProperties() {
        propertiesProvider.get()
    }
}
