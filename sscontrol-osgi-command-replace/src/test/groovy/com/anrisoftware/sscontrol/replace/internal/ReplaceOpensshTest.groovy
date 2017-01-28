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
package com.anrisoftware.sscontrol.replace.internal

import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*

import javax.inject.Inject

import org.apache.commons.io.FileUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import com.anrisoftware.globalpom.textmatch.tokentemplate.TokensTemplateModule
import com.anrisoftware.globalpom.threads.external.core.Threads
import com.anrisoftware.sscontrol.replace.external.Replace.ReplaceFactory
import com.anrisoftware.sscontrol.replace.internal.ParseSedSyntax.ParseSedSyntaxFactory
import com.anrisoftware.sscontrol.shell.external.utils.AbstractCmdTestBase
import com.anrisoftware.sscontrol.shell.external.utils.CmdUtilsModules
import com.anrisoftware.sscontrol.shell.external.utils.SshFactory
import com.anrisoftware.sscontrol.shell.internal.cmd.CmdModule
import com.anrisoftware.sscontrol.shell.internal.copy.CopyModule
import com.anrisoftware.sscontrol.shell.internal.fetch.FetchModule
import com.anrisoftware.sscontrol.shell.internal.scp.ScpModule
import com.anrisoftware.sscontrol.shell.internal.ssh.SshShellModule
import com.google.inject.Module

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
class ReplaceOpensshTest extends AbstractCmdTestBase {

    static Threads threads

    @Inject
    ReplaceFactory replaceFactory

    @Inject
    ParseSedSyntaxFactory parseSedSyntaxFactory

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    static Map expectedResources = [
        args_dest_search_replace_scp: ReplaceOpensshTest.class.getResource('args_dest_search_replace_scp_expected.txt'),
        args_privileged_dest_search_replace_scp: ReplaceOpensshTest.class.getResource('args_privileged_dest_search_replace_scp_expected.txt'),
        args_privileged_dest_search_replace_sudo: ReplaceOpensshTest.class.getResource('args_privileged_dest_search_replace_sudo_expected.txt'),
        args_privileged_dest_search_replace_cp: ReplaceOpensshTest.class.getResource('args_privileged_dest_search_replace_cp_expected.txt'),
        args_privileged_dest_search_replace_rm: ReplaceOpensshTest.class.getResource('args_privileged_dest_search_replace_rm_expected.txt'),
        args_dest_sed_replace_scp: ReplaceOpensshTest.class.getResource('args_dest_sed_replace_scp_expected.txt'),
    ]

    @Test
    void "args_dest_search_replace"() {
        def test = [
            name: "args_dest_search_replace",
            args: [
                dest: "/tmp/aaa.txt",
                search: /(?m)^test=.*/,
                replace: 'test=replaced',
            ],
            expected: { Map args ->
                File dir = args.dir as File
                String name = args.name as String
                assertFileResource ReplaceOpensshTest, dir, "scp.out", "${args.test.name}_scp_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "cp.out", "${args.test.name}_cp_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "rm.out", "${args.test.name}_rm_expected.txt"
            },
        ]
        def tmp = folder.newFolder()
        test.args.tmp = folder.newFile("replace_test.txt")
        FileUtils.write test.args.tmp, 'test=foo\n'
        log.info '\n######### {} #########\ncase: {}', test.name, test
        test.host = SshFactory.localhost(injector).hosts[0]
        doTest test, tmp
    }

    @Test
    void "args_privileged_dest_search_replace"() {
        def test = [
            name: "args_privileged_dest_search_replace",
            args: [
                dest: "/tmp/aaa.txt",
                search: /(?m)^test=.*/,
                replace: 'test=replaced',
                privileged: true,
            ],
            expected: { Map args ->
                File dir = args.dir as File
                String name = args.name as String
                assertFileResource ReplaceOpensshTest, dir, "scp.out", "${args.test.name}_scp_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "cp.out", "${args.test.name}_cp_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "rm.out", "${args.test.name}_rm_expected.txt"
            },
        ]
        def tmp = folder.newFolder()
        test.args.tmp = folder.newFile("replace_test.txt")
        FileUtils.write test.args.tmp, 'test=foo\n'
        log.info '\n######### {} #########\ncase: {}', test.name, test
        test.host = SshFactory.localhost(injector).hosts[0]
        doTest test, tmp
    }

    @Test
    void "args_dest_sed_replace"() {
        def test = [
            name: "args_dest_sed_replace",
            args: [
                dest: "/tmp/aaa.txt",
                replace: 's/(?m)^test=.*/test=replaced/',
            ],
            expected: { Map args ->
                File dir = args.dir as File
                String name = args.name as String
                assertFileResource ReplaceOpensshTest, dir, "scp.out", "${args.test.name}_scp_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "sudo.out", "${args.test.name}_sudo_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "cp.out", "${args.test.name}_cp_expected.txt"
                assertFileResource ReplaceOpensshTest, dir, "rm.out", "${args.test.name}_rm_expected.txt"
            },
        ]
        def tmp = folder.newFolder()
        test.args.tmp = folder.newFile("replace_test.txt")
        FileUtils.write test.args.tmp, 'test=foo\n'
        log.info '\n######### {} #########\ncase: {}', test.name, test
        test.host = SshFactory.localhost(injector).hosts[0]
        doTest test, tmp
    }

    @Test
    void "parse sed syntax"() {
        def testCases = [
            [
                string: "s/(?m)^define\\('DB_NAME', '.*?'\\);/define('DB_NAME', 'db');/",
                expected: { Map args ->
                    ParseSedSyntax parser = args.parser as ParseSedSyntax
                    assert parser.search == "(?m)^define\\('DB_NAME', '.*?'\\);"
                    assert parser.replace == "define('DB_NAME', 'db');"
                },
            ],
            [
                string: "s/(?m)^define\\('DB_NAME \\/', '.*?'\\);/define('DB_NAME', 'db');/",
                expected: { Map args ->
                    ParseSedSyntax parser = args.parser as ParseSedSyntax
                    assert parser.search == "(?m)^define\\('DB_NAME \\/', '.*?'\\);"
                    assert parser.replace == "define('DB_NAME', 'db');"
                },
            ],
        ]
        testCases.eachWithIndex { Map test, int k ->
            log.info '{}. case: {}', k, test
            def parser = parseSedSyntaxFactory.create(test.string).parse()
            test.expected([parser: parser])
        }
    }

    def createCmd(Map test, File tmp, int k) {
        def fetch = replaceFactory.create test.args, test.host, this, threads, log
        createEchoCommands tmp, [
            'mkdir',
            'chown',
            'chmod',
            'cp',
            'rm',
            'sudo',
            'scp'
        ]
        return fetch
    }

    @Before
    void setupTest() {
        super.setupTest()
        this.threads = CmdUtilsModules.getThreads(injector)
    }

    Module[] getAdditionalModules() {
        [
            new TokensTemplateModule(),
            new SshShellModule(),
            new CmdModule(),
            new ReplaceModule(),
            new ScpModule(),
            new CopyModule(),
            new FetchModule(),
            new CmdUtilsModules(),
        ] as Module[]
    }
}
