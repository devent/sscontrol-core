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
package com.anrisoftware.sscontrol.icinga.script.debian.internal.debian_9.icinga_2

import com.anrisoftware.propertiesutils.AbstractContextPropertiesProvider

/**
 * Icinga 2 Debian 9 properties provider from
 * {@code "/icinga_2_debian_9.properties"}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
class IcingaDebianProperties extends AbstractContextPropertiesProvider {

    private static final URL RESOURCE = IcingaDebianProperties.class.getResource("/icinga_2_debian_9.properties")

    IcingaDebianProperties() {
        super(IcingaDebianProperties.class, RESOURCE)
    }
}