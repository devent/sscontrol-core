/*
 * Copyright 2016 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.k8smaster.internal;

import static com.anrisoftware.sscontrol.k8smaster.internal.K8sMasterImplLogger._.clusterSet;
import static com.anrisoftware.sscontrol.k8smaster.internal.K8sMasterImplLogger._.pluginAdded;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;
import com.anrisoftware.sscontrol.k8smaster.external.Cluster;
import com.anrisoftware.sscontrol.k8smaster.external.Plugin;

/**
 * Logging for {@link K8sMasterImpl}.
 * 
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class K8sMasterImplLogger extends AbstractLogger {

    enum _ {

        pluginAdded("Plugin {} added to {}"),

        clusterSet("Cluster {} set for {}");

        private String name;

        private _(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Sets the context of the logger to {@link K8sMasterImpl}.
     */
    public K8sMasterImplLogger() {
        super(K8sMasterImpl.class);
    }

    void pluginAdded(K8sMasterImpl k8s, Plugin plugin) {
        debug(pluginAdded, plugin, k8s);
    }

    void clusterSet(K8sMasterImpl k8s, Cluster cluster) {
        debug(clusterSet, cluster, k8s);
    }
}
