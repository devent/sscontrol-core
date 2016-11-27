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
package com.anrisoftware.sscontrol.shell.external.utils

import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*
import groovy.util.logging.Slf4j

import javax.inject.Inject

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import com.anrisoftware.globalpom.durationformat.DurationFormatModule
import com.anrisoftware.globalpom.durationsimpleformat.DurationSimpleFormatModule
import com.anrisoftware.globalpom.exec.internal.command.DefaultCommandLineModule
import com.anrisoftware.globalpom.exec.internal.core.DefaultProcessModule
import com.anrisoftware.globalpom.exec.internal.logoutputs.LogOutputsModule
import com.anrisoftware.globalpom.exec.internal.pipeoutputs.PipeOutputsModule
import com.anrisoftware.globalpom.exec.internal.runcommands.RunCommandsModule
import com.anrisoftware.globalpom.exec.internal.script.ScriptCommandModule
import com.anrisoftware.globalpom.exec.internal.scriptprocess.ScriptProcessModule
import com.anrisoftware.globalpom.threads.external.core.Threads
import com.anrisoftware.globalpom.threads.properties.external.PropertiesThreads
import com.anrisoftware.globalpom.threads.properties.external.PropertiesThreadsFactory
import com.anrisoftware.globalpom.threads.properties.internal.PropertiesThreadsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.resources.templates.internal.maps.TemplatesDefaultMapsModule
import com.anrisoftware.resources.templates.internal.templates.TemplatesResourcesModule
import com.anrisoftware.resources.templates.internal.worker.STDefaultPropertiesModule
import com.anrisoftware.resources.templates.internal.worker.STWorkerModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesImpl.HostServicePropertiesImplFactory
import com.anrisoftware.sscontrol.services.internal.TargetsModule
import com.anrisoftware.sscontrol.services.internal.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.internal.TargetsImpl.TargetsImplFactory
import com.anrisoftware.sscontrol.types.external.HostPropertiesService
import com.anrisoftware.sscontrol.types.external.HostService
import com.anrisoftware.sscontrol.types.external.HostServiceScript
import com.anrisoftware.sscontrol.types.external.HostServices
import com.anrisoftware.sscontrol.types.external.SshHost
import com.anrisoftware.sscontrol.types.external.TargetsService
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector

/**
 * Extend this class to test service scripts.
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
abstract class ScriptTestBase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    Injector injector

    @Inject
    HostServicesImplFactory servicesFactory

    @Inject
    ThreadsTestPropertiesProvider threadsProperties

    @Inject
    PropertiesThreadsFactory threadsFactory

    Threads threads

    void doTest(Map test, int k) {
        log.info '{}. case: {}', k, test
        File dir = folder.newFolder()
        def services = servicesFactory.create()
        putServices services
        putSshService services
        Eval.me 'service', services, test.input as String
        def all = services.targets.getHosts('default')
        createDummyCommands dir
        services.getServices().each { String name ->
            List<HostService> service = services.getServices(name)
            service.eachWithIndex { HostService s, int i ->
                if (s.name == serviceName) {
                    List<SshHost> targets = s.targets.size() == 0 ? all : s.targets
                    targets.each { SshHost host ->
                        log.info '{}. {} {} {}', i, name, s, host
                        HostServiceScript script = services.getAvailableScriptService('hostname/debian/8').create(services, s, host, threads)
                        script = setupScript script, dir: dir
                        script.run()
                    }
                }
            }
        }
        Closure expected = test.expected
        expected test: test, services: services, dir: dir
    }

    abstract String getServiceName()

    abstract void createDummyCommands(File dir)

    abstract void putServices(HostServices services)

    abstract List getAdditionalModules()

    void putSshService(HostServices services) {
        services.addService 'ssh', SshFactory.localhost(injector)
    }

    HostServiceScript setupScript(Map args, HostServiceScript script) {
        script.chdir = args.dir
        script.sudoEnv.PATH = args.dir
        script.env.PATH = args.dir
        return script
    }

    Injector createInjector() {
        this.injector = Guice.createInjector(
                new TargetsModule(),
                new PropertiesModule(),
                new PropertiesUtilsModule(),
                new RunCommandsModule(),
                new LogOutputsModule(),
                new PipeOutputsModule(),
                new DefaultProcessModule(),
                new DefaultCommandLineModule(),
                new ScriptCommandModule(),
                new ScriptProcessModule(),
                new STDefaultPropertiesModule(),
                new STWorkerModule(),
                new TemplatesDefaultMapsModule(),
                new TemplatesResourcesModule(),
                new PropertiesThreadsModule(),
                new DurationSimpleFormatModule(),
                new DurationFormatModule(),
                new AbstractModule() {

                    @Override
                    protected void configure() {
                        bind TargetsService to TargetsImplFactory
                        bind(HostPropertiesService).to(HostServicePropertiesImplFactory)
                    }
                })
        this.injector = injector.createChildInjector(additionalModules)
        injector
    }

    Threads createThreads() {
        PropertiesThreads threads = threadsFactory.create();
        threads.setProperties threadsProperties.get()
        threads.setName("script");
        threads
    }
}