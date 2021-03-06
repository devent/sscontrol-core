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
package com.anrisoftware.sscontrol.k8s.backup.script.linux.internal.script_1_13;

import com.anrisoftware.sscontrol.k8s.backup.client.external.BackupWorker;
import com.anrisoftware.sscontrol.types.host.HostServiceScript;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class BackupLinuxModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(HostServiceScript.class, BackupLinux.class)
                .build(BackupLinuxFactory.class));
        install(new FactoryModuleBuilder().implement(BackupWorker.class, BackupWorkerImpl.class)
                .build(BackupWorkerImplFactory.class));
        install(new FactoryModuleBuilder().implement(HostServiceScript.class, KubectlClusterLinux.class)
                .build(KubectlClusterLinuxFactory.class));
    }

}
