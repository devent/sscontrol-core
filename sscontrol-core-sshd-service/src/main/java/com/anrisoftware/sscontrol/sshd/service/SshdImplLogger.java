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

import static com.anrisoftware.sscontrol.sshd.service.SshdImplLogger.m.addAllowUser;
import static com.anrisoftware.sscontrol.sshd.service.SshdImplLogger.m.bindingSet;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;

/**
 * Logging for {@link SshdImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class SshdImplLogger extends AbstractLogger {

    enum m {

        addAllowUser("Allow user '{}' added to {}"),

        bindingSet("Binding {} set for {}");

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
     * Sets the context of the logger to {@link SshdImpl}.
     */
    public SshdImplLogger() {
        super(SshdImpl.class);
    }

    void addAllowUser(SshdImpl sshd, String name) {
        debug(addAllowUser, name, sshd);
    }

    void bindingSet(SshdImpl sshd, Binding binding) {
        debug(bindingSet, binding, sshd);
    }

}
