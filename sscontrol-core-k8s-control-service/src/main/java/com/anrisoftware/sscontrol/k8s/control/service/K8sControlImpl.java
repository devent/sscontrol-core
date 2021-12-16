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

import static com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.stringListStatement;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.anrisoftware.globalpom.core.arrays.ToList;
import com.anrisoftware.sscontrol.k8s.base.service.K8s;
import com.anrisoftware.sscontrol.k8s.base.service.K8sImpl.K8sImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.AccountImpl.AccountImplFactory;
import com.anrisoftware.sscontrol.k8s.control.service.BindingImpl.BindingImplFactory;
import com.anrisoftware.sscontrol.tls.Tls;
import com.anrisoftware.sscontrol.tls.Tls.TlsFactory;
import com.anrisoftware.sscontrol.types.host.HostServiceFactory;
import com.anrisoftware.sscontrol.types.misc.GeneticListPropertyUtil;
import com.anrisoftware.sscontrol.types.misc.GeneticListPropertyUtil.GeneticListProperty;
import com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.ListProperty;
import com.google.inject.assistedinject.Assisted;

import lombok.experimental.Delegate;

/**
 * Kubernetes control pane service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public class K8sControlImpl implements K8sControl {

    /**
     *
     *
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface K8sControlImplFactory extends HostServiceFactory {

    }

    private final K8sControlImplLogger log;

    @Inject
    private transient Map<String, AuthenticationFactory> authenticationFactories;

    @Inject
    private transient Map<String, AuthorizationFactory> authorizationFactories;

    private final List<Authentication> authentications;

    private final List<Authorization> authorizations;

    private final List<String> admissions;

    private Binding binding;

    private transient BindingImplFactory bindingFactory;

    private transient AccountImplFactory accountFactory;

    private Account account;

    @Delegate
    private final K8s k8s;

    private final List<Object> nodes;

    private Tls ca;

    private final TlsFactory tlsFactory;

    private String podNetworkCidr;

    @Inject
    K8sControlImpl(K8sControlImplLogger log, K8sImplFactory k8sFactory, BindingImplFactory bindingFactory,
            AccountImplFactory accountFactory, TlsFactory tlsFactory, @Assisted Map<String, Object> args) {
        this.log = log;
        this.k8s = (K8s) k8sFactory.create("k8s-control", args);
        this.authentications = new ArrayList<>();
        this.authorizations = new ArrayList<>();
        this.admissions = new ArrayList<>();
        this.binding = bindingFactory.create();
        this.bindingFactory = bindingFactory;
        this.accountFactory = accountFactory;
        this.account = accountFactory.create();
        this.nodes = new ArrayList<>();
        this.tlsFactory = tlsFactory;
        this.ca = tlsFactory.create();
        parseArgs(args);
    }

    /**
     * <pre>
     * bind insecure: "127.0.0.1", secure: "0.0.0.0", port: 8080
     * </pre>
     */
    public void bind(Map<String, Object> args) {
        Map<String, Object> a = new HashMap<>(args);
        this.binding = bindingFactory.create(a);
        log.bindingSet(this, binding);
    }

    @Override
    public void ca(Map<String, Object> args) {
        this.ca = tlsFactory.create(args);
        log.caSet(this, ca);
    }

    /**
     * <pre>
     * authentication "basic", file: "some_file"
     * </pre>
     */
    public void authentication(Map<String, Object> args, String name) {
        Map<String, Object> a = new HashMap<>(args);
        a.put("type", name);
        authentication(a);
    }

    /**
     * <pre>
     * authentication type: "basic", file: "some_file"
     * </pre>
     */
    public void authentication(Map<String, Object> args) {
        String name = args.get("type").toString();
        AuthenticationFactory factory = authenticationFactories.get(name);
        assertThat(format("authentication(%s)=null", name), factory, is(notNullValue()));
        Authentication auth = factory.create(args);
        authentications.add(auth);
        log.authenticationAdded(this, auth);
    }

    /**
     * <pre>
     * authorization "allow"
     * </pre>
     */
    public void authorization(String name) {
        Map<String, Object> a = new HashMap<>();
        a.put("mode", name);
        authorization(a);
    }

    /**
     * <pre>
     * authorization "abac", file: "policy_file.json"
     * </pre>
     */
    public void authorization(Map<String, Object> args, String name) {
        Map<String, Object> a = new HashMap<>(args);
        a.put("mode", name);
        authorization(a);
    }

    /**
     * <pre>
     * authorization mode: "abac", abac: ""
     * </pre>
     */
    public void authorization(Map<String, Object> args) {
        String name = args.get("mode").toString();
        AuthorizationFactory factory = authorizationFactories.get(name);
        assertThat(format("authorization(%s)=null", name), factory, is(notNullValue()));
        Authorization auth = factory.create(args);
        authorizations.add(auth);
        log.authorizationAdded(this, auth);
    }

    /**
     * <pre>
     * admission << "AlwaysAdmit,ServiceAccount"
     * </pre>
     */
    public List<String> getAdmission() {
        return stringListStatement(new ListProperty() {

            @Override
            public void add(String property) {
                String[] split = StringUtils.split(property, ",");
                ToList.toList(admissions, split);
                log.admissionsAdded(K8sControlImpl.this, property);
            }
        });
    }

    /**
     * <pre>
     * account ca: 'ca.pem', key: 'ca_key.pem' // or
     * </pre>
     */
    public void account(Map<String, Object> args) {
        this.account = accountFactory.create(args);
        log.accountSet(this, account);
    }

    /**
     * <pre>
     * node &lt;&lt; 'node0.test'
     * node &lt;&lt; nodes
     * </pre>
     */
    public List<Object> getNode() {
        return GeneticListPropertyUtil.<Object>geneticListStatement(new GeneticListProperty<Object>() {

            @Override
            public void add(Object property) {
                addNode(property);
            }
        });
    }

    /**
     * <pre>
     * podNetworkCidr "10.217.0.0/16"
     * </pre>
     */
    public void podNetworkCidr(String podNetworkCidr) {
        setPodNetworkCidr(podNetworkCidr);
    }

    public void setPodNetworkCidr(String podNetworkCidr) {
        this.podNetworkCidr = podNetworkCidr;
    }

    @Override
    public String getPodNetworkCidr() {
        return podNetworkCidr;
    }

    @Override
    public Binding getBinding() {
        return binding;
    }

    @Override
    public List<Authentication> getAuthentications() {
        return authentications;
    }

    @Override
    public List<Authorization> getAuthorizations() {
        return authorizations;
    }

    @Override
    public List<String> getAdmissions() {
        return admissions;
    }

    @Override
    public Tls getCa() {
        return ca;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    public void addNode(Object node) {
        nodes.add(node);
        log.nodeAdded(this, node);
    }

    @Override
    public List<Object> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("targets", getTargets()).toString();
    }

    private void parseArgs(Map<String, Object> args) {
        parseNodes(args);
    }

    private void parseNodes(Map<String, Object> args) {
        Object v = args.get("nodes");
        if (v != null) {
            addNode(v);
        }
    }

}
