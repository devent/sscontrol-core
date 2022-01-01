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
package com.anrisoftware.sscontrol.utils.repo;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

import com.anrisoftware.sscontrol.utils.repo.BasicCredentialsImpl.BasicCredentialsImplFactory;
import com.anrisoftware.sscontrol.utils.repo.RemoteImpl.RemoteImplFactory;
import com.anrisoftware.sscontrol.utils.repo.SshCredentialsImpl.SshCredentialsImplFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * @see RemoteImplFactory
 * @see SshCredentialsImplFactory
 * @see BasicCredentialsImplFactory
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class UtilsRepoModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(Remote.class, RemoteImpl.class).build(RemoteImplFactory.class));
        bindCredentials();
    }

    private void bindCredentials() {
        install(new FactoryModuleBuilder().implement(Credentials.class, SshCredentialsImpl.class)
                .build(SshCredentialsImplFactory.class));
        install(new FactoryModuleBuilder().implement(Credentials.class, BasicCredentialsImpl.class)
                .build(BasicCredentialsImplFactory.class));
        MapBinder<String, CredentialsFactory> mapbinder = newMapBinder(binder(), String.class,
                CredentialsFactory.class);
        mapbinder.addBinding("ssh").to(SshCredentialsImplFactory.class);
        mapbinder.addBinding("basic").to(BasicCredentialsImplFactory.class);
    }

}
