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
package com.anrisoftware.sscontrol.k8s.restore.script.internal.script_1_7

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.groovy.script.external.ScriptBase
import com.anrisoftware.sscontrol.k8s.backup.client.external.DeploymentFactory
import com.anrisoftware.sscontrol.k8s.backup.client.external.RsyncClient
import com.anrisoftware.sscontrol.k8s.backup.client.external.RsyncClientFactory
import com.anrisoftware.sscontrol.k8s.backup.client.internal.DeploymentImpl
import com.anrisoftware.sscontrol.k8s.restore.service.external.Restore
import com.anrisoftware.sscontrol.k8scluster.external.K8sClusterFactory
import com.anrisoftware.sscontrol.types.cluster.external.ClusterHost
import com.anrisoftware.sscontrol.types.cluster.external.Credentials

import groovy.util.logging.Slf4j

/**
 * Restore service for Kubernetes 1.7.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class Restore_1_7 extends ScriptBase {

    @Inject
    Restore_1_7_Properties propertiesProvider

    @Inject
    K8sClusterFactory clusterFactory

    DeploymentImpl deployment

    RsyncClient rsyncClient

    @Inject
    void setDeploymentFactory(DeploymentFactory factory) {
        Restore service = service
        this.deployment = factory.create(service.cluster)
    }

    @Inject
    void setRsyncClientFactory(RsyncClientFactory factory) {
        Restore service = service
        this.rsyncClient = factory.create(this, service.service, service.cluster, service.client, service.source)
    }

    @Override
    def run() {
        setupDefaults()
        Restore service = service
        assertThat "clusters=0 for $service", service.clusters.size(), greaterThan(0)
        setupHost service.cluster
        deployment = deployment.createClient()
        deployment.with {
            createClient()
            def type = service.source.type
            def rsyncDeploy = getDeployment service.service.namespace, "rsync-${service.service.name}"
            def serviceDeploy = getDeployment service.service.namespace, service.service.name
            def oldScale = serviceDeploy.get().spec.replicas
            scaleRsync rsyncDeploy, 1
            def rsyncService = createPublicService rsyncDeploy
            def rsyncPort = rsyncService.spec.ports[0].nodePort
            try {
                scaleDeployment serviceDeploy, 0
                rsyncClient.start(backup: false, dir: service.source.dir, path: service.service.target, port: rsyncPort)
                if (service.service.chown) {
                    execCommand rsyncDeploy, "sh -c 'chown ${service.service.chown} -R \"${service.service.target}\"'"
                }
            } finally {
                try {
                    scaleDeployment serviceDeploy, oldScale, false
                } finally {
                    deleteService rsyncService
                    scaleDeployment rsyncDeploy, 0
                }
            }
        }
    }

    def setupDefaults() {
        Restore service = service
        if (!service.client.timeout) {
            service.client.timeout = timeoutLong
        }
        if (!service.service.target) {
            service.service.target = defaultServiceTarget
        }
    }

    /**
     * Setups the host.
     */
    def setupHost(ClusterHost host) {
        Credentials c = host.credentials
        if (!host.proto) {
            if (c.hasProperty('tls') && c.tls.ca) {
                host.proto = defaultServerProtoSecured
            } else {
                host.proto = defaultServerProtoUnsecured
            }
        }
        if (!host.port) {
            if (c.hasProperty('tls') && c.tls.ca) {
                host.port = defaultServerPortSecured
            } else {
                host.port = defaultServerPortUnsecured
            }
        }
        return host
    }

    def scaleRsync(def rsyncDeploy, int replicas) {
        try {
            deployment.scaleDeployment rsyncDeploy, replicas
        } catch (e) {
            deployment.scaleDeployment rsyncDeploy, replicas
        }
    }

    @Override
    ContextProperties getDefaultProperties() {
        propertiesProvider.get()
    }

    int getDefaultServerPortUnsecured() {
        properties.getNumberProperty 'default_server_port_unsecured', defaultProperties
    }

    int getDefaultServerPortSecured() {
        properties.getNumberProperty 'default_server_port_secured', defaultProperties
    }

    String getDefaultServerProtoUnsecured() {
        properties.getProperty 'default_server_proto_unsecured', defaultProperties
    }

    String getDefaultServerProtoSecured() {
        properties.getProperty 'default_server_proto_secured', defaultProperties
    }

    String getDefaultServiceTarget() {
        properties.getProperty 'default_service_target', defaultProperties
    }

    @Override
    def getLog() {
        log
    }
}
