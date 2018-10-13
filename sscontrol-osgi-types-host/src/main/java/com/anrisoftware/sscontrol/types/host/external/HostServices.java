package com.anrisoftware.sscontrol.types.host.external;

/*-
 * #%L
 * sscontrol-osgi - types-host
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

import java.util.List;
import java.util.Set;

/**
 * Host services repository.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface HostServices {

    <T extends HostServiceService> T getAvailableService(String name);

    Set<String> getAvailableServices();

    void putAvailableService(String name, HostServiceService service);

    void removeAvailableService(String name);

    <T extends PreHostService> T getAvailablePreService(String name);

    void putAvailablePreService(String name, PreHostService service);

    void removeAvailablePreService(String name);

    <T extends HostServiceScriptService> T getAvailableScriptService(
            ScriptInfo info);

    void putAvailableScriptService(ScriptInfo info,
            HostServiceScriptService service);

    void removeAvailableScriptService(ScriptInfo name);

    Set<ScriptInfo> getAvailableScriptServices();

    void addService(String name, HostService service);

    void removeService(String name, int index);

    Set<String> getServices();

    List<HostService> getServices(String name);

    void putState(String name, Object state);

    <T> T getState(String name);

    HostTargets<?, ?> getTargets();

    HostTargets<?, ?> getClusters();
}
