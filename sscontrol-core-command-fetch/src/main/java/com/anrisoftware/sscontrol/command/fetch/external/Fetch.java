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
package com.anrisoftware.sscontrol.command.fetch.external;

import java.util.Map;

import com.anrisoftware.globalpom.exec.external.core.ProcessTask;
import com.anrisoftware.globalpom.threads.external.core.Threads;
import com.anrisoftware.sscontrol.types.app.external.AppException;
import com.anrisoftware.sscontrol.types.ssh.SshHost;
import com.google.inject.assistedinject.Assisted;

/**
 * Fetch command.
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public interface Fetch {

    static final String PWD_ARG = "pwd";

    static final String SRC_ARG = "src";

    static final String DEST_ARG = "dest";

    static final String LOG_ARG = "log";

    /**
     * Factory to create the fetch command.
     *
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface FetchFactory {

        Fetch create(@Assisted Map<String, Object> args, @Assisted SshHost host,
                @Assisted("parent") Object parent, @Assisted Threads threads,
                @Assisted("log") Object log);
    }

    /**
     * Executes the fetch command.
     * 
     * @throws AppException
     */
    ProcessTask call() throws AppException;

}
