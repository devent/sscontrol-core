/*-
 * #%L
 * sscontrol-osgi - k8s-from-helm-script-linux
 * %%
 * Copyright (C) 2016 - 2018 Advanced Natural Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.anrisoftware.sscontrol.k8s.fromhelm.script.linux.internal.script_1_9

import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach

import com.anrisoftware.sscontrol.k8s.fromhelm.service.internal.FromHelmImpl.FromHelmImplFactory
import com.anrisoftware.sscontrol.repo.git.script.debian.internal.debian_9.GitRepoDebianFactory
import com.anrisoftware.sscontrol.repo.git.service.internal.GitRepoImpl.GitRepoImplFactory
import com.anrisoftware.sscontrol.runner.groovy.internal.RunnerModule
import com.anrisoftware.sscontrol.runner.groovy.internal.RunScriptImpl.RunScriptImplFactory
import com.anrisoftware.sscontrol.runner.test.external.AbstractRunnerTestBase
import com.anrisoftware.sscontrol.ssh.script.linux.external.Ssh_Linux_Factory
import com.anrisoftware.sscontrol.ssh.script.linux.internal.Ssh_Linux_Module
import com.anrisoftware.sscontrol.ssh.service.internal.SshImpl.SshImplFactory
import com.anrisoftware.sscontrol.types.host.external.HostServices

/**
 *
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
abstract class AbstractFromHelmRunnerTest extends AbstractRunnerTestBase {

    static final URL helmCommand = AbstractFromHelmRunnerTest.class.getResource('helm_command.txt')

    @Inject
    RunScriptImplFactory runnerFactory

    @Inject
    SshImplFactory sshFactory

    @Inject
    Ssh_Linux_Factory sshLinuxFactory

    @Inject
    GitRepoImplFactory gitFactory

    @Inject
    GitRepoDebianFactory gitDebianFactory

    @Inject
    FromHelmImplFactory fromHelmFactory

    @Inject
    FromHelmLinuxFactory fromHelmLinuxFactory

    def getRunScriptFactory() {
	runnerFactory
    }

    HostServices putServices(HostServices services) {
	services.putAvailableService 'ssh', sshFactory
	services.putAvailableScriptService 'ssh/linux/0', sshLinuxFactory
	services.putAvailableService 'repo-git', gitFactory
	services.putAvailableScriptService 'repo-git/debian/9', gitDebianFactory
	services.putAvailableService 'from-helm', fromHelmFactory
	services.putAvailableScriptService 'from-helm/linux/0', fromHelmLinuxFactory
	return services
    }

    List getAdditionalModules() {
	def modules = super.additionalModules
	modules << new RunnerModule()
	modules << new Ssh_Linux_Module()
	modules.addAll FromRepositoryTestModules.getAdditionalModules()
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
