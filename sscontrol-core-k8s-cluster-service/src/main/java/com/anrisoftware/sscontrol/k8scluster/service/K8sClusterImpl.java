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
package com.anrisoftware.sscontrol.k8scluster.service;

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

import com.anrisoftware.sscontrol.k8scluster.service.ClusterImpl.ClusterImplFactory;
import com.anrisoftware.sscontrol.types.cluster.ClusterHost;
import com.anrisoftware.sscontrol.types.cluster.Credentials;
import com.anrisoftware.sscontrol.types.host.HostServiceProperties;
import com.anrisoftware.sscontrol.types.host.HostServicePropertiesService;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.ListProperty;
import com.google.inject.assistedinject.Assisted;

import lombok.Data;

/**
 * <i>K8s-Cluster</i> service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Data
public class K8sClusterImpl implements K8sCluster {

    private final K8sClusterImplLogger log;

    @Inject
    private transient Map<String, CredentialsFactory> credentialsFactories;

    private final List<Credentials> credentials;

    private final HostServiceProperties serviceProperties;

    private final List<TargetHost> targets;

    private final ClusterImplFactory clusterFactory;

    private Cluster cluster;

    private final ContextFactory contextFactory;

    private Context context;

    private final K8sClusterHostFactory clusterHostFactory;

    private String group;

    private List<String> caCertHashes;

    private String token;

    private String tlsBootstrapToken;

    @Inject
    K8sClusterImpl(K8sClusterImplLogger log, HostServicePropertiesService propertiesService,
            ClusterImplFactory clusterFactory, ContextFactory contextFactory, K8sClusterHostFactory clusterHostFactory,
            @Assisted Map<String, Object> args) {
        this.log = log;
        this.serviceProperties = propertiesService.create();
        this.targets = new ArrayList<>();
        this.credentials = new ArrayList<>();
        this.clusterFactory = clusterFactory;
        this.contextFactory = contextFactory;
        this.clusterHostFactory = clusterHostFactory;
        this.group = "default";
        this.caCertHashes = new ArrayList<>();
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
     * target name: 'master'
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
     * cluster name: "default-cluster"
     * </pre>
     */
    public void cluster(Map<String, Object> args) {
        if (cluster != null) {
            this.cluster = clusterFactory.create(cluster, args);
        } else {
            this.cluster = clusterFactory.create(args);
        }
        log.clusterSet(this, cluster);
    }

    /**
     * <pre>
     * context name: "default-context"
     * </pre>
     */
    public void context(Map<String, Object> args) {
        this.context = contextFactory.create(args);
        log.contextSet(this, context);
    }

    /**
     * <pre>
     * credentials 'cert', name: 'default-admin', ca: 'ca.pem', cert: 'cert.pem', key: 'key.pem'
     * </pre>
     */
    public void credentials(Map<String, Object> args, String type) {
        Map<String, Object> a = new HashMap<>(args);
        a.put("type", type);
        credentials(a);
    }

    /**
     * <pre>
     * credentials type: 'cert', name: 'default-admin', ca: 'ca.pem', cert: 'cert.pem', key: 'key.pem'
     * </pre>
     */
    public void credentials(Map<String, Object> args) {
        Object type = args.get("type");
        assertThat("type=null", type, notNullValue());
        CredentialsFactory factory = credentialsFactories.get(type.toString());
        Credentials c = factory.create(args);
        credentials.add(c);
        log.credentialsAdded(this, c);
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

    /**
     * Returns the hosts from where kubectl can be called, optionally with the
     * credentials for authentication.
     */
    @Override
    public List<ClusterHost> getHosts() {
        List<ClusterHost> list = new ArrayList<>();
        List<Credentials> creds = new ArrayList<>(credentials);
        for (TargetHost ssh : targets) {
            K8sClusterHost host;
            host = clusterHostFactory.create(this, ssh, creds);
            list.add(host);
        }
        return Collections.unmodifiableList(list);
    }

    public void caCertHash(String hash) {
        caCertHashes.add(hash);
    }

    public void caCertHashes(List<String> hashes) {
        setCaCertHashes(hashes);
    }

    public void token(String token) {
        setToken(token);
    }

    public void bootstrap(String bootstrap) {
        setTlsBootstrapToken(bootstrap);
    }


    @Override
    public String getName() {
        return "k8s-cluster";
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("targets", getTargets()).toString();
    }

    @SuppressWarnings("unchecked")
    private void parseArgs(Map<String, Object> args) {
        parseContext(args);
        parseCluster(args);
        var v = args.get("group");
        if (v != null) {
            this.group = v.toString();
        }
        v = args.get("targets");
        if (v != null) {
            targets.addAll((List<TargetHost>) v);
        }
        v = args.get("hashes");
        if (v != null) {
            setCaCertHashes((List<String>) v);
        }
        v = args.get("hash");
        if (v != null) {
            caCertHash(v.toString());
        }
        v = args.get("token");
        if (v != null) {
            setToken(v.toString());
        }
        v = args.get("bootstrap");
        if (v != null) {
            setTlsBootstrapToken(v.toString());
        }
    }

    private void parseContext(Map<String, Object> args) {
        Object v = args.get("context");
        Map<String, Object> a = new HashMap<>(args);
        a.put("name", v);
        context(a);
    }

    private void parseCluster(Map<String, Object> args) {
        Object v = args.get("cluster");
        Map<String, Object> a = new HashMap<>(args);
        a.put("name", v);
        cluster(a);
    }

}
