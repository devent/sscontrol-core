/*-
 * #%L
 * sscontrol-osgi - collectd-script-centos
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
package com.anrisoftware.sscontrol.nfs.script.debian.internal.debian_9.internal

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
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.nfs.script.debian.internal.debian_9.Nfs_Debian_9_Module
import com.anrisoftware.sscontrol.nfs.service.internal.NfsModule
import com.anrisoftware.sscontrol.services.internal.host.HostServicesModule
import com.anrisoftware.sscontrol.ssh.service.internal.SshModule
import com.anrisoftware.sscontrol.types.misc.internal.TypesModule
import com.anrisoftware.sscontrol.utils.debian.external.DebianUtilsModule
import com.anrisoftware.sscontrol.utils.systemd.external.SystemdUtilsModule
import com.anrisoftware.sscontrol.utils.systemmappings.internal.SystemNameMappingsModule
import com.anrisoftware.sscontrol.utils.ufw.linux.external.UfwUtilsModule

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
class NfsModules {
    
    static List getAdditionalModules() {
	[
	    new SshModule(),
	    new NfsModule(),
	    new Nfs_Debian_9_Module(),
	    new DebianUtilsModule(),
        new UfwUtilsModule(),
	    new SystemdUtilsModule(),
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
	    new TokensTemplateModule(),
	    new ResourcesModule(),
	    new SystemNameMappingsModule(),
	    new StModule(),
	]
    }
}
