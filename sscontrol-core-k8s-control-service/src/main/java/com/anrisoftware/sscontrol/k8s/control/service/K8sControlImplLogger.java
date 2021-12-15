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
package com.anrisoftware.sscontrol.k8s.control.service;

import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.accountSet;
import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.admissionsAdded;
import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.allowPrivilegedSet;
import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.authenticationAdded;
import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.authorizationAdded;
import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.bindingSet;
import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.caSet;
import static com.anrisoftware.sscontrol.k8s.control.service.K8sControlImplLogger.m.nodeAdded;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;
import com.anrisoftware.sscontrol.tls.Tls;

/**
 * Logging for {@link K8sControlImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class K8sControlImplLogger extends AbstractLogger {

    enum m {

        caSet("CA {} set for {}"),

        authenticationAdded("Authentication {} added for {}"),

        authorizationAdded("Authorization {} added for {}"),

        admissionsAdded("Admissions {} added for {}"),

        allowPrivilegedSet("Allow privileged {} set for {}"),

        bindingSet("Binding {} set for {}"),

        accountSet("Account {} set for {}"),

        nodeAdded("Node {} added to {}");

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
     * Sets the context of the logger to {@link K8sControlImpl}.
     */
    public K8sControlImplLogger() {
        super(K8sControlImpl.class);
    }

    void authenticationAdded(K8sControlImpl k8s, Authentication auth) {
        debug(authenticationAdded, auth, k8s);
    }

    void authorizationAdded(K8sControlImpl k8s, Authorization auth) {
        debug(authorizationAdded, auth, k8s);
    }

    void admissionsAdded(K8sControlImpl k8s, String property) {
        debug(admissionsAdded, property, k8s);
    }

    void allowPrivilegedSet(K8sControlImpl k8s, boolean allow) {
        debug(allowPrivilegedSet, allow, k8s);
    }

    void bindingSet(K8sControlImpl k8s, Binding binding) {
        debug(bindingSet, binding, k8s);
    }

    void accountSet(K8sControlImpl k8s, Account account) {
        debug(accountSet, account, k8s);
    }

    void nodeAdded(K8sControlImpl k8s, Object node) {
        debug(nodeAdded, node, k8s);
    }

    void caSet(K8sControlImpl k8s, Tls tls) {
        debug(caSet, tls, k8s);
    }

}
