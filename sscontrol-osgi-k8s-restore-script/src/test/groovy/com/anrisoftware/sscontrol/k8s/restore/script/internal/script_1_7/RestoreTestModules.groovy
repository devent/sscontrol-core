/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.k8s.restore.script.internal.script_1_7

import com.anrisoftware.globalpom.core.resources.ResourcesModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.globalpom.core.textmatch.tokentemplate.TokensTemplateModule
import com.anrisoftware.sscontrol.command.shell.internal.cmd.CmdModule
import com.anrisoftware.sscontrol.command.shell.internal.copy.CopyModule
import com.anrisoftware.sscontrol.command.shell.internal.facts.FactsModule
import com.anrisoftware.sscontrol.command.shell.internal.fetch.FetchModule
import com.anrisoftware.sscontrol.command.shell.internal.replace.ReplaceModule
import com.anrisoftware.sscontrol.command.shell.internal.scp.ScpModule
import com.anrisoftware.sscontrol.command.shell.internal.ssh.CmdImplModule
import com.anrisoftware.sscontrol.command.shell.internal.ssh.ShellCmdModule
import com.anrisoftware.sscontrol.command.shell.internal.ssh.SshShellModule
import com.anrisoftware.sscontrol.command.shell.internal.st.StModule
import com.anrisoftware.sscontrol.command.shell.internal.template.TemplateModule
import com.anrisoftware.sscontrol.command.shell.internal.templateres.TemplateResModule
import com.anrisoftware.sscontrol.command.shell.linux.openssh.internal.find.FindModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.k8s.restore.script.internal.script_1_7.Restore_1_7_Module
import com.anrisoftware.sscontrol.k8s.restore.service.internal.RestoreModule
import com.anrisoftware.sscontrol.k8sbase.base.internal.K8sModule
import com.anrisoftware.sscontrol.k8scluster.internal.K8sClusterModule
import com.anrisoftware.sscontrol.k8scluster.linux.internal.k8scluster_1_5.K8sCluster_1_5_Linux_Module
import com.anrisoftware.sscontrol.k8scluster.linux.internal.k8scluster_1_5.K8sCluster_1_5_Linux_Service
import com.anrisoftware.sscontrol.k8skubectl.linux.external.Kubectl_1_6_Linux_Module
import com.anrisoftware.sscontrol.services.internal.host.HostServicesModule
import com.anrisoftware.sscontrol.ssh.internal.SshModule
import com.anrisoftware.sscontrol.ssh.internal.SshPreModule
import com.anrisoftware.sscontrol.tls.internal.TlsModule
import com.anrisoftware.sscontrol.types.host.external.HostServiceScriptService
import com.anrisoftware.sscontrol.types.misc.internal.TypesModule
import com.anrisoftware.sscontrol.utils.systemmappings.internal.SystemNameMappingsModule
import com.google.inject.AbstractModule

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
class RestoreTestModules {

    /**
     * Returns the needed modules.
     */
    static List getAdditionalModules() {
        [
            new SshModule(),
            new SshPreModule(),
            new K8sModule(),
            new K8sClusterModule(),
            new K8sCluster_1_5_Linux_Module(),
            new Kubectl_1_6_Linux_Module(),
            new RestoreModule(),
            new Restore_1_7_Module(),
            new DebugLoggingModule(),
            new TypesModule(),
            new StringsModule(),
            new HostServicesModule(),
            new ShellCmdModule(),
            new SshShellModule(),
            new CmdImplModule(),
            new CmdModule(),
            new ScpModule(),
            new CopyModule(),
            new FetchModule(),
            new ReplaceModule(),
            new FactsModule(),
            new TemplateModule(),
            new TemplateResModule(),
            new StModule(),
            new TokensTemplateModule(),
            new ResourcesModule(),
            new TlsModule(),
            new SystemNameMappingsModule(),
            new FindModule(),
            new AbstractModule() {

                @Override
                protected void configure() {
                    bind HostServiceScriptService.class to K8sCluster_1_5_Linux_Service.class
                }
            }
        ]
    }
}