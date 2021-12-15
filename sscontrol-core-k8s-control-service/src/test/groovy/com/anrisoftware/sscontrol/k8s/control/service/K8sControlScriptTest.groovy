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
package com.anrisoftware.sscontrol.k8s.control.service

import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.anrisoftware.globalpom.core.resources.ResourcesModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.k8s.base.service.K8sModule
import com.anrisoftware.sscontrol.k8s.control.service.K8sControlImpl.K8sControlImplFactory
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesServiceModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.services.internal.host.HostServicesModule
import com.anrisoftware.sscontrol.services.internal.host.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.internal.targets.TargetsModule
import com.anrisoftware.sscontrol.services.internal.targets.TargetsServiceModule
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
class K8sControlScriptTest {

    @Inject
    K8sControlImplFactory serviceFactory

    @Inject
    HostServicesImplFactory servicesFactory

    @Test
    void "nodes_args"() {
        def test = [
            name: 'nodes_args',
            input: """
service "k8s-control", nodes: "node-0" with {
}
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8sControl s = services.getServices('k8s-control')[0]
                assert s.nodes.size() == 1
                assert s.nodes[0] == 'node-0'
            },
        ]
        doTest test
    }

    @Test
    void "bind"() {
        def test = [
            name: 'bind',
            input: """
service "k8s-control" with {
    bind insecure: "127.0.0.1", secure: "0.0.0.0", insecurePort: 8080, port: 443
}
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8sControl s = services.getServices('k8s-control')[0] as K8sControl
                assert s.binding.insecureAddress == "127.0.0.1"
                assert s.binding.secureAddress == "0.0.0.0"
                assert s.binding.insecurePort == 8080
                assert s.binding.port == 443
            },
        ]
        doTest test
    }

    @Test
    void "account"() {
        def test = [
            name: 'account',
            input: """
service "k8s-control" with {
    account ca: "ca.pem", cert: "cert.pem", key: "key.pem"
}
""",
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8sControl s = services.getServices('k8s-control')[0] as K8sControl
                assert s.account.tls.ca.toString() =~ /.*ca\.pem/
                assert s.account.tls.cert.toString() =~ /.*cert\.pem/
                assert s.account.tls.key.toString() =~ /.*key\.pem/
            },
        ]
        doTest test
    }

    @Test
    void "auth"() {
        def test = [
            name: 'auth',
            input: '''
service "k8s-control" with {
    authentication "cert", ca: "ca.pem", cert: "cert.pem", key: "key.pem"
    authentication "basic", file: "some_file"
    authentication type: "basic", tokens: """\
token,user,uid,"group1,group2,group3"
"""
    authorization "allow"
    authorization "deny"
    authorization "abac", file: "policy_file.json"
    authorization mode: "abac", abac: """\
{"apiVersion": "abac.authorization.kubernetes.io/v1beta1", "kind": "Policy", "spec": {"user": "alice", "namespace": "*", "resource": "*", "apiGroup": "*"}}
"""
    admission << "AlwaysAdmit,ServiceAccount"
}
''',
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8sControl s = services.getServices('k8s-control')[0] as K8sControl
                assert s.targets.size() == 0
                assert s.cluster.serviceRange == null
                assert s.authentications.size() == 3
                int k = -1
                assert s.authentications[++k].type == 'cert'
                assert s.authentications[k].ca.toString() =~ /.*ca\.pem/
                assert s.authentications[k].cert.toString() =~ /.*cert\.pem/
                assert s.authentications[k].key.toString() =~ /.*key\.pem/
                assert s.authentications[++k].type == 'basic'
                assert s.authentications[k].file.toString() =~ /.*some_file/
                assert s.authentications[++k].type == 'basic'
                assert s.authentications[k].file == null
                assert s.authentications[k].tokens =~ /token,user.*/
                assert s.authorizations.size() == 4
                k = -1
                assert s.authorizations[++k].mode == 'allow'
                assert s.authorizations[++k].mode == 'deny'
                assert s.authorizations[++k].mode == 'abac'
                assert s.authorizations[++k].mode == 'abac'
                assert s.admissions.size() == 2
                k = -1
                assert s.admissions[++k] == 'AlwaysAdmit'
                assert s.admissions[++k] == 'ServiceAccount'
            },
        ]
        doTest test
    }

    @Test
    void "labels"() {
        def test = [
            name: 'labels',
            input: '''
service "k8s-control" with {
    label << "muellerpublic.de/usage=apps"
    label key: "muellerpublic.de/some", value: "foo"
}
''',
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8sControl s = services.getServices('k8s-control')[0] as K8sControl
                assert s.targets.size() == 0
                assert s.cluster.serviceRange == null
                assert s.labels.size() == 2
                assert s.labels['muellerpublic.de/usage'].value == 'apps'
                assert s.labels['muellerpublic.de/some'].value == 'foo'
            },
        ]
        doTest test
    }

    @Test
    void "taints"() {
        def test = [
            name: 'taints',
            input: '''
service "k8s-control" with {
    taint << "node.alpha.kubernetes.io/ismaster=:NoSchedule"
    taint << "dedicated=mail:NoSchedule"
    taint key: "extra", value: "foo", effect: "aaa"
}
''',
            expected: { HostServices services ->
                assert services.getServices('k8s-control').size() == 1
                K8sControl s = services.getServices('k8s-control')[0] as K8sControl
                assert s.targets.size() == 0
                assert s.cluster.serviceRange == null
                assert s.taints.size() == 3
                assert s.taints['node.alpha.kubernetes.io/ismaster'].value == ''
                assert s.taints['node.alpha.kubernetes.io/ismaster'].effect == 'NoSchedule'
                assert s.taints['extra'].value == 'foo'
                assert s.taints['extra'].effect == 'aaa'
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
                new K8sControlModule(),
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
