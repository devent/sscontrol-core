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
package com.anrisoftware.sscontrol.shell.script.linux;


import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach

import com.anrisoftware.sscontrol.runner.groovy.internal.RunnerModule
import com.anrisoftware.sscontrol.runner.groovy.internal.RunScriptImpl.RunScriptImplFactory
import com.anrisoftware.sscontrol.runner.test.external.AbstractRunnerTestBase
import com.anrisoftware.sscontrol.shell.service.ShellImpl.ShellImplFactory
import com.anrisoftware.sscontrol.ssh.script.linux.Ssh_Linux_Factory
import com.anrisoftware.sscontrol.ssh.script.linux.Ssh_Linux_Module
import com.anrisoftware.sscontrol.ssh.service.SshImpl.SshImplFactory
import com.anrisoftware.sscontrol.types.host.HostServices

/**
 *
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
abstract class AbstractShellRunnerTest extends AbstractRunnerTestBase {

    @Inject
    RunScriptImplFactory runnerFactory

    @Inject
    SshImplFactory sshFactory

    @Inject
    Ssh_Linux_Factory sshLinuxFactory

    @Inject
    ShellImplFactory shellFactory

    @Inject
    Shell_Linux_Factory shellLinuxFactory

    def getRunScriptFactory() {
        runnerFactory
    }

    HostServices putServices(HostServices services) {
        services.putAvailableService 'ssh', sshFactory
        services.putAvailableScriptService 'ssh/linux/0', sshLinuxFactory
        services.putAvailableService 'shell', shellFactory
        services.putAvailableScriptService 'shell/linux/0', shellLinuxFactory
        return services
    }

    List getAdditionalModules() {
        def modules = super.additionalModules
        modules << new RunnerModule()
        modules << new Ssh_Linux_Module()
        modules.addAll ShellTestModules.getAdditionalModules()
        modules
    }


    @BeforeEach
    void setupTest() {
        toStringStyle
        injector = createInjector()
        injector.injectMembers(this)
        this.threads = createThreads()
    }
}
