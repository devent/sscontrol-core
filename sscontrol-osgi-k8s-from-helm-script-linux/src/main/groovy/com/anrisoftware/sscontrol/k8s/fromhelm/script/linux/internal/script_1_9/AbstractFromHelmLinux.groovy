/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.k8s.fromhelm.script.linux.internal.script_1_9

import static org.apache.commons.io.FilenameUtils.getBaseName
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

import javax.inject.Inject

import org.apache.commons.io.FilenameUtils
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

import com.anrisoftware.resources.templates.external.TemplateResource
import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.groovy.script.external.ScriptBase
import com.anrisoftware.sscontrol.k8s.fromhelm.service.external.FromHelm

import groovy.util.logging.Slf4j

/**
 * From Helm service for Kubernetes 1.9.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class AbstractFromHelmLinux extends ScriptBase {

	TemplateResource helmCmdTemplate

	@Inject
	void loadTemplates(TemplatesFactory templatesFactory) {
		def templates = templatesFactory.create('FromHelm_1_9_Linux_Templates')
		this.helmCmdTemplate = templates.getResource('helm_cmd')
	}

	/**
	 * Initializes Helm.
	 */
	def initHelm() {
		log.info 'Initializes Helm.'
		shell timeout: timeoutMiddle, """\
kubectl -n kube-system create -f - << EOF
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRoleBinding
metadata:
  name: tiller
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
  - kind: ServiceAccount
    name: tiller
    namespace: kube-system
EOF
helm init --service-account tiller
helm repo update
		""" call()
	}

	/**
	 * Downloads Helm.
	 */
	def installHelm() {
		log.info 'Installs Helm.'
		copy src: archive, hash: archiveHash, dest: "/tmp", direct: true, timeout: timeoutLong call()
		def archiveFile = FilenameUtils.getName(archive.toString())
		def archiveName = getBaseName(getBaseName(archive.toString()))
		shell timeout: timeoutMiddle, """\
        cd /tmp
        tar xf "$archiveFile"
        cd "linux-amd64"
        sudo find . -executable -type f -exec cp '{}' '$binDir' \\;
        sudo chmod o+rx '$binDir'/*
		""" call()
	}

	/**
	 * Creates the Helm configuration.
	 */
	def createConfig() {
		FromHelm service = this.service
		def file = createTmpFile()
		def options = new DumperOptions()
		options.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
		copyString str: new Yaml(options).dump(service.configYaml), dest: file
		return file
	}

	/**
	 * Installs a chart from a specified source repository.
	 */
	def fromRepo(Map args) {
		File dir = getState "${service.repo.type}-${service.repo.repo.group}-dir"
		assertThat "checkout-dir=null for $service", dir, notNullValue()
	}

	/**
	 * Installs a chart.
	 */
	def fromChart(Map args) {
		FromHelm service = this.service
		log.info 'Installing chart: {}', service.chart
		assertThat "args.config != null for $service", args.config, notNullValue()
		def v = [:]
		v.resource = helmCmdTemplate
		v.name = "helmInstallCmd"
		v.timeout = timeoutMiddle
		v.vars = [args: args, service: service]
		shell v call()
	}

	@Override
	def getLog() {
		log
	}
}
