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
package com.anrisoftware.sscontrol.ssh.service;

import static com.anrisoftware.sscontrol.ssh.service.SshImplLogger.m.groupSet;
import static com.anrisoftware.sscontrol.ssh.service.SshImplLogger.m.hostAdded;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;
import com.anrisoftware.sscontrol.types.ssh.SshHost;

/**
 * Logging for {@link SshImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class SshImplLogger extends AbstractLogger {

    enum m {

        hostAdded("Host added {} to {}"),

        groupSet("Group '{}' set for {}");

        private String name;

        private m(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Sets the context of the logger to {@link SshImpl}.
     */
    public SshImplLogger() {
        super(SshImpl.class);
    }

    void hostAdded(SshImpl ssh, SshHost host) {
        debug(hostAdded, host, ssh);
    }

    void groupSet(SshImpl ssh, String group) {
        debug(groupSet, group, ssh);
    }
}
