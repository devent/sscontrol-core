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
package com.anrisoftware.sscontrol.nfs.script.debian_11

import com.anrisoftware.propertiesutils.AbstractContextPropertiesProvider

/**
 * Nfs 1.3 Debian 11 properties provider from
 * {@code "/nfs_1_3_debian_11.properties"}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
class Nfs_1_3_Debian_11_Properties extends AbstractContextPropertiesProvider {

    private static final URL RESOURCE = Nfs_1_3_Debian_11_Properties.class.getResource("/nfs_1_3_debian_11.properties")

    Nfs_1_3_Debian_11_Properties() {
        super(Nfs_1_3_Debian_11_Properties.class, RESOURCE)
    }
}
