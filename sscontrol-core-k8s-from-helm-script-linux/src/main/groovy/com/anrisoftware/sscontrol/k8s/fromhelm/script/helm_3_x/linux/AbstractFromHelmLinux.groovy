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
package com.anrisoftware.sscontrol.k8s.fromhelm.script.helm_3_x.linux

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

import javax.inject.Inject

import org.stringtemplate.v4.ST

import com.anrisoftware.resources.templates.external.TemplateResource
import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.k8s.fromhelm.service.FromHelm
import com.anrisoftware.sscontrol.repo.helm.script.linux.Helm_3_RepoUpstreamLinux

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j

/**
 * From Helm service for Kubernetes.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class AbstractFromHelmLinux extends ScriptBase {

    TemplateResource helmCmdTemplate

    @Inject
    void loadTemplates(TemplatesFactory templatesFactory) {
        def templates = templatesFactory.create('FromHelm_3_x_Linux_Templates')
        this.helmCmdTemplate = templates.getResource('helm_cmd')
    }

    /**
     * Returns the script to install Helm from the upstream sources.
     */
    abstract Helm_3_RepoUpstreamLinux getHelmRepoUpstream()

    /**
     * Setups defaults for the Helm service.
     */
    def setupDefaults() {
        FromHelm service = this.service
        if (!service.release.name) {
            service.release.name = parseName(service.chart)
        }
    }

    /**
     * Returns the name part of the chart. Giving the chart {@code 'stable/mariadb'} it will return the name {@code 'mariadb'}.
     */
    String parseName(String chart) {
        String[] split = chart.split('/')
        if (split.length > 1) {
            return split[1]
        } else {
            return split[0]
        }
    }

    /**
     * Downloads Helm.
     */
    def installHelm() {
        helmRepoUpstream.installHelm()
    }

    /**
     * Creates the Helm configuration.
     */
    def createConfig() {
        FromHelm service = this.service
        def file = createTmpFile()
        copyString str: JsonOutput.toJson(service.configYaml), dest: file
        return file
    }

    /**
     * Installs a chart from a specified source repository.
     */
    def fromRepo(Map args) {
        "from_${service.repo.type}_Repo"(args)
    }

    def from_git_Repo(Map args) {
        File dir = getState "${service.repo.type}-${service.repo.repo.group}-dir"
        assertThat "checkout-dir=null for $service", dir, notNullValue()
    }

    def from_helm_Repo(Map args) {
        FromHelm service = this.service
        assertThat "Helm repository exists for $service", repoExists, equalTo(true)
        shell resource: helmCmdTemplate, name: "helmUpdate", vars: [args: args, service: service], timeout: timeoutMiddle call()
        if (!service.chart.contains('/')) {
            service.chart = "${service.repo.repo.group}/${service.chart}"
        }
        if (service.wait) {
            waitChart args
        }
        fromChart args
    }

    /**
     * Waits for the chart to become available.
     */
    def waitChart(Map args) {
        FromHelm service = this.service
        assertThat "Helm release set for $service", service.release, notNullValue()
        assertThat "Helm name set for $service", service.release.name, not(emptyOrNullString())
        log.info 'Wait for chart: {}', service.chart
        def v = [:]
        v.resource = helmCmdTemplate
        v.timeout = timeoutMiddle
        v.vars = [args: args, service: service]
        v.name = "helmWait"
        shell v call()
    }

    /**
     * Installs a chart.
     */
    def fromChart(Map args) {
        FromHelm service = this.service
        assertThat "Helm release set for $service", service.release, notNullValue()
        assertThat "Helm name set for $service", service.release.name, not(emptyOrNullString())
        log.info 'Installing chart: {}', service.chart
        assertThat "args.config != null for $service", args.config, notNullValue()
        def v = [:]
        v.resource = helmCmdTemplate
        v.timeout = timeoutMiddle
        v.vars = [args: args, service: service]
        if (releaseExists) {
            v.name = "helmUpgradeCmd"
        } else {
            v.name = "helmInstallCmd"
        }
        shell v call()
    }

    /**
     * Checks if a release exists.
     */
    boolean isReleaseExists() {
        FromHelm service = this.service
        def v = [:]
        v.resource = helmCmdTemplate
        v.name = "helmReleaseCmd"
        v.timeout = timeoutShort
        v.exitCodes = [0, 1] as int[]
        v.vars = [service: service, status: "(deployed)|(failed)"]
        def p = shell v call()
        return p.exitValue == 0
    }

    /**
     * Checks if a Helm repository exists.
     */
    boolean isRepoExists() {
        FromHelm service = this.service
        def v = [:]
        v.resource = helmCmdTemplate
        v.name = "helmCheckRepo"
        v.timeout = timeoutShort
        v.exitCodes = [0, 1] as int[]
        v.vars = [service: service, repoName: service.repo.repo.group]
        def p = shell v call()
        return p.exitValue == 0
    }

    /**
     * Returns the upstream source URL, for example {@code "https://get.helm.sh/helm-<version>-linux-amd64.tar.gz"}.
     * <ul>
     * <li>property {@code "helm_upstream_source_archive"}
     * <li>variable {@code "<version>"} is replaced by the Helm version.
     * </ul>
     */
    String getUpstreamSourceArchive() {
        def p = getScriptProperty "helm_upstream_source_archive"
        new ST(p).add("version", version).render()
    }

    /**
     * Returns the upstream source shasum URL, for example {@code "https://get.helm.sh/helm-<version>-linux-amd64.tar.gz.sha256sum"}.
     * <ul>
     * <li>property {@code "helm_upstream_source_shasum"}
     * <li>variable {@code "<version>"} is replaced by the Helm version.
     * </ul>
     */
    String getUpstreamSourceShasum() {
        def p = getScriptProperty "helm_upstream_source_shasum"
        new ST(p).add("version", version).render()
    }

    /**
     * Returns the upstream Helm version, for example {@code "v3.7.2"}.
     * <p>
     * Property {@code "helm_version"}
     */
    String getVersion() {
        getScriptProperty "helm_version"
    }

    /**
     * Returns the bin directory, for example {@code "/usr/local/bin"}.
     * <p>
     * Property {@code "bin_dir"}
     */
    File getBinDir() {
        getScriptFileProperty "bin_dir"
    }

    @Override
    def getLog() {
        log
    }
}
