/*
 * Copyright 2016 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.fail2ban.internal;

import static com.anrisoftware.sscontrol.fail2ban.internal.Fail2banImplLogger._.jailAdded;
import static com.anrisoftware.sscontrol.fail2ban.internal.Fail2banImplLogger._.setDefaultJail;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;
import com.anrisoftware.sscontrol.fail2ban.external.Jail;

/**
 * Logging for {@link Fail2banImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class Fail2banImplLogger extends AbstractLogger {

    enum _ {

        setDefaultJail("Sets default jail {} for {}"),

        jailAdded("Jail {} added to {}");

        private String name;

        private _(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Sets the context of the logger to {@link Fail2banImpl}.
     */
    public Fail2banImplLogger() {
        super(Fail2banImpl.class);
    }

    void defaultJailSet(Fail2banImpl fail2ban, Jail jail) {
        debug(setDefaultJail, jail, fail2ban);
    }

    void jailAdded(Fail2banImpl fail2ban, Jail jail) {
        debug(jailAdded, jail, fail2ban);
    }

}