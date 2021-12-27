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
package com.anrisoftware.sscontrol.cilium.service;

import static com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.stringListStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.anrisoftware.sscontrol.types.host.HostServiceProperties;
import com.anrisoftware.sscontrol.types.host.HostServicePropertiesService;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.types.misc.GeneticListPropertyUtil;
import com.anrisoftware.sscontrol.types.misc.GeneticListPropertyUtil.GeneticListProperty;
import com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.ListProperty;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Cilium service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Data
@Slf4j
public class CiliumImpl implements Cilium {

    /**
     *
     *
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface CiliumImplFactory extends CiliumService {

    }

    private static final String SERVICE_NAME = "cilium";

    private final HostServiceProperties serviceProperties;

    private final List<TargetHost> targets;

    private String encryptionInterface;

    private final List<Object> nodes;

    @AssistedInject
    CiliumImpl(HostServicePropertiesService propertiesService, @Assisted Map<String, Object> args) {
        this.targets = new ArrayList<>();
        this.serviceProperties = propertiesService.create();
        this.nodes = new ArrayList<>();
        parseArgs(args);
    }

    @Override
    public String getName() {
        return SERVICE_NAME;
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
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("targets", targets).toString();
    }

    public void encryptionInterface(String encryptionInterface) {
        setEncryptionInterface(encryptionInterface);
    }

    public void setEncryptionInterface(String encryptionInterface) {
        this.encryptionInterface = encryptionInterface;
    }

    @Override
    public String getEncryptionInterface() {
        return encryptionInterface;
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

    public void addNode(Object node) {
        nodes.add(node);
        log.debug("Added node '{}'", node);
    }

    @SuppressWarnings("unchecked")
    private void parseArgs(Map<String, Object> args) {
        Object v = args.get("targets");
        if (v != null) {
            targets.addAll((List<TargetHost>) v);
        }
    }

}
