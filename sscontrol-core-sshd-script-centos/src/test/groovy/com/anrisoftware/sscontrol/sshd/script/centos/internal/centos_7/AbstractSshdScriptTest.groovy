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
package com.anrisoftware.sscontrol.sshd.script.centos.internal.centos_7

import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.centos.Centos_7_TestUtils.*

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
abstract class AbstractSshdScriptTest extends AbstractSshdRunnerTest {

    void createDummyCommands(File dir) {
        createCommand catCommand, dir, "cat"
        createCommand yumCommand, dir, "yum"
        createCommand firewallcmdCommand, dir, "firewall-cmd"
        createWhichCommand dir
        createIdCommand dir
        createEchoCommands dir, [
            'mkdir',
            'chown',
            'chmod',
            'sudo',
            'scp',
            'rm',
            'cp',
            'apt-get',
            'systemctl',
            'sha256sum',
            'mv',
            'basename',
            'wget',
            'useradd',
            'tar',
            'dpkg',
            'curl',
            'sleep',
            'file',
            'semanage',
            'grep'
        ]
    }
}
