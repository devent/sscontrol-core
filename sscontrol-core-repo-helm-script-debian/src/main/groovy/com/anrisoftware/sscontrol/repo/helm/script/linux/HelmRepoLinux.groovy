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
package com.anrisoftware.sscontrol.repo.helm.script.linux

import javax.inject.Inject

import com.anrisoftware.resources.templates.external.TemplateResource
import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.repo.helm.service.HelmRepo

import groovy.util.logging.Slf4j

/**
 * <i>Helm</i> repository service for Linux.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class HelmRepoLinux extends ScriptBase {

    TemplateResource helmRepoCommandsTemplate

    @Inject
    void loadTemplates(TemplatesFactory templatesFactory) {
        def templates = templatesFactory.create('HelmRepoLinuxTemplates')
        this.helmRepoCommandsTemplate = templates.getResource('helm_repo_cmds')
    }

    @Override
    def run() {
        HelmRepo service = this.service
        shell resource: helmRepoCommandsTemplate, name: 'helmRepoAdd' call()
    }

    @Override
    def getLog() {
        log
    }
}
