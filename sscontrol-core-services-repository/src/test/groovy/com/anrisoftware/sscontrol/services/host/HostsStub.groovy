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
package com.anrisoftware.sscontrol.services.host

import javax.inject.Inject

import com.anrisoftware.sscontrol.types.host.HostService
import com.anrisoftware.sscontrol.types.host.HostServiceProperties
import com.anrisoftware.sscontrol.types.host.HostServiceFactory
import com.anrisoftware.sscontrol.types.host.TargetHost
import com.anrisoftware.sscontrol.types.ssh.SshHost
import com.google.inject.assistedinject.Assisted

import groovy.transform.ToString

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@ToString
class HostsStub implements HostService {

    /**
     *
     *
     * @author Erwin Müller <erwin.mueller@deventm.de>
     * @version 1.0
     */
    interface HostsStubFactory {

        HostsStub create(Map<String, Object> args)
    }

    /**
     *
     *
     * @author Erwin Müller <erwin.mueller@deventm.de>
     * @version 1.0
     */
    @ToString
    static class HostsStubServiceImpl implements HostServiceFactory {

        @Inject
        HostsStubFactory serviceFactory

        @Override
        String getName() {
        }

        @Override
        HostService create(Map<String, Object> args) {
            serviceFactory.create(args)
        }
    }

    /**
     *
     *
     * @author Erwin Müller <erwin.mueller@deventm.de>
     * @version 1.0
     */
    static class Host {

        String host

        String address
    }

    List<Host> hosts

    List<SshHost> targets

    @Inject
    HostsStub(@Assisted Map<String, Object> args) {
        this.targets = args.targets
        this.hosts = []
    }

    void host(def name, def address) {
        hosts.add new Host(host: name, address: address)
    }

    List<Host> getHosts() {
        hosts;
    }

    @Override
    String getName() {
        'host'
    }

    @Override
    SshHost getTarget() {
        targets[0]
    }

    @Override
    List<TargetHost> getTargets() {
        targets
    }

    @Override
    HostServiceProperties getServiceProperties() {
    }
}
