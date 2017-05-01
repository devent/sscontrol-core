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
package com.anrisoftware.sscontrol.repo.git.linux.internal.debian_8

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*

import org.junit.Test

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
class GitRepo_Debian_Test extends Abstract_Git_Script_Debian_Test {

    @Test
    void "git_ssh"() {
        def test = [
            name: "git_ssh",
            input: """
service "ssh", host: "localhost"
service "git", group: 'wordpress-app' with {
    remote url: "git://git@github.com/user/wordpress-app"
    credentials "ssh", key: "${idRsa}"
}
""",
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource GitRepo_Debian_Test, dir, "dpkg.out", "${args.test.name}_dpkg_expected.txt"
                assertFileResource GitRepo_Debian_Test, dir, "apt-get.out", "${args.test.name}_apt_get_expected.txt"
                assertFileResource GitRepo_Debian_Test, dir, "scp.out", "${args.test.name}_scp_expected.txt"
                assertFileResource GitRepo_Debian_Test, dir, "mkdir.out", "${args.test.name}_mkdir_expected.txt"
                assertFileResource GitRepo_Debian_Test, dir, "cp.out", "${args.test.name}_cp_expected.txt"
                assertFileResource GitRepo_Debian_Test, dir, "cat.out", "${args.test.name}_cat_expected.txt"
                assertFileResource GitRepo_Debian_Test, dir, "git.out", "${args.test.name}_git_expected.txt"
            },
        ]
        doTest test
    }

    @Test
    void "git_file"() {
        def test = [
            name: "git_file",
            input: """
service "ssh", host: "localhost"
service "git", group: 'wordpress-app' with {
    remote url: "/user/wordpress-app.git"
}
""",
            generatedDir: folder.newFolder(),
            expected: { Map args ->
                File dir = args.dir
                File gen = args.test.generatedDir
                assertFileResource GitRepo_Debian_Test, dir, "git.out", "${args.test.name}_git_expected.txt"
            },
        ]
        doTest test
    }
}