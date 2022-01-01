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
package com.anrisoftware.sscontrol.repo.helm.service;

import static com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.stringListStatement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.anrisoftware.sscontrol.repo.helm.service.HelmRepoHostImpl.HelmRepoHostImplFactory;
import com.anrisoftware.sscontrol.types.host.HostServiceFactory;
import com.anrisoftware.sscontrol.types.host.HostServiceProperties;
import com.anrisoftware.sscontrol.types.host.HostServicePropertiesService;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.ListProperty;
import com.anrisoftware.sscontrol.types.repo.external.RepoHost;
import com.anrisoftware.sscontrol.utils.repo.Credentials;
import com.anrisoftware.sscontrol.utils.repo.CredentialsFactory;
import com.anrisoftware.sscontrol.utils.repo.Remote;
import com.anrisoftware.sscontrol.utils.repo.RemoteImpl.RemoteImplFactory;
import com.google.inject.assistedinject.Assisted;

import lombok.extern.slf4j.Slf4j;

/**
 * <i>Git</i> code repository service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
public class HelmRepoImpl implements HelmRepo {

    /**
     *
     *
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface HelmRepoImplFactory extends HostServiceFactory {

    }

    @Inject
    private transient Map<String, CredentialsFactory> credentialsFactories;

    private final HostServiceProperties serviceProperties;

    private final List<TargetHost> targets;

    private final HelmRepoHostImplFactory hostFactory;

    private Credentials credentials;

    private String group;

    private Remote remote;

    private final RemoteImplFactory remoteFactory;

    @Inject
    HelmRepoImpl(HostServicePropertiesService propertiesService, HelmRepoHostImplFactory hostFactory,
            RemoteImplFactory remoteFactory, @Assisted Map<String, Object> args) {
        this.serviceProperties = propertiesService.create();
        this.targets = new ArrayList<>();
        this.hostFactory = hostFactory;
        this.remoteFactory = remoteFactory;
        parseArgs(args);
    }

    /**
     * <pre>
     * property << 'name=value'
     * </pre>
     */
    public List<String> getProperty() {
        return stringListStatement(new ListProperty() {

            @Override
            public void add(String property) {
                serviceProperties.addProperty(property);
            }
        });
    }

    /**
     * <pre>
     * target "default"
     * </pre>
     */
    public void target(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("target", name);
        target(args);
    }

    /**
     * <pre>
     * target name: "default"
     * </pre>
     */
    public void target(Map<String, Object> args) {
        Object v = args.get("target");
        @SuppressWarnings("unchecked")
        List<TargetHost> l = InvokerHelper.asList(v);
        targets.addAll(l);
    }

    /**
     * <pre>
     * group "wordpress-app"
     * </pre>
     */
    public void group(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("group", name);
        group(args);
    }

    /**
     * <pre>
     * group name: "wordpress-app"
     * </pre>
     */
    public void group(Map<String, Object> args) {
        Object v = args.get("group");
        this.group = v.toString();
    }

    /**
     * <pre>
     * remote "git://git@github.com/fluentd-logging"
     * </pre>
     */
    public void remote(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("url", name);
        remote(args);
    }

    /**
     * <pre>
     * remote url: "git://git@github.com/fluentd-logging"
     * </pre>
     */
    public void remote(Map<String, Object> args) {
        this.remote = remoteFactory.create(args);
        log.debug("Remote set {}", remote);
    }

    /**
     * <pre>
     * credentials "ssh", key: "file://id_rsa.pub"
     * </pre>
     */
    public void credentials(Map<String, Object> args, String type) {
        Map<String, Object> a = new HashMap<>(args);
        a.put("type", type);
        credentials(a);
    }

    /**
     * <pre>
     * credentials type: "ssh", key: "file://id_rsa.pub"
     * </pre>
     */
    public void credentials(Map<String, Object> args) {
        Object type = args.get("type");
        assertThat("type=null", type, notNullValue());
        CredentialsFactory factory = credentialsFactories.get(type.toString());
        Credentials c = factory.create(args);
        this.credentials = c;
        log.debug("Credentials set {}", credentials);
    }

    @Override
    public TargetHost getTarget() {
        return getTargets().get(0);
    }

    public void addTargets(List<TargetHost> list) {
        this.targets.addAll(list);
    }

    @Override
    public List<TargetHost> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    @Override
    public HostServiceProperties getServiceProperties() {
        return serviceProperties;
    }

    @Override
    public String getName() {
        return "repo-helm";
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public Remote getRemote() {
        return remote;
    }

    @Override
    public List<RepoHost> getHosts() {
        List<RepoHost> list = new ArrayList<>();
        for (TargetHost ssh : targets) {
            HelmRepoHost host;
            host = hostFactory.create(this, ssh);
            list.add(host);
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("targets", getTargets()).toString();
    }

    private void parseArgs(Map<String, Object> args) {
        parseTargets(args);
        parseGroup(args);
    }

    @SuppressWarnings("unchecked")
    private void parseTargets(Map<String, Object> args) {
        Object v = args.get("targets");
        if (v != null) {
            targets.addAll((List<TargetHost>) v);
        }
    }

    private void parseGroup(Map<String, Object> args) {
        Object v = args.get("group");
        if (v != null) {
            this.group = v.toString();
        }
    }

}
