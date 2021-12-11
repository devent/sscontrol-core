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
package com.anrisoftware.sscontrol.crio.script.debian

import org.stringtemplate.v4.ST

import com.anrisoftware.sscontrol.crio.service.Crio
import com.anrisoftware.sscontrol.groovy.script.ScriptBase
import com.anrisoftware.sscontrol.utils.debian.DebianUtils

import groovy.util.logging.Slf4j

/**
 * CRI-O from upstream.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Upstream_Crio_Debian extends ScriptBase {

    /**
     * Installs cri-o from the upstream repository.
     */
    def installCrio() {
        debian.checkPackages() ? { }() : {
            debian.addPackagesRepositoryAlternative(key: libcontainersRepositoryKey, url: libcontainersPepositoryUrl, file: libcontainersRepositoryListFile)
            debian.addPackagesRepositoryAlternative(key: crioRepositoryKey, url: crioPepositoryUrl, file: crioRepositoryListFile)
            debian.installPackages()
        }()
    }

    /**
     *
     * @return DebianUtils
     */
    abstract DebianUtils getDebian()

    /**
     * For example <code>/etc/apt/sources.list.d/libcontainers.list</code>
     * <ul>
     * <li><code>libcontainers_repository_list_file</code>
     * </ul>
     */
    File getLibcontainersRepositoryListFile() {
        getScriptFileProperty 'libcontainers_repository_list_file'
    }

    /**
     * For example <code>https://download.opensuse.org/repositories/devel:/kubic:/libcontainers:/stable/Debian_11/</code>
     * <ul>
     * <li><code>libcontainers_pepository_url</code>
     * </ul>
     */
    String getLibcontainersPepositoryUrl() {
        getScriptProperty 'libcontainers_pepository_url'
    }

    /**
     * For example <code>https://download.opensuse.org/repositories/devel:/kubic:/libcontainers:/stable/Debian_11/Release.key</code>
     * <ul>
     * <li><code>libcontainers_repository_key</code>
     * </ul>
     */
    String getLibcontainersRepositoryKey() {
        getScriptProperty 'libcontainers_repository_key'
    }

    /**
     * For example <code>/etc/apt/sources.list.d/cri-o.list</code>
     * <ul>
     * <li><code>crio_repository_list_file</code>
     * </ul>
     */
    File getCrioRepositoryListFile() {
        getScriptFileProperty 'crio_repository_list_file'
    }

    /**
     * For example <code>https://download.opensuse.org/repositories/devel:/kubic:/libcontainers:/stable:/cri-o:/<majorVersion>:/<fullVersion>/Debian_11/</code>
     * <ul>
     * <li><code>crio_pepository_url</code>
     * </ul>
     */
    String getCrioPepositoryUrl() {
        def service = (Crio) service
        new ST(getScriptProperty('crio_pepository_url')).add('majorVersion', service.version).add('fullVersion', crioFullVersion).render()
    }

    /**
     * For example <code>https://download.opensuse.org/repositories/devel:kubic:libcontainers:stable:cri-o:<majorVersion>/Debian_11/Release.key</code>
     * <ul>
     * <li><code>crio_repository_key</code>
     * </ul>
     */
    String getCrioRepositoryKey() {
        def service = (Crio) service
        new ST(getScriptProperty('crio_repository_key')).add('majorVersion', service.version).add('fullVersion', crioFullVersion).render()
    }

    /**
     * Returns the full version of cri-o that corresponds to the cri-o major version, for example {@code 1.22.1}
     */
    abstract String getCrioFullVersion()

    @Override
    def getLog() {
        log
    }
}
