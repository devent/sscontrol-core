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
package com.anrisoftware.sscontrol.repo.git.service;

import static com.anrisoftware.sscontrol.repo.git.service.SshCredentialsImplLogger.m.keySet;

import java.net.URI;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;

/**
 * Logging for {@link SshCredentialsImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class SshCredentialsImplLogger extends AbstractLogger {

    enum m {

        keySet("Key {} set for {}");

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
     * Sets the context of the logger to {@link SshCredentialsImpl}.
     */
    public SshCredentialsImplLogger() {
        super(SshCredentialsImpl.class);
    }

    void keySet(SshCredentialsImpl credentials, URI key) {
        debug(keySet, key, credentials);
    }
}
