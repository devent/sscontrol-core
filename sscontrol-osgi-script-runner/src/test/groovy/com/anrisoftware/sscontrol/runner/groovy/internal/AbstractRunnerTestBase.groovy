package com.anrisoftware.sscontrol.runner.groovy.internal

import javax.inject.Inject

import org.apache.commons.io.IOUtils

import com.anrisoftware.globalpom.strings.StringsModule
import com.anrisoftware.globalpom.textmatch.tokentemplate.TokensTemplateModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.parser.groovy.internal.ParserModule
import com.anrisoftware.sscontrol.parser.groovy.internal.ParserImpl.ParserImplFactory
import com.anrisoftware.sscontrol.replace.internal.ReplaceModule
import com.anrisoftware.sscontrol.runner.groovy.internal.RunScriptImpl.RunScriptImplFactory
import com.anrisoftware.sscontrol.services.internal.HostServicesModule
import com.anrisoftware.sscontrol.services.internal.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.shell.external.utils.AbstractScriptTestBase
import com.anrisoftware.sscontrol.shell.internal.cmd.CmdModule
import com.anrisoftware.sscontrol.shell.internal.copy.CopyModule
import com.anrisoftware.sscontrol.shell.internal.facts.FactsModule
import com.anrisoftware.sscontrol.shell.internal.fetch.FetchModule
import com.anrisoftware.sscontrol.shell.internal.scp.ScpModule
import com.anrisoftware.sscontrol.shell.internal.ssh.CmdImplModule
import com.anrisoftware.sscontrol.shell.internal.ssh.ShellCmdModule
import com.anrisoftware.sscontrol.shell.internal.ssh.SshShellModule
import com.anrisoftware.sscontrol.shell.internal.template.TemplateModule
import com.anrisoftware.sscontrol.types.external.HostServices
import com.anrisoftware.sscontrol.types.internal.TypesModule

import groovy.util.logging.Slf4j

/**
 * 
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
abstract class AbstractRunnerTestBase extends AbstractScriptTestBase {

    @Inject
    ParserImplFactory parserFactory

    @Inject
    RunScriptImplFactory runnerFactory

    @Inject
    HostServicesImplFactory servicesFactory

    void doTest(Map test, int k) {
        log.info '\n######### {}. case: {}', k, test
        File parent = folder.newFolder()
        File scriptFile = new File(parent, "Script.groovy")
        File dir = folder.newFolder()
        createDummyCommands dir
        IOUtils.copy test.script.openStream(), new FileOutputStream(scriptFile)
        def roots = [parent.toURI()] as URI[]
        def variables = createVariables(dir: dir)
        def services = putServices(servicesFactory.create())
        def parser = parserFactory.create(roots, "Script.groovy", variables, services)
        def parsed = parser.parse()
        assert parsed.services.size() == 2
        runnerFactory.create(threads, parsed).run variables
    }

    Map createVariables(Map args) {
        [:]
    }

    String getServiceName() {
    }

    String getScriptServiceName() {
    }

    HostServices putServices(HostServices services) {
        services
    }

    void createDummyCommands(File dir) {
    }

    List getAdditionalModules() {
        [
            new ParserModule(),
            new RunnerModule(),
            new DebugLoggingModule(),
            new TypesModule(),
            new StringsModule(),
            new HostServicesModule(),
            new SshShellModule(),
            new ShellCmdModule(),
            new CmdImplModule(),
            new CmdModule(),
            new ScpModule(),
            new CopyModule(),
            new FetchModule(),
            new ReplaceModule(),
            new TemplateModule(),
            new FactsModule(),
            new TokensTemplateModule(),
        ]
    }
}