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
package com.anrisoftware.sscontrol.cilium.service

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import javax.inject.Inject

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import com.anrisoftware.globalpom.core.durationformat.DurationFormatModule
import com.anrisoftware.globalpom.core.strings.StringsModule
import com.anrisoftware.propertiesutils.PropertiesUtilsModule
import com.anrisoftware.sscontrol.cilium.service.CiliumImpl.CiliumImplFactory
import com.anrisoftware.sscontrol.debug.internal.DebugLoggingModule
import com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesServiceModule
import com.anrisoftware.sscontrol.properties.internal.PropertiesModule
import com.anrisoftware.sscontrol.services.host.HostServicesModule
import com.anrisoftware.sscontrol.services.host.HostServicesImpl.HostServicesImplFactory
import com.anrisoftware.sscontrol.services.targets.TargetsModule
import com.anrisoftware.sscontrol.services.targets.TargetsServiceModule
import com.anrisoftware.sscontrol.types.host.HostServices
import com.anrisoftware.sscontrol.types.misc.TypesModule
import com.anrisoftware.sscontrol.types.ssh.external.Ssh
import com.anrisoftware.sscontrol.utils.systemmappings.internal.SystemNameMappingsModule
import com.google.inject.Guice

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
class CiliumScriptTest {

    @Inject
    HostServicesImplFactory servicesFactory

    @Inject
    CiliumImplFactory hostnameFactory

    static Stream<Arguments> cilium_service() {
        return Stream.of(
                of("""
service "cilium"
""", { HostServices services ->
                    assert services.getServices('cilium').size() == 1
                    Cilium service = services.getServices('cilium')[0] as Cilium
                    assert service
                },
                )
                )
    }

    @ParameterizedTest
    @MethodSource
    void cilium_service(def input, def expected) {
        def services = servicesFactory.create()
        services.targets.addTarget([getGroup: {'default'}, getHosts: { []}] as Ssh)
        services.putAvailableService 'cilium', hostnameFactory
        Eval.me 'service', services, input as String
        expected services
    }

    @BeforeEach
    void setupTest() {
        toStringStyle
        Guice.createInjector(
                new CiliumModule(),
                new DebugLoggingModule(),
                new TypesModule(),
                new StringsModule(),
                new DurationFormatModule(),
                new HostServicesModule(),
                new TargetsModule(),
                new TargetsServiceModule(),
                new PropertiesModule(),
                new PropertiesUtilsModule(),
                new SystemNameMappingsModule(),
                new HostServicePropertiesServiceModule(),
                ).injectMembers(this)
    }
}
