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
package com.anrisoftware.sscontrol.repo.helm.service

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import com.anrisoftware.globalpom.core.resources.ResourcesModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesServiceModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.repo.helm.service.HelmRepoImpl.HelmRepoImplFactory
import com.anrisoftware.sscontrol.services.host.HostServicesModule
import com.anrisoftware.sscontrol.services.host.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.targets.TargetsModule
import com.anrisoftware.sscontrol.services.targets.TargetsServiceModule
import com.anrisoftware.sscontrol.types.host.HostServices
import com.anrisoftware.sscontrol.types.misc.TypesModule
import com.anrisoftware.sscontrol.types.ssh.Ssh
import com.anrisoftware.sscontrol.utils.repo.UtilsRepoModule
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
class HelmRepoServiceTest {

    @Inject
    HelmRepoImplFactory serviceFactory

    @Inject
    HostServicesImplFactory servicesFactory

    static Stream<Arguments> helm_service() {
        def list = []
        list << of(
                "with repo",
                """\
service "repo-helm", group: 'wordpress-app' with {
    remote url: "https://fantastic-charts.storage.googleapis.com"
}
""", { HostServices services ->
                    assert services.getServices('repo-helm').size() == 1
                    HelmRepo s = services.getServices('repo-helm')[0]
                    assert s.name == 'repo-helm'
                    assert s.group == 'wordpress-app'
                    assert s.remote.uri.toString() == 'https://fantastic-charts.storage.googleapis.com'
                })
        list << of(
                "with basic auth",
                """\
service "repo-helm", group: 'wordpress-app' with {
    remote url: "https://fantastic-charts.storage.googleapis.com"
    credentials "basic", user: "foo", password: "bar"
}
""", { HostServices services ->
                    assert services.getServices('repo-helm').size() == 1
                    HelmRepo s = services.getServices('repo-helm')[0]
                    assert s.name == 'repo-helm'
                    assert s.group == 'wordpress-app'
                    assert s.remote.uri.toString() == 'https://fantastic-charts.storage.googleapis.com'
                    assert s.credentials.type == 'basic'
                    assert s.credentials.user == 'foo'
                    assert s.credentials.password == 'bar'
                })
        list.stream()
    }

    @ParameterizedTest
    @MethodSource
    void helm_service(def name, def input, def expected) {
        log.info '\n######### {} #########\n{}', name, input
        def services = servicesFactory.create()
        services.targets.addTarget([getGroup: {'default'}, getHosts: { []}] as Ssh)
        services.putAvailableService 'repo-helm', serviceFactory
        Eval.me 'service', services, input as String
        expected services
    }

    @BeforeEach
    void setupTest() {
        toStringStyle
        Guice.createInjector(
                new HelmRepoModule(),
                new UtilsRepoModule(),
                new PropertiesModule(),
                new DebugLoggingModule(),
                new TypesModule(),
                new TargetsServiceModule(),
                new StringsModule(),
                new HostServicesModule(),
                new TargetsModule(),
                new PropertiesUtilsModule(),
                new ResourcesModule(),
                new SystemNameMappingsModule(),
                new HostServicePropertiesServiceModule(),
                ).injectMembers(this)
    }
}
