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
package com.anrisoftware.sscontrol.sshd.service;

import java.util.List;

import com.anrisoftware.sscontrol.types.host.HostService;
import com.anrisoftware.sscontrol.types.misc.DebugLogging;

/**
 * Sshd service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface Sshd extends HostService {

    /**
     * Returns the debug logging.
     * 
     * @return the {@link DebugLogging}
     */
    DebugLogging getDebugLogging();

    /**
     * Return the list of allowed users.
     */
    List<String> getAllowUsers();

    /**
     * The binding of the sshd.
     */
    Binding getBinding();
}
