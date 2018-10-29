/*-
 * #%L
 * sscontrol-osgi - collectd-script-centos
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
package com.anrisoftware.sscontrol.nfs.script.debian.internal.debian_9

import static com.anrisoftware.sscontrol.nfs.script.debian.internal.debian_9.Nfs_1_3_Debian_9_Service.*

import javax.inject.Inject

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.nfs.script.nfs_1_3.external.Nfs_1_3
import com.anrisoftware.sscontrol.nfs.script.nfs_1_3.external.Nfs_1_3_Ufw

import groovy.util.logging.Slf4j

/**
 * Nfs 1.3. for Debian 9.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class Nfs_1_3_Debian_9 extends Nfs_Debian_9 {
    
    @Inject
    Nfs_1_3_Debian_9_Properties propertiesProvider
    
    Nfs_1_3 nfs

    Nfs_1_3_Ufw nfsUfw

    @Inject
    void setNfsFactory(Nfs_1_3_Factory factory) {
        this.nfs = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Inject
    void setNfsFirewalldFactory(Nfs_1_3_Ufw_Factory factory) {
        this.nfsUfw = factory.create(scriptsRepository, service, target, threads, scriptEnv)
    }

    @Override
    ContextProperties getDefaultProperties() {
        propertiesProvider.get()
    }

    @Override
    def run() {
        nfs.setupDefaultOptions()
        stopServices()
        installPackages()
        nfsUfw.configureFilewalld()
        nfs.deployExports()
        nfs.createExports()
        startServices()
        enableServices()
    }

    @Override
    def getLog() {
        log
    }

    @Override
    String getSystemName() {
        SYSTEM_NAME
    }

    @Override
    String getSystemVersion() {
        SYSTEM_VERSION
    }
}
