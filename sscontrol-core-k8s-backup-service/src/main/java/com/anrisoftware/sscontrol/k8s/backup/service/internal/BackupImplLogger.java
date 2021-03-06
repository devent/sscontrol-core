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
package com.anrisoftware.sscontrol.k8s.backup.service.internal;

import static com.anrisoftware.sscontrol.k8s.backup.service.internal.BackupImplLogger.m.clientSet;
import static com.anrisoftware.sscontrol.k8s.backup.service.internal.BackupImplLogger.m.clustersAdded;
import static com.anrisoftware.sscontrol.k8s.backup.service.internal.BackupImplLogger.m.destinationSet;
import static com.anrisoftware.sscontrol.k8s.backup.service.internal.BackupImplLogger.m.serviceSet;

import java.util.List;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;
import com.anrisoftware.sscontrol.k8s.backup.client.external.Client;
import com.anrisoftware.sscontrol.k8s.backup.client.external.Destination;
import com.anrisoftware.sscontrol.k8s.backup.client.external.Service;
import com.anrisoftware.sscontrol.types.cluster.ClusterHost;

/**
 * Logging for {@link BackupImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class BackupImplLogger extends AbstractLogger {

    enum m {

        clustersAdded("Clusters {} added to {}"),

        serviceSet("Service {} set for {}"),

        destinationSet("Destination {} set for {}"),

        clientSet("Client {} set for {}");

        private String name;

        private m(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Sets the context of the logger to {@link BackupImpl}.
     */
    BackupImplLogger() {
        super(BackupImpl.class);
    }

    void clustersAdded(BackupImpl from, List<ClusterHost> list) {
        debug(clustersAdded, list, from);
    }

    void serviceSet(BackupImpl backup, Service service) {
        debug(serviceSet, service, backup);
    }

    void destinationSet(BackupImpl backup, Destination dest) {
        debug(destinationSet, dest, backup);
    }

    void clientSet(BackupImpl backup, Client client) {
        debug(clientSet, client, backup);
    }
}
