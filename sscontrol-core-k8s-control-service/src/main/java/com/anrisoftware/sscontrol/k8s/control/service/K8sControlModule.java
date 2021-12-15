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

import static com.google.inject.multibindings.MapBinder.newMapBinder;

import com.anrisoftware.sscontrol.k8s.control.service.AbacAuthorizationImpl.AbacAuthorizationImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.AccountImpl.AccountImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.AlwaysAllowAuthorizationImpl.AlwaysAllowAuthorizationImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.AlwaysDenyAuthorizationImpl.AlwaysDenyAuthorizationImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.BasicAuthenticationImpl.BasicAuthenticationImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.BindingImpl.BindingImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.ClientCertsAuthenticationImpl.ClientCertsAuthenticationImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.K8sControlImpl.K8sControlImplFactory;
import com.anrisoftware.sscontrol.types.host.HostService;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * Kubernetes control pane service module.
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class K8sControlModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(HostService.class, K8sControlImpl.class)
                .build(K8sControlImplFactory.class));
        install(new FactoryModuleBuilder().implement(Binding.class, BindingImpl.class).build(BindingImplFactory.class));
        install(new FactoryModuleBuilder().implement(Account.class, AccountImpl.class).build(AccountImplFactory.class));
        bindAuthentication();
        bindAuthorization();
    }

    private void bindAuthentication() {
        install(new FactoryModuleBuilder().implement(Authentication.class, ClientCertsAuthenticationImpl.class)
                .build(ClientCertsAuthenticationImplFactory.class));
        install(new FactoryModuleBuilder().implement(Authentication.class, BasicAuthenticationImpl.class)
                .build(BasicAuthenticationImplFactory.class));
        MapBinder<String, AuthenticationFactory> mapbinder = newMapBinder(binder(), String.class,
                AuthenticationFactory.class);
        mapbinder.addBinding("cert").to(ClientCertsAuthenticationImplFactory.class);
        mapbinder.addBinding("basic").to(BasicAuthenticationImplFactory.class);
    }

    private void bindAuthorization() {
        install(new FactoryModuleBuilder().implement(Authorization.class, AlwaysAllowAuthorizationImpl.class)
                .build(AlwaysAllowAuthorizationImplFactory.class));
        install(new FactoryModuleBuilder().implement(Authorization.class, AlwaysDenyAuthorizationImpl.class)
                .build(AlwaysDenyAuthorizationImplFactory.class));
        install(new FactoryModuleBuilder().implement(Authorization.class, AbacAuthorizationImpl.class)
                .build(AbacAuthorizationImplFactory.class));
        MapBinder<String, AuthorizationFactory> mapbinder = newMapBinder(binder(), String.class,
                AuthorizationFactory.class);
        mapbinder.addBinding("allow").to(AlwaysAllowAuthorizationImplFactory.class);
        mapbinder.addBinding("deny").to(AlwaysDenyAuthorizationImplFactory.class);
        mapbinder.addBinding("abac").to(AbacAuthorizationImplFactory.class);
    }

}
