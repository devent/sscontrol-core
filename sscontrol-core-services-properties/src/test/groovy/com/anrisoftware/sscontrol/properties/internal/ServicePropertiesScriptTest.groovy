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
package com.anrisoftware.sscontrol.properties.internal

import static com.anrisoftware.globalpom.utils.TestUtils.*

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesImpl.HostServicePropertiesImplFactory
import com.anrisoftware.sscontrol.properties.internal.PropertiesStub.PropertiesStubFactory
import com.anrisoftware.sscontrol.properties.internal.PropertiesStub.PropertiesStubServiceImpl
import com.anrisoftware.sscontrol.types.host.HostServicePropertiesService
import com.anrisoftware.sscontrol.types.misc.TypesModule
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.assistedinject.FactoryModuleBuilder

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
class ServicePropertiesScriptTest {

    @Inject
    PropertiesStubServiceImpl propertiesService

    @Test
    void "properties script"() {
        def testCases = [
            [
                input: """
dhclient.with {
    property << 'default_option = rfc3442-classless-static-routes code 121 = array of unsigned integer 8'
    property << 'default_sends = host-name "<hostname>"'
    property << 'default_sends = host-name "my.hostname"'
    property << 'multi_line = first line \\\nsecond line'
}
dhclient
""",
                expected: { PropertiesStub dhclient ->
                    assert dhclient.serviceProperties.propertyNames.size() == 3
                    assert dhclient.serviceProperties.getProperty('default_sends') == 'host-name "my.hostname"'
                    assert dhclient.serviceProperties.getProperty('multi_line') == 'first line second line'
                    assert dhclient.serviceProperties.propertyNames.containsAll([
                        'default_option',
                        'default_sends',
                        'multi_line'
                    ])
                },
            ],
        ]
        testCases.eachWithIndex { Map test, int k ->
            log.info '{}. case: {}', k, test
            def database = Eval.me 'dhclient', propertiesService.create([:]), test.input as String
            log.info '{}. case: dhclient: {}', k, database
            Closure expected = test.expected
            expected database
        }
    }

    Injector injector

    @BeforeEach
    void setupTest() {
        toStringStyle
        this.injector = Guice.createInjector(
                new PropertiesModule(),
                new TypesModule(),
                new PropertiesUtilsModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(HostServicePropertiesService).to(HostServicePropertiesImplFactory)
                        install(new FactoryModuleBuilder().implement(PropertiesStub, PropertiesStub).build(PropertiesStubFactory))
                    }
                })
        injector.injectMembers(this)
    }
}
