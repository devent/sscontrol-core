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
package com.anrisoftware.sscontrol.fail2ban.script.debian_11;

import static com.google.inject.Guice.createInjector;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.anrisoftware.sscontrol.types.host.HostService;
import com.anrisoftware.sscontrol.types.host.HostServiceScript;
import com.anrisoftware.sscontrol.types.host.HostServiceScriptFactory;
import com.anrisoftware.sscontrol.types.host.HostServices;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
@Component(service = HostServiceScriptFactory.class)
public class Fail2ban_Debian_11_Service implements HostServiceScriptFactory {

    static final String SYSTEM_VERSION = "11";

    static final String SYSTEM_NAME = "debian";

    @Inject
    private Fail2ban_Debian_11_Factory fail2banFactory;

    public String getSystemName() {
        return SYSTEM_NAME;
    }

    public String getSystemVersion() {
        return SYSTEM_VERSION;
    }

    @Override
    public HostServiceScript create(HostServices repository, HostService service, TargetHost target,
            ExecutorService threads, Map<String, Object> env) {
        return fail2banFactory.create(repository, service, target, threads, env);
    }

    @Activate
    protected void start() {
        createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                install(new FactoryModuleBuilder().implement(HostServiceScript.class, Fail2ban_Debian_11.class)
                        .build(Fail2ban_Debian_11_Factory.class));
            }
        }).injectMembers(this);
    }

}
