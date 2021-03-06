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
package com.anrisoftware.sscontrol.ssh.service;

import com.anrisoftware.sscontrol.ssh.service.SshHostImpl.SshHostImplFactory;
import com.anrisoftware.sscontrol.ssh.service.SshImpl.SshImplFactory;
import com.anrisoftware.sscontrol.types.host.HostService;
import com.anrisoftware.sscontrol.types.ssh.SshHost;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * <i>Ssh</i> script module.
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class SshModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(HostService.class, SshImpl.class)
                .build(SshImplFactory.class));
        install(new FactoryModuleBuilder()
                .implement(SshHost.class, SshHostImpl.class)
                .build(SshHostImplFactory.class));
    }

}
