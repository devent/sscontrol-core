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
package com.anrisoftware.sscontrol.k8s.fromhelm.script.helm_3_x.linux;

import java.net.URL;

import com.anrisoftware.propertiesutils.AbstractContextPropertiesProvider;

/**
 * From repository service for Kubernetes properties provider from
 * {@code "/from_helm_3_x_linux.properties"}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@SuppressWarnings("serial")
public class FromHelmLinuxProperties extends AbstractContextPropertiesProvider {

    private static final URL RESOURCE = FromHelmLinuxProperties.class.getResource("/from_helm_3_x_linux.properties");

    FromHelmLinuxProperties() {
        super(FromHelmLinuxProperties.class, RESOURCE);
    }
}
