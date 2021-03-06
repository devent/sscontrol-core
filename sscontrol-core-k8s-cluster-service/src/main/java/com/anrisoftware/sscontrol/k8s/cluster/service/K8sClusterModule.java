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
package com.anrisoftware.sscontrol.k8s.cluster.service;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

import com.anrisoftware.sscontrol.k8s.cluster.service.ClusterImpl.ClusterImplFactory;
import com.anrisoftware.sscontrol.k8s.cluster.service.CredentialsAnonImpl.CredentialsAnonImplFactory;
import com.anrisoftware.sscontrol.k8s.cluster.service.CredentialsCertImpl.CredentialsCertImplFactory;
import com.anrisoftware.sscontrol.types.cluster.Credentials;
import com.anrisoftware.sscontrol.types.host.HostService;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Kubernetes control script module.
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class K8sClusterModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(HostService.class, K8sClusterImpl.class)
                .build(K8sClusterFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Cluster.class, ClusterImpl.class)
                .build(ClusterImplFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Context.class, ContextImpl.class)
                .build(ContextFactory.class));
        install(new FactoryModuleBuilder()
                .implement(K8sClusterHost.class, K8sClusterHostImpl.class)
                .build(K8sClusterHostFactory.class));
        bindCredentials();
    }

    private void bindCredentials() {
        install(new FactoryModuleBuilder()
                .implement(Credentials.class, CredentialsCertImpl.class)
                .build(CredentialsCertImplFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Credentials.class, CredentialsAnonImpl.class)
                .build(CredentialsAnonImplFactory.class));
        MapBinder<String, CredentialsFactory> mapbinder = newMapBinder(binder(),
                String.class, CredentialsFactory.class);
        mapbinder.addBinding("cert").to(CredentialsCertImplFactory.class);
        mapbinder.addBinding("anon").to(CredentialsAnonImplFactory.class);
    }

}
