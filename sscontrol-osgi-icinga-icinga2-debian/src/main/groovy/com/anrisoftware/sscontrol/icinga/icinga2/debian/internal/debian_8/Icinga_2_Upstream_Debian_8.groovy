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
package com.anrisoftware.sscontrol.icinga.icinga2.debian.internal.debian_8

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.groovy.script.external.ScriptBase

import groovy.util.logging.Slf4j

/**
 * Installs Icinga 2 from the upstream repository for Debian 8.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class Icinga_2_Upstream_Debian_8 extends ScriptBase {

    @Inject
    Icinga_2_Debian_8_Properties debianPropertiesProvider

    @Override
    Object run() {
        installIcinga()
        installNagiosPlugins()
    }

    def installIcinga() {
        def packages = [
            [package: icingaPackage, version: icingaVersion],
        ]
        if (checkAptPackagesVersion(packages)) {
            return
        }
        addAptPackagesRepository([name: "icinga-${distributionName}"])
        installAptPackages(packages.inject([]) { result, map ->
            result << "${map.package}=${map.version}"
        })
    }

    def installNagiosPlugins() {
        def packages = [
            [package: "nagios-plugins", version: nagiosPluginsVersion],
        ]
        if (checkAptPackage(packages)) {
            return
        }
        addAptBackportsRepository()
        installAptBackportsPackages(packages.inject([]) { result, map ->
            result << "${map.package}=${map.version}"
        })
    }

    def stopServices() {
        stopSystemdService 'icinga2'
    }

    def startServices() {
        startEnableSystemdService 'icinga2'
    }

    String getIcingaPackage() {
        properties.getProperty "icinga_package", defaultProperties
    }

    String getIcingaVersion() {
        properties.getProperty "icinga_version", defaultProperties
    }

    String getNagiosPluginsVersion() {
        properties.getProperty "nagios_plugins_version", defaultProperties
    }

    @Override
    ContextProperties getDefaultProperties() {
        debianPropertiesProvider.get()
    }

    @Override
    def getLog() {
        log
    }
}