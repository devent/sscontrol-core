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
package com.anrisoftware.sscontrol.debug.internal

import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingImpl.DebugLoggingImplFactory
import com.anrisoftware.sscontrol.types.misc.DebugLogging
import com.anrisoftware.sscontrol.types.misc.TypesModule
import com.google.inject.Guice

import groovy.util.logging.Slf4j

@Slf4j
class DebugScriptTest {

    @Inject
    DebugLoggingImplFactory debugFactory

    @Test
    void "debug script"() {
        def testCases = [
            [
                input: """
debugParent.with {
    debug "general", level: 1, file: "/var/log/mysql/mysql.log"
    debug "error", level: 1
    debug "slow-queries", level: 1
}
debugParent
""",
                expected: [
                    modulesSize: 3,
                ]
            ],
            [
                input: """
debugLogs = []
debugLogs << [name: "error", level: 1]
debugLogs << [name: "slow-queries", level: 1]
debugLogs << [name: "general", level: 1, file: "/var/log/mysql/mysql.log"]
debugLogs.each { debugParent.debug it }
debugParent
""",
                expected: [
                    modulesSize: 3,
                ]
            ],
            [
                input: """
debugParent.with {
    debug << [name: "error", level: 1]
    debug << [name: "slow-queries", level: 1]
    debug << [name: "general", level: 1, file: "/var/log/mysql/mysql.log"]
}
debugParent
""",
                expected: [
                    modulesSize: 3,
                ]
            ],
        ]
        testCases.eachWithIndex { Map test, int k ->
            log.info '{}. case: {}', k, test
            DebugLogging logging = Eval.me('debugParent', debugFactory.create(), test.input as String) as DebugLogging
            Map expected = test.expected
            assert logging.modules.size() == expected.modulesSize
        }
    }

    @BeforeEach
    void setupTest() {
        toStringStyle
        Guice.createInjector(
                new DebugLoggingModule(),
                new TypesModule(),
                new StringsModule()
                ).injectMembers(this)
    }
}
