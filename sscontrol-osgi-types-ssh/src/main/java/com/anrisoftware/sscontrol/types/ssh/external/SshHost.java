package com.anrisoftware.sscontrol.types.ssh.external;

/*-
 * #%L
 * sscontrol-osgi - types-ssh
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

import java.io.File;
import java.net.URI;

import com.anrisoftware.sscontrol.types.host.external.SystemInfo;
import com.anrisoftware.sscontrol.types.host.external.TargetHost;

/**
 * <i>Ssh</i> host.
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
public interface SshHost extends TargetHost {

    String getUser();

    /**
     * Returns the private SSH key.
     */
    URI getKey();

    /**
     * Returns the socket file of a control master for multiplexing.
     */
    File getSocket();

    SystemInfo getSystem();
}
