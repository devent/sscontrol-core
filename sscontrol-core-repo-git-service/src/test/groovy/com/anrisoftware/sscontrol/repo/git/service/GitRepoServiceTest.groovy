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
package com.anrisoftware.sscontrol.repo.git.service

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
import com.anrisoftware.sscontrol.repo.git.service.GitRepoImpl.GitRepoImplFactory
import com.anrisoftware.sscontrol.services.host.HostServicesModule
import com.anrisoftware.sscontrol.services.host.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.targets.TargetsModule
import com.anrisoftware.sscontrol.services.targets.TargetsServiceModule
import com.anrisoftware.sscontrol.types.host.HostServices
import com.anrisoftware.sscontrol.types.misc.TypesModule
import com.anrisoftware.sscontrol.types.ssh.Ssh
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
class GitRepoServiceTest {

    @Inject
    GitRepoImplFactory serviceFactory

    @Inject
    HostServicesImplFactory servicesFactory

    static Stream<Arguments> git_service() {
        def list = []
        list << of(
                "with file",
                """\
service "repo-git", group: 'wordpress-app' with {
    remote url: "/devent/wordpress-app"
}
""", { HostServices services ->
                    assert services.getServices('repo-git').size() == 1
                    GitRepo s = services.getServices('repo-git')[0]
                    assert s.name == 'repo-git'
                    assert s.group == 'wordpress-app'
                    assert s.remote.uri.toString() == 'file:/devent/wordpress-app'
                })
        list << of(
                "with git credentials",
                """\
service "repo-git", group: 'wordpress-app' with {
    remote url: "git://github.com/devent/wordpress-app.git"
    credentials "ssh", key: "id_rsa.pub"
}
""", { HostServices services ->
                    assert services.getServices('repo-git').size() == 1
                    GitRepo s = services.getServices('repo-git')[0]
                    assert s.name == 'repo-git'
                    assert s.group == 'wordpress-app'
                    assert s.remote.uri.toString() == 'git://github.com/devent/wordpress-app.git'
                    assert s.credentials.type == 'ssh'
                    assert s.credentials.key.toString() == 'file:id_rsa.pub'
                })
        list << of(
                "with checkout",
                """\
service "repo-git", group: 'wordpress-app' with {
    remote url: "git@github.com:devent/wordpress-app-test.git"
    checkout branch: "master", tag: "yaml", commit: "e9edddc2e2a59ecb5526febf5044828e7fedd914"
}
""", { HostServices services ->
                    assert services.getServices('repo-git').size() == 1
                    GitRepo s = services.getServices('repo-git')[0]
                    assert s.name == 'repo-git'
                    assert s.group == 'wordpress-app'
                    assert s.remote.uri.toString() == 'ssh://git@github.com/devent/wordpress-app-test.git'
                    assert s.checkout.branch == 'master'
                    assert s.checkout.tag == 'yaml'
                    assert s.checkout.commit == 'e9edddc2e2a59ecb5526febf5044828e7fedd914'
                })
        list.stream()
    }

    @ParameterizedTest
    @MethodSource
    void git_service(def name, def input, def expected) {
        log.info '\n######### {} #########\n{}', name, input
        def services = servicesFactory.create()
        services.targets.addTarget([getGroup: {'default'}, getHosts: { []}] as Ssh)
        services.putAvailableService 'repo-git', serviceFactory
        Eval.me 'service', services, input as String
        expected services
    }

    @BeforeEach
    void setupTest() {
        toStringStyle
        Guice.createInjector(
                new GitRepoModule(),
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
