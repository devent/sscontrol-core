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

import com.anrisoftware.resources.templates.external.TemplateResource
import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.haproxy.service.HAProxy
import com.anrisoftware.sscontrol.haproxy.service.Proxy

import groovy.util.logging.Slf4j

/**
 * HAProxy 2.x.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class HAProxy_2_x extends ScriptBase {

    TemplateResource exportsTemplate

    @Inject
    Map<String, ApplyProxyDefaults> applyProxyDefaults

    Map<String, TemplateResource> proxyConfigResources

    @Inject
    void loadTemplates(TemplatesFactory templatesFactory) {
        def templates = templatesFactory.create('HAProxy_2_x_Templates')
        this.exportsTemplate = templates.getResource('haproxy_config')
        this.proxyConfigResources = [:]
        proxyConfigResources.http = templates.getResource('http_config')
        proxyConfigResources.https = templates.getResource('https_config')
        proxyConfigResources.ssh = templates.getResource('ssh_config')
    }

    /**
     * Setups the default options for hosts without options.
     */
    def setupDefaultOptions() {
        HAProxy service = this.service
        service.proxies.each { Proxy proxy ->
            applyProxyDefaults[proxy.name].applyDefaults(this, proxy)
        }
    }

    /**
     * Deploys the configuration.
     */
    def deployConfig() {
        HAProxy service = this.service
        template privileged: true, resource: exportsTemplate, name: 'haproxyConfig', dest: configFile call()
        def dir = new File(configDir, "conf.d/")
        service.proxies.each { Proxy proxy ->
            shell privileged: true, "mkdir -p ${dir}" call()
            def file = new File(dir, "${proxy.name}.cfg")
            def t = proxyConfigResources[proxy.name]
            template privileged: true, resource: t, name: 'haproxyConfig', vars: [proxy: proxy], dest: file call()
        }
        shell privileged: true, "cat ${dir}/*.cfg >> ${configFile}" call()
    }

    /**
     * Deploys the dhparam file.
     */
    def deployDhparam() {
        HAProxy service = this.service
        template privileged: true, resource: exportsTemplate, name: 'haparamFile', dest: dhparamFile call()
    }

    /**
     * Returns the path of the dhparam file.
     */
    File getDhparamFile() {
        getScriptFileProperty "dhparam_file", configDir
    }

    @Override
    def getLog() {
        log
    }
}
