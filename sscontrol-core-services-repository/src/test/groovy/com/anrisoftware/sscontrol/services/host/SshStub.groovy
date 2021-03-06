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

import org.apache.commons.lang3.StringUtils

import com.anrisoftware.sscontrol.types.host.HostService
import com.anrisoftware.sscontrol.types.host.HostServiceProperties
import com.anrisoftware.sscontrol.types.host.TargetHost
import com.anrisoftware.sscontrol.types.misc.DebugLogging
import com.anrisoftware.sscontrol.types.ssh.Ssh
import com.anrisoftware.sscontrol.types.ssh.SshHost
import com.anrisoftware.sscontrol.types.ssh.TargetServiceService
import com.google.inject.assistedinject.Assisted

import groovy.transform.ToString

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@ToString
class SshStub implements Ssh {

    /**
     *
     *
     * @author Erwin Müller <erwin.mueller@deventm.de>
     * @version 1.0
     */
    interface SshStubFactory {

        SshStub create(Map<String, Object> args)
    }

    /**
     *
     *
     * @author Erwin Müller <erwin.mueller@deventm.de>
     * @version 1.0
     */
    @ToString
    static class SshStubServiceImpl implements TargetServiceService {

        @Inject
        SshStubFactory serviceFactory

        @Override
        String getName() {
        }

        @Override
        HostService create(Map<String, Object> args) {
            serviceFactory.create(args)
        }
    }

    String group

    List<SshHost> targets

    List<SshHost> hosts

    @Inject
    SshStub(@Assisted Map<String, Object> args) {
        this.targets = args.targets ? args.targets : []
        this.hosts = []
    }

    void group(String name) {
        this.group = name
    }

    String getGroup() {
        if (StringUtils.isEmpty(group)) {
            return "default"
        } else {
            return group
        }
    }

    void host(String name) {
        hosts.add([
            getHost: { name }
        ] as SshHost)
    }

    @Override
    String getName() {
        'ssh'
    }

    @Override
    List<SshHost> getHosts() {
        hosts
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

    @Override
    DebugLogging getDebugLogging() {
        return null
    }
}
