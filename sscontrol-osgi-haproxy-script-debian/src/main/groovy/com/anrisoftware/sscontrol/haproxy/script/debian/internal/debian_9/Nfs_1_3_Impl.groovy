package com.anrisoftware.sscontrol.haproxy.script.debian.internal.debian_9

import com.anrisoftware.propertiesutils.ContextProperties
import com.anrisoftware.sscontrol.nfs.script.nfs_1_3.external.Nfs_1_3

import groovy.util.logging.Slf4j

import static com.anrisoftware.sscontrol.haproxy.script.debian.internal.debian_9.Nfs_1_3_Debian_9_Service.*

import javax.inject.Inject

/**
 * Nfs 1.3 on Debian 9 systems.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
com.anrisoftware.sscontrol.haproxy.script.debian.internal.debian_9   Nfs_1_3_Debian_9_Properties propertiesProvider
    
    @Override
    ContextProperties getDefaultProperties() {
        propertiesProvider.get()
    }

    @Override
    def run() {
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
