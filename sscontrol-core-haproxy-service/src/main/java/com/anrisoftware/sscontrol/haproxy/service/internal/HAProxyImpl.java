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
package com.anrisoftware.sscontrol.haproxy.service.internal;

import static com.anrisoftware.sscontrol.types.misc.external.StringListPropertyUtil.stringListStatement;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.anrisoftware.sscontrol.haproxy.service.external.HAProxy;
import com.anrisoftware.sscontrol.haproxy.service.external.Proxy;
import com.anrisoftware.sscontrol.haproxy.service.internal.ProxyImpl.ProxyImplFactory;
import com.anrisoftware.sscontrol.types.host.HostServiceProperties;
import com.anrisoftware.sscontrol.types.host.HostServicePropertiesService;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.types.misc.external.StringListPropertyUtil.ListProperty;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * HAProxy service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public class HAProxyImpl implements HAProxy {

    private static final String SERVICE_NAME = "haproxy";

    private final HAProxyImplLogger log;

    private final HostServiceProperties serviceProperties;

    private final List<TargetHost> targets;

    private final List<Proxy> proxies;

    private String version;

    @Inject
    private transient ProxyImplFactory proxyFactory;

    @AssistedInject
    HAProxyImpl(HAProxyImplLogger log, HostServicePropertiesService propertiesService,
            @Assisted Map<String, Object> args) {
        this.log = log;
        this.targets = new ArrayList<>();
        this.serviceProperties = propertiesService.create();
        this.proxies = new ArrayList<>();
        parseArgs(args);
    }

    /**
     * <pre>
     * proxy "http"
     * proxy "http" with { }
     * </pre>
     */
    public Proxy proxy(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        return proxy(args);
    }

    /**
     * <pre>
     * proxy name: "http"
     * proxy name: "http" with { }
     * </pre>
     */
    public Proxy proxy(Map<String, Object> args) {
        Proxy proxy = proxyFactory.create(args);
        proxies.add(proxy);
        log.exportAdded(this, proxy);
        return proxy;
    }

    @Override
    public String getName() {
        return format("%s-%s", SERVICE_NAME, version);
    }

    @Override
    public TargetHost getTarget() {
        return getTargets().get(0);
    }

    @Override
    public List<TargetHost> getTargets() {
        return targets;
    }

    public List<String> getProperty() {
        return stringListStatement(new ListProperty() {

            @Override
            public void add(String property) {
                serviceProperties.addProperty(property);
            }
        });
    }

    @Override
    public HostServiceProperties getServiceProperties() {
        return serviceProperties;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public List<Proxy> getProxies() {
        return proxies;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("hosts", targets).toString();
    }

    private void parseArgs(Map<String, Object> args) {
        parseVersion(args);
        parseTargets(args);
    }

    @SuppressWarnings("unchecked")
    private void parseTargets(Map<String, Object> args) {
        Object v = args.get("targets");
        if (v != null) {
            targets.addAll((List<TargetHost>) v);
        }
    }

    private void parseVersion(Map<String, Object> args) {
        Object v = args.get("version");
        this.version = v.toString();
    }

}
