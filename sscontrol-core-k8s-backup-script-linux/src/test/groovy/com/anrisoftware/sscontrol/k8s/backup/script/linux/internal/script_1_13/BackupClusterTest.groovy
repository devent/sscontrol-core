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
package com.anrisoftware.sscontrol.k8s.backup.script.linux.internal.script_1_13

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.utils.RobobeeSocketCondition.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static org.junit.jupiter.api.Assumptions.*

import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import org.junit.rules.TemporaryFolder

import com.anrisoftware.sscontrol.shell.utils.RobobeeSocketCondition
import com.anrisoftware.sscontrol.types.host.HostServiceScript

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@EnableRuleMigrationSupport
@ExtendWith(RobobeeSocketCondition.class)
class BackupClusterTest extends AbstractBackupRunnerTest {

    @Test
    void "backup wordpress cluster"() {
        def test = [
            name: "backup_wordpress_cluster",
            script: '''
service "ssh", group: "backup" with {
    host "localhost"
}
service "ssh" with {
    host "robobee@robobee-test", socket: robobeeSocket
}
service "k8s-cluster" with {
}
service "backup", target: "backup" with {
    service namespace: "interscalar-com", name: "mariadb"
    destination dir: backupDir
    client key: rsyncKey, proxy: true, timeout: "1h"
}
''',
            scriptVars: [
                robobeeSocket: robobeeSocket,
                backupDir: folder.newFolder(),
                rsyncKey: rsyncKey,
            ],
            expectedServicesSize: 3,
            expected: { Map args ->
                def backupDir = args.test.scriptVars.backupDir
                assert new File(backupDir, "data/data/aria_log_control").isFile()
            },
        ]
        doTest test
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    Map getScriptEnv(Map args) {
        getEmptyScriptEnv args
    }

    void createDummyCommands(File dir) {
    }

    def setupServiceScript(Map args, HostServiceScript script) {
        return script
    }
}
