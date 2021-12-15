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
package com.anrisoftware.sscontrol.k8s.base.service;

import static com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.stringListStatement;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.split;
import static org.codehaus.groovy.runtime.InvokerHelper.invokeMethod;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.anrisoftware.sscontrol.debug.external.DebugLoggingService;
import com.anrisoftware.sscontrol.types.cluster.ClusterHost;
import com.anrisoftware.sscontrol.types.host.HostServiceProperties;
import com.anrisoftware.sscontrol.types.host.HostServicePropertiesService;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.types.misc.DebugLogging;
import com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.ListProperty;
import com.google.inject.assistedinject.Assisted;

/**
 * <i>K8s-Master</i> script service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public class K8sImpl implements K8s {

    /**
     *
     *
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface K8sImplFactory extends K8sService {

    }

    private final List<TargetHost> targets;

    private final HostServiceProperties serviceProperties;

    private final K8sImplLogger log;

    private DebugLogging debug;

    @Inject
    private transient LabelFactory labelFactory;

    private final Map<String, Label> labels;

    @Inject
    private transient TaintFactory taintFactory;

    private final Map<String, Taint> taints;

    private final List<ClusterHost> clusterHosts;

    private Map<String, Object> cluster;

    private Map<String, Object> init;

    private Map<String, Object> join;

    private Map<String, Object> kubelet;

    @Inject
    K8sImpl(K8sImplLogger log, HostServicePropertiesService propertiesService, @Assisted Map<String, Object> args) {
        this.log = log;
        this.targets = new ArrayList<>();
        this.serviceProperties = propertiesService.create();
        this.labels = new HashMap<>();
        this.taints = new HashMap<>();
        this.clusterHosts = new ArrayList<>();
        this.cluster = new HashMap<>();
        this.init = new HashMap<>();
        this.join = new HashMap<>();
        this.kubelet = new HashMap<>();
        parseArgs(args);
    }

    @Inject
    public void setDebugService(DebugLoggingService debugService) {
        this.debug = debugService.create();
    }

    @Override
    public void target(Map<String, Object> args) {
        Object v = args.get("target");
        @SuppressWarnings("unchecked")
        List<TargetHost> l = InvokerHelper.asList(v);
        targets.addAll(l);
    }

    @Override
    public void debug(Map<String, Object> args, String name) {
        Map<String, Object> arguments = new HashMap<>(args);
        arguments.put("name", name);
        invokeMethod(debug, "debug", arguments);
    }

    /**
     * <pre>
     * debug name: "error", level: 1
     * </pre>
     */
    @Override
    public void debug(Map<String, Object> args) {
        Map<String, Object> arguments = new HashMap<>(args);
        invokeMethod(debug, "debug", arguments);
    }

    /**
     * <pre>
     * debug << [name: "error", level: 1]
     * </pre>
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getDebug() {
        return (List<Object>) invokeMethod(debug, "getDebug", null);
    }

    /**
     * <pre>
     * property << 'name=value'
     * </pre>
     */
    @Override
    public List<String> getProperty() {
        return stringListStatement(new ListProperty() {

            @Override
            public void add(String property) {
                serviceProperties.addProperty(property);
            }
        });
    }

    @Override
    public void setCluster(Map<String, Object> args) {
        this.cluster = args;
    }

    @Override
    public Map<String, Object> getCluster() {
        return cluster;
    }

    @Override
    public void cluster(Map<String, Object> args) {
        cluster.putAll(args);
    }

    @Override
    public void setInit(Map<String, Object> args) {
        this.init = args;
    }

    @Override
    public Map<String, Object> getInit() {
        return init;
    }

    @Override
    public void init(Map<String, Object> args) {
        init.putAll(args);
    }

    @Override
    public void setJoin(Map<String, Object> args) {
        this.join = args;
    }

    @Override
    public Map<String, Object> getJoin() {
        return join;
    }

    @Override
    public void join(Map<String, Object> args) {
        join.putAll(args);
    }

    @Override
    public void setKubelet(Map<String, Object> args) {
        this.kubelet = args;
    }

    @Override
    public Map<String, Object> getKubelet() {
        return kubelet;
    }

    @Override
    public void kubelet(Map<String, Object> args) {
        kubelet.putAll(args);
    }

    @Override
    public void label(Map<String, Object> args) {
        Label label = labelFactory.create(args);
        log.labelAdded(this, label);
        labels.put(label.getKey(), label);
    }

    @Override
    public List<String> getLabel() {
        return stringListStatement(new ListProperty() {

            @Override
            public void add(String property) {
                String[] s = split(property, "=");
                Map<String, Object> args = new HashMap<>();
                args.put("key", s[0]);
                if (s.length > 1) {
                    args.put("value", s[1]);
                }
                label(args);
            }
        });
    }

    @Override
    public void taint(Map<String, Object> args) {
        Taint taint = taintFactory.create(args);
        log.taintAdded(this, taint);
        taints.put(taint.getKey(), taint);
    }

    private static final Pattern TAINT_PATTERN = Pattern.compile("^(?<key>.*)(:?=(?<value>.*)(:?:(?<effect>.*)))");

    @Override
    public List<String> getTaint() {
        return stringListStatement(new ListProperty() {

            @Override
            public void add(String property) {
                Matcher m = TAINT_PATTERN.matcher(property);
                assertThat(format("taint matches %s but was %s", TAINT_PATTERN, property), m.matches(), equalTo(true));
                String key = m.group("key");
                String value = m.group("value");
                String effect = m.group("effect");
                Map<String, Object> args = new HashMap<>();
                args.put("key", key);
                args.put("value", value);
                args.put("effect", effect);
                taint(args);
            }
        });
    }

    @Override
    public DebugLogging getDebugLogging() {
        return debug;
    }

    @Override
    public TargetHost getTarget() {
        return getTargets().get(0);
    }

    @Override
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
        return "k8s-master";
    }

    @Override
    public Map<String, Label> getLabels() {
        return labels;
    }

    @Override
    public Map<String, Taint> getTaints() {
        return taints;
    }

    @Override
    public ClusterHost getClusterHost() {
        if (getClusterHosts().size() > 0) {
            return getClusterHosts().get(0);
        } else {
            return null;
        }
    }

    public void addClusterHosts(List<ClusterHost> list) {
        this.clusterHosts.addAll(list);
        log.clusterHostsAdded(this, list);
    }

    @Override
    public List<ClusterHost> getClusterHosts() {
        return Collections.unmodifiableList(clusterHosts);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("targets", targets).toString();
    }

    private void parseArgs(Map<String, Object> args) {
        parseTargets(args);
        parseClusters(args);
    }

    @SuppressWarnings("unchecked")
    private void parseTargets(Map<String, Object> args) {
        Object v = args.get("targets");
        if (v != null) {
            addTargets((List<TargetHost>) v);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseClusters(Map<String, Object> args) {
        Object v = args.get("clusters");
        if (v != null) {
            addClusterHosts((List<ClusterHost>) v);
        }
    }

}
