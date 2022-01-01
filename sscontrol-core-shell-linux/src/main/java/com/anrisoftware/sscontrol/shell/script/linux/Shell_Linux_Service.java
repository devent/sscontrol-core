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
package com.anrisoftware.sscontrol.shell.script.linux;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import com.anrisoftware.sscontrol.types.host.HostService;
import com.anrisoftware.sscontrol.types.host.HostServiceScript;
import com.anrisoftware.sscontrol.types.host.HostServiceScriptFactory;
import com.anrisoftware.sscontrol.types.host.HostServices;
import com.anrisoftware.sscontrol.types.host.SystemInfo;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.utils.systemmappings.external.AbstractScriptInfo;
import com.anrisoftware.sscontrol.utils.systemmappings.external.AbstractSystemInfo;

/**
 *
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class Shell_Linux_Service implements HostServiceScriptFactory {

    static final String SERVICE_NAME = "shell";

    static final String SYSTEM_VERSION = "0";

    static final String SYSTEM_NAME = "linux";

    static final String SYSTEM_SYSTEM = "linux";

    @Inject
    private Shell_Linux_Factory scriptFactory;

    public SystemInfo getSystem() {
        return new AbstractScriptInfo(SERVICE_NAME, new AbstractSystemInfo(SYSTEM_SYSTEM, SYSTEM_NAME, SYSTEM_VERSION) {
        }) {
        };
    }

    @Override
    public HostServiceScript create(HostServices repository, HostService service, TargetHost target,
            ExecutorService threads, Map<String, Object> env) {
        return scriptFactory.create(repository, service, target, threads, env);
    }
}
