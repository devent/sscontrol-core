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
package com.anrisoftware.sscontrol.cilium.script.cilium_1_x

import javax.inject.Inject

import com.anrisoftware.sscontrol.cilium.service.Cilium
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.types.ssh.TargetsAddressListFactory
import com.anrisoftware.sscontrol.types.ssh.TargetsListFactory
import com.anrisoftware.sscontrol.utils.ufw.linux.UfwLinuxUtilsFactory
import com.anrisoftware.sscontrol.utils.ufw.linux.UfwUtils

import groovy.util.logging.Slf4j

/**
 * Configures the Ufw firewall.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Abstract_Cilium_Ufw_Linux extends ScriptBase {

    UfwUtils ufw

    @Inject
    TargetsListFactory nodesFactory

    @Inject
    TargetsAddressListFactory addressesFactory

    @Inject
    void setUfwUtilsFactory(UfwLinuxUtilsFactory factory) {
        this.ufw = factory.create this
    }

    @Override
    Object run() {
        if (!ufwAvailable) {
            return
        }
        openPublicPorts()
        openNodesPorts()
    }

    /**
     * Checks that UFW client is available.
     */
    boolean isUfwAvailable() {
        if (!ufw.active) {
            log.debug 'No Ufw available, nothing to do.'
            return false
        } else {
            return true
        }
    }

    /**
     * Opens the public ports.
     */
    def openPublicPorts() {
        Cilium service = this.service
        nodesAddresses.each {
            ufw.allowPortsToNetwork publicTcpPorts, it, this
        }
    }

    /**
     * Opens the ports between nodes.
     */
    def openNodesPorts() {
        Cilium service = this.service
        ufw.allowTcpPortsOnNodes nodes, nodesAddresses, nodesTcpPorts, this
        ufw.allowUdpPortsOnNodes nodes, nodesAddresses, nodesUdpPorts, this
        nodes.each {
            ufw.allowFromToPorts it, clusterPoolIpv4Didr, it.hostAddress, podsPorts, "tcp", this
        }
    }

    List getPublicTcpPorts() {
        getScriptListProperty 'public_tcp_ports'
    }

    List getPublicUdpPorts() {
        getScriptListProperty 'public_udp_ports'
    }

    List getNodesTcpPorts() {
        getScriptListProperty 'nodes_tcp_ports'
    }

    List getNodesUdpPorts() {
        getScriptListProperty 'nodes_udp_ports'
    }

    List getPodsPorts() {
        getScriptListProperty 'pods_ports'
    }

    String getClusterPoolIpv4Didr() {
        getScriptProperty 'cluster_pool_ipv4_cidr'
    }

    List getNodes() {
        Cilium service = this.service
        nodesFactory.create(service, scriptsRepository, "nodes", this).nodes
    }

    List getNodesAddresses() {
        Cilium service = this.service
        addressesFactory.create(service, scriptsRepository, "nodes", this).nodes
    }

    @Override
    def getLog() {
        log
    }
}
