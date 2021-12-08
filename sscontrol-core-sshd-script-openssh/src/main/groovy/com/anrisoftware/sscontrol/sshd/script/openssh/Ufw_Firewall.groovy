package com.anrisoftware.sscontrol.sshd.script.openssh;

import javax.inject.Inject

import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.sshd.service.Sshd
import com.anrisoftware.sscontrol.utils.ufw.linux.UfwLinuxUtilsFactory
import com.anrisoftware.sscontrol.utils.ufw.linux.UfwUtils

import groovy.util.logging.Slf4j

/**
 * Configures the Ufw firewall.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Ufw_Firewall extends ScriptBase {

    UfwUtils ufw

    @Inject
    void setUfwUtilsFactory(UfwLinuxUtilsFactory factory) {
        this.ufw = factory.create this
    }

    @Override
    Object run() {
        Sshd service = service
        if (!ufw.active) {
            log.debug 'No Ufw available.'
            return
        }
        updateFirewall()
    }

    def updateFirewall() {
        Sshd service = service
        if (service.binding.port) {
            ufw.allowPortsToAny([service.binding.port], this)
        }
    }

    @Override
    def getLog() {
        log
    }
}
