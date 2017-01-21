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
package com.anrisoftware.sscontrol.etcd.internal;

import static com.anrisoftware.sscontrol.etcd.internal.EtcdImplLogger._.advertiseAdded;
import static com.anrisoftware.sscontrol.etcd.internal.EtcdImplLogger._.bindingAdded;
import static com.anrisoftware.sscontrol.etcd.internal.EtcdImplLogger._.memberNameSet;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;
import com.anrisoftware.sscontrol.etcd.external.Binding;

/**
 * Logging for {@link EtcdImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class EtcdImplLogger extends AbstractLogger {

    enum _ {

        bindingAdded("Binding {} added for {}"),

        memberNameSet("Member name {} set for {}"),

        advertiseAdded("Advertise {} added for {}");

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
     * Sets the context of the logger to {@link EtcdImpl}.
     */
    public EtcdImplLogger() {
        super(EtcdImpl.class);
    }

    void bindingAdded(EtcdImpl etcd, Binding binding) {
        debug(bindingAdded, binding, etcd);
    }

    void memberNameSet(EtcdImpl etcd, String member) {
        debug(memberNameSet, member, etcd);
    }

    void advertiseAdded(EtcdImpl etcd, Binding binding) {
        debug(advertiseAdded, binding, etcd);
    }
}