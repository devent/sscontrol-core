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
package com.anrisoftware.sscontrol.k8scluster.script.linux.k8scluster_1_2x;

import com.anrisoftware.sscontrol.types.host.HostServiceScriptFactory;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 *
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class K8sClusterLinuxServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HostServiceScriptFactory.class)
                .annotatedWith(Names.named("cluster-service"))
                .to(K8sClusterLinuxService.class);
    }

}