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
package com.anrisoftware.sscontrol.k8sbase.script.upstream.k8s_1_2x.linux;

import javax.inject.Inject

import org.apache.commons.lang3.StringUtils
import org.joda.time.Duration

import com.anrisoftware.resources.templates.external.TemplateResource
import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.k8s.base.service.K8s
import com.anrisoftware.sscontrol.k8s.base.service.Taint
import com.anrisoftware.sscontrol.k8s.base.service.TaintFactory
import com.anrisoftware.sscontrol.k8s.kubectl.linux.kubectl_1_2x.AbstractKubectlLinux
import com.anrisoftware.sscontrol.utils.st.base64renderer.external.UriBase64Renderer

import groovy.util.logging.Slf4j

/**
 * Configures the K8s service of version >=1.20 from the upstream sources.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class AbstractK8sUpstreamLinux extends ScriptBase {

    TemplateResource kubeadmConfigTemplate

    @Inject
    AddressesFactory addressesFactory

    @Inject
    TaintFactory taintFactory

    @Inject
    void loadTemplates(TemplatesFactory templatesFactory) {
        def attr = [renderers: [new UriBase64Renderer()]]
        def templates = templatesFactory.create('K8s_1_2x_UpstreamLinuxTemplates', attr)
        this.kubeadmConfigTemplate = templates.getResource('kubeadm_config')
    }

    def setupMiscDefaults() {
        log.debug 'Setup misc defaults for {}', service
        K8s service = service
        if (!service.nodeName) {
            service.nodeName = service.target.hostName
        }
        if (!service.debugLogging.modules['debug']) {
            service.debug "debug", level: defaultLogLevel
        }
        if (!service.allowPrivileged) {
            service.privileged defaultAllowPrivileged
        }
        if (!service.allocateNodeCidrs) {
            service.allocateNodeCidrs defaultAllocateNodeCidrs
        }
        if (!service.podNetworkCidr) {
            if (defaultPodNetworkCidr) {
                service.podNetworkCidr defaultPodNetworkCidr
            }
        }
    }

    def setupLabelsDefaults() {
        log.debug 'Setup default labels for {}', service
        K8s service = service
        def name = service.nodeName
        def label = robobeeLabelNode
        service.label key: label, value: name
    }

    def installKube() {
        K8s service = service
        shell privileged: true, timeout: timeoutLong, """
if ! kubeadm token list; then
kubeadm init --config /root/kubeadm.yaml ${kubeadmArgs.join(" ")}
fi
""" call()
    }

    List getKubeadmArgs() {
        K8s service = service
        def list = []
        if (service.podNetworkCidr) {
            list << "--pod-network-cidr=${service.podNetworkCidr}"
        }
        return list
    }

    def joinNode() {
        K8s service = service
        def joinCommand = service.cluster.joinCommand
        assert StringUtils.isNotBlank(joinCommand) : "No join command for node ${target}"
        shell privileged: true, timeout: timeoutLong, """
if ! ls /etc/kubernetes/kubelet.conf&>/dev/null; then
${joinCommand} --node-name=${service.cluster.name} ${ignoreChecksErrors}
fi
""" call()
    }

    def setupKubectl() {
        shell """
mkdir -p \$HOME/.kube
sudo cp /etc/kubernetes/admin.conf \$HOME/.kube/config
sudo chown \$(id -u):\$(id -g) \$HOME/.kube/config
""" call()
    }

    /**
     * Wait until the node is in Ready state.
     */
    def waitNodeReady() {
        K8s service = service
        log.info 'Wait until the node is in Ready state: {}', service.nodeName
        def vars = [:]
        vars.timeout = timeoutMiddle
        kubectlCluster.waitNodeReady vars, service.nodeName
    }

    /**
     * Wait until the node is available.
     */
    def waitNodeAvailable() {
        K8s service = service
        log.info 'Wait until the node is available: {}', service.nodeName
        def vars = [:]
        vars.timeout = timeoutMiddle
        kubectlCluster.waitNodeAvailable vars, service.nodeName
    }

    def createKubeadmConfig() {
        log.info 'Create kubeadm configuration.'
        template privileged: true, resource: kubeadmConfigTemplate,
        name: 'kubeadmConfig', dest: "/root/kubeadm.yaml", vars: [:] call()
    }

    def createKubeletConfig() {
        log.info 'Create kubelet configuration.'
        template privileged: true, resource: kubeletConfigTemplate,
        name: 'kubeletConfig', dest: scriptProperties.kubelet_extra_conf, vars: [:] call()
    }

    def applyTaints() {
        K8s service = service
        def node = service.nodeName
        if (service.taints.isEmpty()) {
            log.info 'No taints to apply for node {}.', node
            return
        }
        log.info 'Apply node taints {} for {}.', service.taints, node
        kubectlCluster.applyNodeTaints([:], node, service.taints)
    }

    def applyLabels() {
        K8s service = service
        def node = service.nodeName
        if (service.labels.isEmpty()) {
            log.info 'No labels to apply for node {}.', node
            return
        }
        log.info 'Apply node labels {} for {}.', service.labels, node
        kubectlCluster.applyNodeLabels([:], node, service.labels)
    }

    def getJoinCommand() {
        shell privileged: true, outString: true, timeout: timeoutShort, """
token=\$(kubeadm token generate)
kubeadm token create \$token --print-join-command
""" call().out
    }

    /**
     * Returns the taint to mark a node as the master node and forbid the
     * scheduling of pods.
     */
    Taint getIsmasterNoScheduleTaint() {
        def key = getScriptProperty 'taint_key_ismaster'
        def effect = getScriptProperty 'taint_effect_ismaster'
        return taintFactory.create(key: key, value: null, effect: effect)
    }

    /**
     * Returns if there are node labels to be set after the node was registered.
     * See <a href="https://kubernetes.io/docs/admin/kubelet/">kubelet --node-labels mapStringString</a>
     */
    boolean getHaveLabels() {
        service.labels.size() > 0
    }

    /**
     * Returns node labels to be set after the node was registered.
     * See <a href="https://kubernetes.io/docs/admin/kubelet/">kubelet --node-labels mapStringString</a>
     */
    List getNodeLabels() {
        K8s service = service
        service.labels.inject([]) { list, k, v ->
            list << "${v.key}=${v.value}"
        }
    }

    /**
     * Returns if there are node taints to be set after the node was registered.
     * See <a href="https://kubernetes.io/docs/admin/kubelet/">kubelet --register-with-taints []api.Taint</a>
     */
    boolean getHaveTaints() {
        service.taints.size() > 0
    }

    /**
     * Returns node taints to be set after the node was registered.
     * See <a href="https://kubernetes.io/docs/admin/kubelet/">kubelet --register-with-taints []api.Taint</a>
     */
    List getNodeTaints() {
        def list = []
        K8s service = service
        service.taints.each { String key, Taint taint ->
            list << taintString(taint)
        }
        return list
    }

    String taintString(Taint taint) {
        "${taint.key}=${taint.value?taint.value:''}:${taint.effect}"
    }

    /**
     * Returns the run kubectl for the cluster.
     */
    abstract AbstractKubectlLinux getKubectlCluster()

    List getMasterHosts() {
        K8s service = service
        addressesFactory.create(service.cluster, service.cluster.apiServers).hosts
    }

    boolean getDefaultAllowPrivileged() {
        getScriptBooleanProperty "default_allow_privileged"
    }

    String getDefaultPodNetworkCidr() {
        def cidr = getScriptProperty "default_pod_network_cidr"
        if (StringUtils.isBlank(cidr)) {
            return null
        } else {
            return cidr
        }
    }

    boolean getDefaultAllocateNodeCidrs() {
        getScriptBooleanProperty "default_allocate_node_cidrs"
    }

    def getKubernetesVersion() {
        getScriptProperty 'kubernetes_version'
    }

    String getRobobeeLabelNode() {
        getScriptProperty 'robobee_label_node'
    }

    String getRobobeeLabelNamespace() {
        getScriptProperty 'robobee_label_namespace'
    }

    String getKubernetesLabelHostname() {
        getScriptProperty 'kubernetes_label_hostname'
    }

    Duration getKubectlTimeout() {
        getScriptDurationProperty 'kubectl_timeout'
    }

    File getKubectlCmd() {
        getFileProperty 'kubectl_cmd', binDir
    }

    /**
     * Returns the needed packages for kubeadm.
     *
     * <ul>
     * <li>profile property {@code kubeadm_packages}</li>
     * </ul>
     */
    List getKubeadmPackages() {
        getScriptListProperty "kubeadm_packages", ","
    }

    /**
     * Returns the needed package names to stop from being automatically updated or removed for kubeadm.
     *
     * <ul>
     * <li>profile property {@code kubeadm_hold}</li>
     * </ul>
     */
    List getKubeadmHold() {
        getScriptListProperty "kubeadm_hold", ","
    }

    /**
     * Returns the file path of the apt repository list file for Kubernetes,
     * for example {@code /etc/apt/sources.list.d/kubernetes.list}
     *
     * <ul>
     * <li>profile property {@code kubernetes_repository_list_file}</li>
     * </ul>
     */
    File getKubernetesRepositoryListFile() {
        getScriptFileProperty 'kubernetes_repository_list_file'
    }

    /**
     * Returns the url of the Kubernetes repository,
     * for example {@code https://apt.kubernetes.io/}
     *
     * <ul>
     * <li>profile property {@code kubernetes_repository_url}</li>
     * </ul>
     */
    String getKubernetesRepositoryUrl() {
        getScriptProperty 'kubernetes_repository_url'
    }

    /**
     * Returns the url of the Kubernetes repository signing key,
     * for example {@code https://packages.cloud.google.com/apt/doc/apt-key.gpg}
     *
     * <ul>
     * <li>profile property {@code kubernetes_repository_key}</li>
     * </ul>
     */
    String getKubernetesRepositoryKey() {
        getScriptProperty 'kubernetes_repository_key'
    }

    /**
     * Returns the url of the Kubernetes repository distribution name,
     * for example {@code kubernetes-xenial}
     *
     * <ul>
     * <li>profile property {@code kubernetes_repository_dist}</li>
     * </ul>
     */
    String getKubernetesRepositoryDist() {
        getScriptProperty 'kubernetes_repository_dist'
    }

    /**
     * Returns the url of the Kubernetes repository component,
     * for example {@code main}
     *
     * <ul>
     * <li>profile property {@code kubernetes_repository_component}</li>
     * </ul>
     */
    String getKubernetesRepositoryComponent() {
        getScriptProperty 'kubernetes_repository_component'
    }

    /**
     * Returns the Kubernetes repository keyring file,
     * for example {@code main}
     *
     * <ul>
     * <li>profile property {@code /usr/share/keyrings/kubernetes-archive-keyring.gpg}</li>
     * </ul>
     */
    File getKubernetesRepositoryKeyringFile() {
        getScriptFileProperty 'kubernetes_repository_keyring_file'
    }

    @Override
    def getLog() {
        log
    }
}
