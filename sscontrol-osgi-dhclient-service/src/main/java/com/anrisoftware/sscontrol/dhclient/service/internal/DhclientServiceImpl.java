/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.dhclient.service.internal;

import static com.google.inject.util.Providers.of;

import java.util.Map;

import javax.inject.Inject;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.anrisoftware.globalpom.core.strings.ToStringService;
import com.anrisoftware.sscontrol.dhclient.service.external.Dhclient;
import com.anrisoftware.sscontrol.dhclient.service.external.DhclientService;
import com.anrisoftware.sscontrol.dhclient.service.internal.DhclientImpl.DhclientImplFactory;
import com.anrisoftware.sscontrol.types.host.external.HostServiceService;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * <i>dhclient</i> service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Component
@Service(HostServiceService.class)
public class DhclientServiceImpl implements DhclientService {

    @Inject
    private DhclientImplFactory dhclientFactory;

    @Reference
    private ToStringService toStringService;

    @Override
    public String getName() {
        return "dhclient";
    }

    @Override
    public Dhclient create(Map<String, Object> args) {
        return (Dhclient) dhclientFactory.create(args);
    }

    @Activate
    protected void start() {
        Guice.createInjector(new DhclientModule(), new AbstractModule() {

            @Override
            protected void configure() {
                bind(ToStringService.class).toProvider(of(toStringService));
            }
        }).injectMembers(this);
    }
}