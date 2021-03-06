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
package com.anrisoftware.sscontrol.k8s.script.control_1_2x.debian_11

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.utils.debian.Debian_11_TestUtils.*

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
abstract class AbstractControlScriptTest extends AbstractControlRunnerTest {

    static final Map certs = [
        ca: AbstractControlScriptTest.class.getResource('cert_ca.txt'),
        cert: AbstractControlScriptTest.class.getResource('cert_cert.txt'),
        key: AbstractControlScriptTest.class.getResource('cert_key.txt'),
    ]

    static final URL kubeadmCommand = AbstractControlScriptTest.class.getResource('script_kubeadm_command.txt')

    static final URL fstabSwapFile = AbstractControlScriptTest.class.getResource('fstab_swap.txt')

    void createDummyCommands(File dir) {
        createCommand catCommand, dir, "cat"
        createCommand grepCommand, dir, 'grep'
        createCommand whichufwnotfoundCommand, dir, 'which'
        createCommand kubeadmCommand, dir, 'kubeadm'
        new File(dir, "/etc/apt/sources.list.d").mkdirs()
        createFile fstabSwapFile, new File(dir, "/etc"), 'fstab'
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
            'apt-mark',
            'systemctl',
            'which',
            'sha256sum',
            'mv',
            'basename',
            'wget',
            'useradd',
            'tar',
            'curl',
            'sleep',
            'kubectl',
            'ufw',
            'modprobe',
            'sed',
            'swapoff'
        ]
    }
}
