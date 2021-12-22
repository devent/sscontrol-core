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
package com.anrisoftware.sscontrol.k8s.node.service;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.anrisoftware.sscontrol.k8s.base.service.K8s;
import com.anrisoftware.sscontrol.k8s.base.service.K8sImpl.K8sImplFactory;
import com.google.inject.assistedinject.Assisted;

import lombok.Data;
import lombok.experimental.Delegate;

/**
 * <i>K8s-Node</i> script service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Data
public class K8sNodeImpl implements K8sNode {

    /**
     *
     *
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface K8sNodeImplFactory extends K8sNodeService {

    }

    @Delegate
    private final K8s k8s;

    @Inject
    K8sNodeImpl(K8sImplFactory k8sFactory, @Assisted Map<String, Object> args) {
        this.k8s = (K8s) k8sFactory.create("k8s-node", args);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("targets", getTargets()).toString();
    }
}
