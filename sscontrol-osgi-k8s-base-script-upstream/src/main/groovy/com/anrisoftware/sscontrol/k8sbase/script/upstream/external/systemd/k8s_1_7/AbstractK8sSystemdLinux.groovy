/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.k8sbase.script.upstream.external.systemd.k8s_1_7

import javax.inject.Inject

import com.anrisoftware.sscontrol.groovy.script.external.ScriptBase
import com.anrisoftware.sscontrol.utils.systemd.external.SystemdUtils
import com.anrisoftware.sscontrol.utils.systemd.external.SystemdUtilsFactory

import groovy.util.logging.Slf4j

/**
 * Configures the K8s service for Systemd.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class AbstractK8sSystemdLinux extends ScriptBase {

    SystemdUtils systemd

    @Inject
    void setSystemd(SystemdUtilsFactory factory) {
        this.systemd = factory.create this
    }

    def stopServices() {
        systemd.stopServices()
    }

    def startServices() {
        systemd.startServices()
    }

    def enableServices() {
        systemd.enableServices()
    }

    @Override
    def getLog() {
        log
    }
}
