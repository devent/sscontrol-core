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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.rules.TemporaryFolder
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.composer.ComposerException

import com.anrisoftware.globalpom.core.resources.ResourcesModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.resources.st.internal.worker.STDefaultPropertiesModule
import com.anrisoftware.resources.st.internal.worker.STWorkerModule
import com.anrisoftware.resources.templates.internal.maps.TemplatesDefaultMapsModule
import com.anrisoftware.resources.templates.internal.templates.TemplatesResourcesModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.k8s.base.service.K8sModule
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesServiceModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.repo.helm.service.HelmRepoModule
import com.anrisoftware.sscontrol.repo.helm.service.HelmRepoImpl.HelmRepoImplFactory
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
@EnableRuleMigrationSupport
class FromHelmServiceTest {

    @Inject
    RobobeeScriptFactory robobeeScriptFactory

    @Inject
    FromHelmImplFactory fromHelmFactory

    @Inject
    HostServicesImplFactory servicesFactory

    @Inject
    HelmRepoImplFactory helmFactory

    static Stream<Arguments> service_script() {
        def list = []
        list << of(
                "use external cluster in the group default implicit with repository",
                """\
service "repo-helm", group: "wordpress-app" with {
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
service "repo-helm", group: "wordpress-app" with {
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
service "repo-helm", group: "wordpress-app" with {
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
        list << of(
                "helm chart with dry-run and debug output extern",
                """\
service "from-helm", chart: "stable/mariadb" with {
    dryrun true
    debug true
}
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    assert s.dryrun
                    assert s.debug
                })
        list << of(
                "helm chart with dry-run and debug output inline",
                """\
service "from-helm", chart: "stable/mariadb", dryrun: true, debug: true
""", { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    assert s.dryrun
                    assert s.debug
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
service "repo-helm", group: "wordpress-app" with {
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

    static Stream<Arguments> service_with_inline() {
        def list = []
        list << of(
                "helm chart with insertNodeSelector",
                '''\
service "from-helm", chart: "stable/mariadb" with {
    config << """
wordpress:
  affinity:
    nodeAntiAffinity:
${insertNodeSelector(6, "app", "nfs-subdir-external-provisioner")}
${insertPreferredSchedulingTerm(6, "app", "nfs-subdir-external-provisioner", 100)}
    podAntiAffinity:
${insertPodAffinityTerm(6, "app", "nfs-subdir-external-provisioner", "kubernetes.io/hostname")}
${insertWeightedPodAffinityTerm(6, "app", "nfs-subdir-external-provisioner", 100, "kubernetes.io/hostname")}
"""
}
''', { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    println dumpYaml(s.configYaml)
                    assert dumpYaml(s.configYaml) == '''\
wordpress:
  affinity:
    nodeAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: app
            operator: In
            values:
            - nfs-subdir-external-provisioner
      preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        preference:
          matchExpressions:
          - key: app
            operator: In
            values:
            - nfs-subdir-external-provisioner
    podAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
          matchExpressions:
          - key: app
            operator: In
            values:
            - nfs-subdir-external-provisioner
        topologyKey: kubernetes.io/hostname
      preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          labelSelector:
            matchExpressions:
            - key: app
              operator: In
              values:
              - nfs-subdir-external-provisioner
          topologyKey: kubernetes.io/hostname
'''
                })
        list << of(
                "helm chart with insertToleration",
                '''\
service "from-helm", chart: "stable/mariadb" with {
    config << """
wordpress:
  toleration:
${insertTolerationControlPane(4)}
${insertToleration(4, "robobeerun.com/grafana")}
${insertToleration(4, "robobeerun.com/keycloak", "required")}
"""
}
''', { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    println dumpYaml(s.configYaml)
                    assert dumpYaml(s.configYaml) == '''\
wordpress:
  toleration:
  - effect: NoSchedule
    key: node-role.kubernetes.io/master
    operator: Equal
  - effect: NoSchedule
    key: robobeerun.com/grafana
    operator: Equal
  - effect: NoSchedule
    key: robobeerun.com/keycloak
    operator: Equal
    value: required
'''
                })
        list << of(
                "helm chart with insertResources",
                '''\
service "from-helm", chart: "stable/mariadb" with {
    config << """
wordpress:
  resources:
${insertResources(4, [cpu: 0.5, memory: "600Mi"])}
"""
}
''', { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    println dumpYaml(s.configYaml)
                    assert dumpYaml(s.configYaml) == '''\
wordpress:
  resources:
    limits:
      cpu: 0.5
      memory: 600Mi
    requests:
      cpu: 0.5
      memory: 600Mi
'''
                })
        list << of(
                "helm chart with persistentVolumeClaimSpec",
                '''\
service "from-helm", chart: "stable/mariadb" with {
    config << """
wordpress:
  dataVolumeClaimSpec:
${insertPersistentVolumeClaimSpec(4, "1Gi", "nfs")}
"""
}
''', { HostServices services ->
                    assert services.getServices('from-helm').size() == 1
                    FromHelm s = services.getServices('from-helm')[0]
                    assert s.chart == "stable/mariadb"
                    println dumpYaml(s.configYaml)
                    assert dumpYaml(s.configYaml) == '''\
wordpress:
  dataVolumeClaimSpec:
    accessModes:
    - ReadWriteOnce
    storageClassName: nfs
    resources:
      requests:
        storage: 1Gi
'''
                })
        list.stream()
    }

    static Yaml yaml

    @BeforeAll
    static void createYaml() {
        def options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }


    static String dumpYaml(def object) {
        return yaml.dump(object)
    }

    @ParameterizedTest
    @MethodSource
    void service_with_inline(def name, def script, def expected) {
        log.info "########### {}: {}\n###########", name, script
        doTest([name: name, script: script, scriptVars: [:], expected: expected])
    }

    void doTest(Map test) {
        log.info '\n######### {} #########\ncase: {}', test.name, test
        def services = servicesFactory.create()
        services.targets.addTarget SshFactory.localhost(injector)
        services.putAvailableService 'repo-helm', helmFactory
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
                new HelmRepoModule(),
                new UtilsRepoModule(),
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
                new TemplatesDefaultMapsModule(),
                new STWorkerModule(),
                new STDefaultPropertiesModule(),
                new TemplatesResourcesModule(),
                )
        injector.injectMembers(this)
    }
}
