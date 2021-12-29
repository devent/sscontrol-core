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
package com.anrisoftware.sscontrol.k8s.fromhelm.service

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import javax.inject.Inject

import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.rules.TemporaryFolder
import org.yaml.snakeyaml.composer.ComposerException

import com.anrisoftware.globalpom.core.resources.ResourcesModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.k8s.base.service.K8sModule
import com.anrisoftware.sscontrol.k8s.fromhelm.service.FromHelmImpl.FromHelmImplFactory
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesServiceModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.repo.git.service.GitRepoModule
import com.anrisoftware.sscontrol.repo.git.service.GitRepoImpl.GitRepoImplFactory
import com.anrisoftware.sscontrol.services.host.HostServicesModule
import com.anrisoftware.sscontrol.services.host.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.targets.TargetsModule
import com.anrisoftware.sscontrol.services.targets.TargetsServiceModule
import com.anrisoftware.sscontrol.shell.utils.RobobeeScriptModule
import com.anrisoftware.sscontrol.shell.utils.SshFactory
import com.anrisoftware.sscontrol.shell.utils.RobobeeScript.RobobeeScriptFactory
import com.anrisoftware.sscontrol.tls.TlsModule
import com.anrisoftware.sscontrol.types.host.HostServices
import com.anrisoftware.sscontrol.types.misc.TypesModule
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
class FromHelmServiceTest {

    @Inject
    RobobeeScriptFactory robobeeScriptFactory

    @Inject
    FromHelmImplFactory fromHelmFactory

    @Inject
    HostServicesImplFactory servicesFactory

    @Inject
    GitRepoImplFactory gitFactory

    static Stream<Arguments> service_script() {
        def list = []
        list << of(
                "use external cluster in the group default implicit with repository",
                """\
service "repo-git", group: "wordpress-app" with {
    remote url: "git@github.com:devent/wordpress-app.git"
    credentials "ssh", key: "id_rsa"
}
service "from-helm", repo: "wordpress-app" with {
}
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.repo.repo.remote.uri.toString() == 'ssh://git@github.com/devent/wordpress-app.git'
                    assert s.repo.repo.credentials.type == 'ssh'
                })
        list << of(
                "use custom config",
                """\
service "repo-git", group: "wordpress-app" with {
    remote url: "git@github.com:devent/wordpress-app.git"
    credentials "ssh", key: "id_rsa"
}
service "from-helm", repo: "wordpress-app" with {
    config << "{mariadbUser: user0, mariadbDatabase: user0db}"
}
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.configYaml.toString() == "[mariadbUser:user0, mariadbDatabase:user0db]"
                })
        list << of(
                "multiple custom config",
                """\
service "repo-git", group: "wordpress-app" with {
    remote url: "git@github.com:devent/wordpress-app.git"
    credentials "ssh", key: "id_rsa"
}
service "from-helm", repo: "wordpress-app" with {
    config << '''
mariadbUser: user0
mariadbDatabase: user0db
'''
    config << '''
wordpressUser: user0
wordpressDatabase: user0db
'''
}
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.configYaml.toString() == "[mariadbUser:user0, mariadbDatabase:user0db, wordpressUser:user0, wordpressDatabase:user0db]"
                })
        list << of(
                "helm chart",
                """\
service "from-helm", chart: "stable/mariadb", version: "1.0", ns: "helm-test", name: "wordpress" with {
}
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    assert s.version == "1.0"
                    assert s.release.namespace == "helm-test"
                    assert s.release.name == "wordpress"
                })
        list << of(
                "helm chart with helm repo",
                """\
service "repo-helm", group: "nfs-subdir-external-provisioner" with {
    remote url: "https://kubernetes-sigs.github.io/nfs-subdir-external-provisioner/"
}
service "from-helm", chart: "stable/mariadb", repo: "nfs-subdir-external-provisioner", version: "1.0" with {
    release ns: "helm-test", name: "wordpress"
}
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    assert s.version == "1.0"
                    assert s.release.namespace == "helm-test"
                    assert s.release.name == "wordpress"
                })
        list.stream()
    }

    @ParameterizedTest
    @MethodSource
    void service_script(def name, def script, def expected) {
        log.info "########### {}: {}\n###########", name, script
        doTest([name: name, script: script, scriptVars: [:], expected: expected])
    }

    static Stream<Arguments> service_script_throws_ComposerException() {
        def list = []
        list << of(
                "invalid custom config",
                """\
service "repo-git", group: "wordpress-app" with {
    remote url: "git@github.com:devent/wordpress-app.git"
    credentials "ssh", key: "id_rsa"
}
service "from-helm", repo: "wordpress-app" with {
    config << '''
*The first line.
The last line.
'''
}
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.configYaml.toString() == "[mariadbUser:user0, mariadbDatabase:user0db, wordpressUser:user0, wordpressDatabase:user0db]"
                })
        list.stream()
    }

    @ParameterizedTest
    @MethodSource
    void service_script_throws_ComposerException(def name, def script, def expected) {
        log.info "########### {}: {}\n###########", name, script
        assertThrows ComposerException.class, { doTest([name: name, script: script, scriptVars: [:], expected: expected]) }
    }

    void doTest(Map test) {
        log.info '\n######### {} #########\ncase: {}', test.name, test
        def services = servicesFactory.create()
        services.targets.addTarget SshFactory.localhost(injector)
        services.putAvailableService 'repo-git', gitFactory
        services.putAvailableService 'from-helm', fromHelmFactory
        robobeeScriptFactory.create folder.newFile(), test.script, test.scriptVars, services call()
        Closure expected = test.expected
        expected services
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    def injector

    @BeforeEach
    void setupTest() {
        toStringStyle
        injector = Guice.createInjector(
                new K8sModule(),
                new FromHelmModule(),
                new GitRepoModule(),
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
                new RobobeeScriptModule(),
                new SystemNameMappingsModule(),
                new HostServicePropertiesServiceModule(),
                )
        injector.injectMembers(this)
    }
}
