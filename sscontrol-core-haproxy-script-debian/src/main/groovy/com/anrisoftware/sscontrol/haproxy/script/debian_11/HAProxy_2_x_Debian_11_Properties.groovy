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
package com.anrisoftware.sscontrol.haproxy.script.debian_11

import com.anrisoftware.propertiesutils.AbstractContextPropertiesProvider

/**
 * HAProxy 2.x Debian 11 properties provider from {@code "/haproxy_2_x_debian_11.properties"}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
class HAProxy_2_x_Debian_11_Properties extends AbstractContextPropertiesProvider {

    private static final URL RESOURCE = HAProxy_2_x_Debian_11_Properties.class.getResource("/haproxy_2_x_debian_11.properties")

    HAProxy_2_x_Debian_11_Properties() {
        super(HAProxy_2_x_Debian_11_Properties.class, RESOURCE)
    }
}
