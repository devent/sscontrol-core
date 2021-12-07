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
package com.anrisoftware.sscontrol.utils.centos.external

import javax.inject.Inject

import com.anrisoftware.sscontrol.types.host.HostServiceScript
import com.google.inject.assistedinject.Assisted

/**
 * CentOS 7 utilities.
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
class Centos_7_Utils extends CentosUtils {

    @Inject
    Centos_7_Properties propertiesProvider

    @Inject
    Centos_7_Utils(@Assisted HostServiceScript script) {
        super(script)
    }

    @Override
    public Properties getDefaultProperties() {
        propertiesProvider.get()
    }
}
