package com.anrisoftware.sscontrol.sshd.script.debian.internal.debian_9

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*
import static org.junit.jupiter.api.Assumptions.*

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.anrisoftware.sscontrol.types.host.external.HostServiceScript

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
class SshdServerTest extends AbstractSshdRunnerTest {

    @Test
    void "server_basic"() {
        def test = [
            name: "server_basic",
            script: '''
service "ssh", host: "robobee@robobee-test", socket: robobeeSocket
service "sshd"
''',
            scriptVars: [robobeeSocket: robobeeSocket],
            expectedServicesSize: 2,
            expected: { Map args ->
                assertStringResource SshdServerTest, readRemoteFile('/etc/ssh/sshd_config'), "${args.test.name}_sshd_config_expected.txt"
            },
        ]
        doTest test
    }

    @Test
    void "server_binding_port"() {
        def test = [
            name: "server_binding_port",
            script: '''
service "ssh", host: "robobee@robobee-test", socket: robobeeSocket
service "sshd" with {
    bind port: 2222
}
''',
            scriptVars: [robobeeSocket: robobeeSocket],
            expectedServicesSize: 2,
            after: {  Map args ->
                remoteCommand """
sudo bash -s << eof1
cat > /etc/ssh/sshd_config << 'EOL'
${sshdConfig()}
EOL
eof1
sudo systemctl restart sshd
""", 'robobee-test', 2222
            },
            expected: { Map args ->
                assertStringResource SshdServerTest, readRemoteFile('/etc/ssh/sshd_config', 'robobee-test', 2222), "${args.test.name}_sshd_config_expected.txt"
            },
        ]
        doTest test
    }

    @Test
    void "server_ufw_binding_port"() {
        def test = [
            name: "server_ufw_binding_port",
            script: '''
service "ssh", host: "robobee@robobee-test", socket: robobeeSocket
service "sshd" with {
    bind port: 2222
}
''',
            scriptVars: [robobeeSocket: robobeeSocket],
            expectedServicesSize: 2,
            after: {  Map args ->
                remoteCommand """
sudo bash -s << eof1
cat > /etc/ssh/sshd_config << 'EOL'
${sshdConfig()}
EOL
eof1
sudo systemctl restart sshd
""", 'robobee-test', 2222
            },
            expected: { Map args ->
                assertStringResource SshdServerTest, readRemoteFile('/etc/ssh/sshd_config', 'robobee-test', 2222), "${args.test.name}_sshd_config_expected.txt"
            },
        ]
        doTest test
    }

    static final sshdConfig = { IOUtils.toString(SshdServerTest.class.getResource("sshd_config.txt").openStream(), StandardCharsets.UTF_8) }

    @BeforeEach
    void beforeMethod() {
        assumeSocketExists robobeeSocket
    }

    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }

    void createDummyCommands(File dir) {
    }

    def setupServiceScript(Map args, HostServiceScript script) {
        return script
    }
}
