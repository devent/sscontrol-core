/*
 * Copyright 2016 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.fail2ban.internal

import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.joda.time.Duration
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger

import com.anrisoftware.globalpom.durationformat.DurationFormatModule
import com.anrisoftware.globalpom.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.fail2ban.external.Backend
import com.anrisoftware.sscontrol.fail2ban.external.Fail2ban
import com.anrisoftware.sscontrol.fail2ban.external.Jail
import com.anrisoftware.sscontrol.fail2ban.external.Type
import com.anrisoftware.sscontrol.fail2ban.internal.Fail2banImpl.Fail2banImplFactory
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesImpl.HostServicePropertiesImplFactory
import com.anrisoftware.sscontrol.services.internal.HostServicesModule
import com.anrisoftware.sscontrol.services.internal.TargetsModule
import com.anrisoftware.sscontrol.services.internal.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.internal.TargetsImpl.TargetsImplFactory
import com.anrisoftware.sscontrol.types.external.HostPropertiesService
import com.anrisoftware.sscontrol.types.external.HostServices
import com.anrisoftware.sscontrol.types.external.Ssh
import com.anrisoftware.sscontrol.types.external.TargetsService
import com.anrisoftware.sscontrol.types.internal.TypesModule
import com.google.inject.AbstractModule
import com.google.inject.Guice

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@CompileStatic
class Fail2banScriptTest {

    @Inject
    HostServicesImplFactory servicesFactory

    @Inject
    Fail2banImplFactory hostnameFactory

    @Test
    void "fail2ban service"() {
        def testCases = [
            [
                input: """
service "fail2ban" with {
    jail "apache"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                },
            ],
            [
                input: """
service "fail2ban", notify: "admin@localhost" with {
    jail "apache"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.defaultJail.notify == "admin@localhost"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                },
            ],
            [
                input: """
service "fail2ban" with {
    notify "admin@localhost"
    jail "apache"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.defaultJail.notify == "admin@localhost"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                },
            ],
            [
                input: """
service "fail2ban" with {
    notify address: "admin@localhost"
    jail "apache"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.defaultJail.notify == "admin@localhost"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                },
            ],
            [
                input: """
service "fail2ban" with {
    jail "apache", notify: "admin@localhost"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                    assert j.notify == "admin@localhost"
                },
            ],
            [
                input: """
service "fail2ban" with {
    jail "apache" with {
        notify "admin@localhost"
    }
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                    assert j.notify == "admin@localhost"
                },
            ],
            [
                input: """
service "fail2ban" with {
    jail "apache" with {
        notify address: "admin@localhost"
    }
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                    assert j.notify == "admin@localhost"
                },
            ],
            [
                input: """
import com.anrisoftware.sscontrol.fail2ban.external.Backend
import com.anrisoftware.sscontrol.fail2ban.external.Type

service "fail2ban" with {
    banning retries: 3, time: "PT10M", backend: Backend.polling, type: Type.deny
    jail "apache"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.defaultJail.notify == null
                    assert fail.defaultJail.banningRetries == 3
                    assert fail.defaultJail.banningTime == Duration.standardMinutes(10)
                    assert fail.defaultJail.banningBackend == Backend.polling
                    assert fail.defaultJail.banningType == Type.deny
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                },
            ],
            [
                input: """
service "fail2ban" with {
    ignore << "128.0.0.1"
    jail "apache"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.defaultJail.notify == null
                    assert fail.defaultJail.ignoreAddresses.size() == 1
                    assert fail.defaultJail.ignoreAddresses.containsAll(['128.0.0.1'])
                    assert fail.defaultJail.banningRetries == null
                    assert fail.defaultJail.banningTime == null
                    assert fail.defaultJail.banningBackend == null
                    assert fail.defaultJail.banningType == null
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                },
            ],
            [
                input: """
service "fail2ban" with {
    ignore address: "128.0.0.1"
    jail "apache"
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.defaultJail.ignoreAddresses.size() == 1
                    assert fail.defaultJail.ignoreAddresses.containsAll(['128.0.0.1'])
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                },
            ],
            [
                input: """
service "fail2ban" with {
    jail "apache" with {
        ignore << "128.0.0.1"
    }
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                    assert j.ignoreAddresses.size() == 1
                    assert j.ignoreAddresses.containsAll(['128.0.0.1'])
                },
            ],
            [
                input: """
service "fail2ban" with {
    jail "apache" with {
        ignore address: "128.0.0.1"
    }
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                    assert j.ignoreAddresses.size() == 1
                    assert j.ignoreAddresses.containsAll(['128.0.0.1'])
                },
            ],
            [
                input: """
service "fail2ban" with {
    jail "apache" with {
        banning app: "Apache"
    }
}
""",
                expected: { HostServices services ->
                    assert services.getServices('fail2ban').size() == 1
                    Fail2ban fail = services.getServices('fail2ban')[0] as Fail2ban
                    assert fail.defaultJail.service == "default"
                    assert fail.jails.size() == 1
                    Jail j = fail.jails[0]
                    assert j.service == "apache"
                    assert j.banningApp == "Apache"
                },
            ],
        ]
        testCases.eachWithIndex { Map test, int k ->
            log.info '\n#### {}. case: {}', k, test
            def services = servicesFactory.create()
            services.targets.addTarget([getGroup: {'default'}, getHosts: { []}] as Ssh)
            services.putAvailableService 'fail2ban', hostnameFactory
            Eval.me 'service', services, test.input as String
            Closure expected = test.expected
            expected services
        }
    }

    @Before
    void setupTest() {
        toStringStyle
        Guice.createInjector(
                new Fail2banModule(),
                new DebugLoggingModule(),
                new TypesModule(),
                new StringsModule(),
                new DurationFormatModule(),
                new HostServicesModule(),
                new TargetsModule(),
                new PropertiesModule(),
                new PropertiesUtilsModule(),
                new AbstractModule() {

                    @Override
                    protected void configure() {
                        bind TargetsService to TargetsImplFactory
                        bind(HostPropertiesService).to(HostServicePropertiesImplFactory)
                    }
                }).injectMembers(this)
    }
}