/*-
 * #%L
 * sscontrol-osgi - k8s-from-repository-script-linux
 * %%
 * Copyright (C) 2016 - 2018 Advanced Natural Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.anrisoftware.sscontrol.k8s.fromrepository.script.linux.internal.script_1_12

import com.anrisoftware.propertiesutils.AbstractContextPropertiesProvider

/**
 * From repository service for Kubernetes properties provider from
 * {@code "/from_repository_1_12_linux.properties"}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
class FromRepositoryLinuxProperties extends AbstractContextPropertiesProvider {

    private static final URL RESOURCE = FromRepositoryLinuxProperties.class.getResource("/from_repository_1_12_linux.properties")

    FromRepositoryLinuxProperties() {
        super(FromRepositoryLinuxProperties.class, RESOURCE)
    }
}
