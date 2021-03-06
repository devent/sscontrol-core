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
package com.anrisoftware.sscontrol.k8s.control.service;

import java.util.List;
import java.util.Map;

import com.anrisoftware.sscontrol.k8s.base.service.K8s;
import com.anrisoftware.sscontrol.tls.Tls;

/**
 * Kubernetes control pane service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface K8sControl extends K8s {

    void setClusterName(String clusterName);

    String getClusterName();

    List<Authentication> getAuthentications();

    List<Authorization> getAuthorizations();

    List<String> getAdmissions();

    /**
     * Returns the address and port for the api-server.
     *
     * @return {@link Binding}
     */
    Binding getBinding();

    Account getAccount();

    /**
     * Returns the list of Kubernetes nodes.
     */
    List<Object> getNodes();

    /**
     * Set allow privileged containers.
     *
     * <pre>
     * privileged true
     * </pre>
     */
    void privileged(Boolean allow);

    void setAllowPrivileged(boolean allow);

    Boolean isAllowPrivileged();

    /**
     * Sets the pod network CIDR.
     *
     * <pre>
     * podNetworkCidr "192.168.0.0/16"
     * </pre>
     */
    void podNetworkCidr(String cidr);

    void setPodNetworkCidr(String cidr);

    String getPodNetworkCidr();

    /**
     * Sets should CIDRs for Pods be allocated and set on the cloud provider.
     *
     * <pre>
     * allocateNodeCidrs true
     * </pre>
     */
    void allocateNodeCidrs(boolean allocate);

    void setAllocateNodeCidrs(Boolean allocate);

    Boolean isAllocateNodeCidrs();

    /**
     * Returns the CA certificates for signing generated TLS certificates.
     */
    Tls getCa();

    /**
     * Sets the CA certificates for signing generated TLS certificates.
     *
     * <pre>
     * ca ca: "ca.pem", key: "key.pem"
     * </pre>
     */
    void ca(Map<String, Object> args);

}
