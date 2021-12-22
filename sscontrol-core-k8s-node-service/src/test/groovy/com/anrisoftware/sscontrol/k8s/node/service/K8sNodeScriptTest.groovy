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
package com.anrisoftware.sscontrol.k8s.node.service

import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import org.junit.rules.TemporaryFolder

import com.anrisoftware.globalpom.core.resources.ResourcesModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.k8s.base.service.K8sModule
import com.anrisoftware.sscontrol.k8s.node.service.K8sNodeImpl.K8sNodeImplFactory
import com.anrisoftware.sscontrol.k8scluster.service.K8sCluster
import com.anrisoftware.sscontrol.k8scluster.service.K8sClusterFactory
import com.anrisoftware.sscontrol.k8scluster.service.K8sClusterModule
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesServiceModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.services.host.HostServicesModule
import com.anrisoftware.sscontrol.services.host.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.targets.TargetsModule
import com.anrisoftware.sscontrol.services.targets.TargetsServiceModule
import com.anrisoftware.sscontrol.shell.utils.RobobeeScriptModule
import com.anrisoftware.sscontrol.shell.utils.RobobeeScript.RobobeeScriptFactory
import com.anrisoftware.sscontrol.ssh.service.SshModule
import com.anrisoftware.sscontrol.ssh.service.SshImpl.SshImplFactory
import com.anrisoftware.sscontrol.tls.TlsModule
import com.anrisoftware.sscontrol.types.host.HostServices
import com.anrisoftware.sscontrol.types.misc.TypesModule
import com.anrisoftware.sscontrol.types.ssh.external.Ssh
import com.anrisoftware.sscontrol.utils.systemmappings.internal.SystemNameMappingsModule
import com.google.inject.Guice

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@EnableRuleMigrationSupport
class K8sNodeScriptTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Inject
    RobobeeScriptFactory robobeeScriptFactory

    @Inject
    SshImplFactory sshFactory

    @Inject
    K8sNodeImplFactory serviceFactory

    @Inject
    HostServicesImplFactory servicesFactory

    @Inject
    K8sClusterFactory clusterFactory

    @Test
    void "script_basic"() {
        def test = [
            name: 'script_basic',
            script: """
service "ssh", host: "robobee@node-3.robobee-test.test", socket: robobeeSocket
service "ssh", group: "control-nodes" with {
    host "robobee@node-3.robobee-test.test", socket: robobeeSocket
}
service "k8s-cluster", target: "control-nodes" with {
    caCertHash "sha256:7501bc596d3dce2f88ece232d3454876293bea94884bb19f90f2ebc6824e845f"
    token "34f578.e9676c9fc49544bb"
}
service "k8s-node", clusters: "k8s-cluster"
""",
            scriptVars: [ robobeeSocket: '/tmp/robobee@robobee-3-test:22'],
            expected: { HostServices services ->
                assert services.getServices('k8s-cluster').size() == 1
                K8sCluster c = services.getServices('k8s-cluster')[0]
                assert c.caCertHashes.size() == 1
                assert c.caCertHashes.contains("sha256:7501bc596d3dce2f88ece232d3454876293bea94884bb19f90f2ebc6824e845f")
                assert c.token == "34f578.e9676c9fc49544bb"
                assert services.getServices('k8s-node').size() == 1
                K8sNode s = services.getServices('k8s-node')[0]
                assert s.clusterHost.hostAddress == "192.168.56.203"
            },
        ]
        doTest test
    }

    void doTest(Map test) {
        log.info '\n######### {} #########\ncase: {}', test.name, test
        def services = servicesFactory.create()
        services.targets.addTarget([getGroup: {'default'}, getHosts: { []}] as Ssh)
        services.putAvailableService 'ssh', sshFactory
        services.putAvailableService 'k8s-cluster', clusterFactory
        services.putAvailableService 'k8s-node', serviceFactory
        robobeeScriptFactory.create folder.newFile(), test.script, test.scriptVars, services call()
        Closure expected = test.expected
        expected services
    }

    @BeforeEach
    void setupTest() {
        toStringStyle
        Guice.createInjector(
                new SshModule(),
                new K8sModule(),
                new K8sNodeModule(),
                new K8sClusterModule(),
                new RobobeeScriptModule(),
                new PropertiesModule(),
                new DebugLoggingModule(),
                new TypesModule(),
                new StringsModule(),
                new HostServicesModule(),
                new TargetsModule(),
                new TargetsServiceModule(),
                new PropertiesUtilsModule(),
                new ResourcesModule(),
                new TlsModule(),
                new SystemNameMappingsModule(),
                new HostServicePropertiesServiceModule(),
                ).injectMembers(this)
    }
}
