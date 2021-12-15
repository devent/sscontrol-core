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
package com.anrisoftware.sscontrol.k8s.base.service;

import java.util.List;
import java.util.Map;

import com.anrisoftware.sscontrol.types.cluster.ClusterService;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.types.misc.DebugLogging;

/**
 * <i>K8s</i> service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface K8s extends ClusterService {

    DebugLogging getDebugLogging();

    /**
     * <pre>
     * target name: 'master'
     * </pre>
     */
    void target(Map<String, Object> args);

    /**
     * <pre>
     * debug "error", level: 1
     * </pre>
     */
    void debug(Map<String, Object> args, String name);

    /**
     * <pre>
     * debug name: "error", level: 1
     * </pre>
     */
    void debug(Map<String, Object> args);

    List<Object> getDebug();

    void addTargets(List<TargetHost> list);

    /**
     * Sets the cluster configuration.
     *
     * <pre>
     * cluster = [etcd: [external: [endpoints: "etcd-0.example.local", caFile: ...]]]
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void setCluster(Map<String, Object> args);

    /**
     * Returns or sets the cluster configuration.
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    Map<String, Object> getCluster();

    /**
     * Sets the cluster configuration.
     *
     * <pre>
     * cluster etcd: [external: [endpoints: "etcd-0.example.local", caFile: ...]]
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void cluster(Map<String, Object> args);

    /**
     * Sets the init configuration.
     *
     * <pre>
     * init = [localAPIEndpoint: [advertiseAddress: 1.2.3.4, bindPort: 6443]]
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void setInit(Map<String, Object> args);

    /**
     * Returns or sets the init configuration.
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    Map<String, Object> getInit();

    /**
     * Sets the init configuration.
     *
     * <pre>
     * init localAPIEndpoint: [advertiseAddress: 1.2.3.4, bindPort: 6443]
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void init(Map<String, Object> args);

    /**
     * Sets the join configuration.
     *
     * <pre>
     * join = [discovery: [bootstrapToken: [apiServerEndpoint: "kube-apiserver:6443", token: "abcdef.0123456789abcdef", unsafeSkipCAVerification: true]]]
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void setJoin(Map<String, Object> args);

    /**
     * Returns or sets the join configuration.
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    Map<String, Object> getJoin();

    /**
     * Sets the join configuration.
     *
     * <pre>
     * join discovery: [bootstrapToken: [apiServerEndpoint: "kube-apiserver:6443", token: "abcdef.0123456789abcdef", unsafeSkipCAVerification: true]]
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void join(Map<String, Object> args);

    /**
     * Sets the kubelet configuration.
     *
     * <pre>
     * kubelet = [address: "0.0.0.0", port: 10250]
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void setKubelet(Map<String, Object> args);

    /**
     * Returns or sets the kubelet configuration.
     *
     * <pre>
     * kubelet.address: "0.0.0.0"
     * kubelet.port: 10250
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    Map<String, Object> getKubelet();

    /**
     * Sets the kubelet configuration.
     *
     * <pre>
     * kubelet address: "0.0.0.0", port: 10250
     * </pre>
     *
     * @see <a href=
     *      "https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/control-plane-flags/">Customizing
     *      components with the kubeadm API</a>
     */
    void kubelet(Map<String, Object> args);

    /**
     * Returns labels for the node.
     */
    Map<String, Label> getLabels();

    /**
     * Returns taints for the node.
     */
    Map<String, Taint> getTaints();

    List<String> getProperty();

    /**
     * Adds node label.
     *
     * <pre>
     * label key: "muellerpublic.de/some", value: "foo"
     * </pre>
     */
    void label(Map<String, Object> args);

    /**
     * Adds node label.
     *
     * <pre>
     * label << 'name=value'
     * </pre>
     */
    List<String> getLabel();

    /**
     * Adds node taint.
     *
     * <pre>
     * taint key: "extra", value: "foo", effect: "aaa"
     * </pre>
     */
    void taint(Map<String, Object> args);

    /**
     * Adds node taint.
     *
     * <pre>
     * taint << "dedicated=mail:NoSchedule"
     * </pre>
     */
    List<String> getTaint();

}
