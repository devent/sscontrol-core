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
package com.anrisoftware.sscontrol.nfs.script.nfs_1_3

import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.nfs.service.Export
import com.anrisoftware.sscontrol.nfs.service.Host
import com.anrisoftware.sscontrol.nfs.service.Nfs

import groovy.util.logging.Slf4j

/**
 * Nfs 1.3 firewalld.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Nfs_1_3_Firewalld extends ScriptBase {

    /**
     * Configures firewalld to allow connections from Nfs hosts.
     */
    def configureFilewalld() {
        Nfs service = this.service
            shell privileged: true, """
if ! firewall-cmd --get-zones | grep nfs; then
    firewall-cmd --permanent --new-zone=nfs
fi
firewall-cmd --permanent --zone=nfs --add-service=mountd
firewall-cmd --permanent --zone=nfs --add-service=rpc-bind
firewall-cmd --permanent --zone=nfs --add-service=nfs
""" call()
        service.exports.each { Export export ->
            export.hosts.each { Host host ->
            shell privileged: true, """
firewall-cmd --permanent --zone=nfs --add-source=${InetAddress.getByName(host.name).hostAddress}
""" call()
            }
        }
            shell privileged: true, """
firewall-cmd --reload
""" call()
    }

    @Override
    def getLog() {
        log
    }
}
