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
package com.anrisoftware.sscontrol.k8s.base.service

import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.anrisoftware.globalpom.core.resources.ResourcesModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.k8s.base.service.K8sImpl.K8sImplFactory
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesServiceModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.services.host.HostServicesModule
import com.anrisoftware.sscontrol.services.host.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.targets.TargetsModule
import com.anrisoftware.sscontrol.services.targets.TargetsServiceModule
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
class K8sScriptTest {

    @Inject
    K8sImplFactory serviceFactory

    @Inject
    HostServicesImplFactory servicesFactory

    @Test
    void "basic"() {
        def test = [
            name: 'cluster args',
            input: """
service "k8s-control"
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8s s = services.getServices('k8s-control')[0] as K8s
                assert s.targets.size() == 0
            },
        ]
        doTest test
    }

    @Test
    void "cluster config"() {
        def test = [
            name: 'cluster config',
            input: """
service "k8s-control" with {
    cluster apiServer: [extraArgs: ['allow-privileged': true]]
}
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8s s = services.getServices('k8s-control')[0] as K8s
                assert s.cluster.apiServer.extraArgs["allow-privileged"] == true
            },
        ]
        doTest test
    }

    @Test
    void "init config"() {
        def test = [
            name: 'init config',
            input: """
service "k8s-control" with {
    init localAPIEndpoint: [advertiseAddress: "192.168.0.1", bindPort: 8888]
}
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8s s = services.getServices('k8s-control')[0] as K8s
                assert s.init.localAPIEndpoint.advertiseAddress == "192.168.0.1"
                assert s.init.localAPIEndpoint.bindPort == 8888
            },
        ]
        doTest test
    }

    @Test
    void "join config"() {
        def test = [
            name: 'join config',
            input: """
service "k8s-control" with {
    join discovery: [bootstrapToken: [apiServerEndpoint: "kube-apiserver:6443", token: "abcdef.0123456789abcdef", unsafeSkipCAVerification: true], timeout: "5m0s", tlsBootstrapToken: "abcdef.0123456789abcdef"]
}
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8s s = services.getServices('k8s-control')[0] as K8s
                assert s.join.discovery.bootstrapToken.apiServerEndpoint == "kube-apiserver:6443"
                assert s.join.discovery.bootstrapToken.token == "abcdef.0123456789abcdef"
                assert s.join.discovery.bootstrapToken.unsafeSkipCAVerification == true
                assert s.join.discovery.timeout == "5m0s"
                assert s.join.discovery.tlsBootstrapToken == "abcdef.0123456789abcdef"
            },
        ]
        doTest test
    }

    @Test
    void "kubelet config"() {
        def test = [
            name: 'kubelet config',
            input: """
service "k8s-control" with {
    kubelet clusterDNS: "10.96.0.10"
}
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8s s = services.getServices('k8s-control')[0] as K8s
                assert s.kubelet.clusterDNS == "10.96.0.10"
            },
        ]
        doTest test
    }

    void doTest(Map test) {
        log.info '\n######### {} #########\ncase: {}', test.name, test
        def services = servicesFactory.create()
        services.targets.addTarget([getGroup: {'default'}, getHosts: { []}] as Ssh)
        services.putAvailableService 'k8s-control', serviceFactory
        Eval.me 'service', services, test.input as String
        Closure expected = test.expected
        expected services
    }

    @BeforeEach
    void setupTest() {
        toStringStyle
        Guice.createInjector(
                new K8sModule(),
                new PropertiesModule(),
                new DebugLoggingModule(),
                new TypesModule(),
                new TargetsServiceModule(),
                new StringsModule(),
                new HostServicesModule(),
                new TargetsModule(),
                new PropertiesUtilsModule(),
                new ResourcesModule(),
                new TlsModule(),
                new SystemNameMappingsModule(),
                new HostServicePropertiesServiceModule(),
                ).injectMembers(this)
    }
}
