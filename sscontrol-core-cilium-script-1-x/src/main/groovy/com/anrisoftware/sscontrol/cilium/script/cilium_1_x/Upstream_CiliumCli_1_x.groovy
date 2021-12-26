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
package com.anrisoftware.sscontrol.cilium.script.cilium_1_x

import org.stringtemplate.v4.ST

import com.anrisoftware.sscontrol.groovy.script.ScriptBase

import groovy.util.logging.Slf4j

/**
 * Installs <i>Cilium-CLI 1.x</i> service from the upstream source.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Upstream_CiliumCli_1_x extends ScriptBase {

    /**
     * Installs cilium-cli from the upstream source.
     */
    def installCiliumCli() {
        withRemoteTempDir {
            shell chdir: it, privileged: false, """
curl -L -O ${upstreamSourceArchive}
curl -L -O ${upstreamSourceShasum}
sha256sum --check cilium-linux-amd64.tar.gz.sha256sum
sudo tar xzvfC cilium-linux-amd64.tar.gz /usr/local/bin
""" call()
        }
    }

    /**
     * Returns the upstream source URL, for example {@code "https://github.com/cilium/cilium-cli/releases/download/<version>/cilium-linux-amd64.tar.gz"}.
     * <ul>
     * <li>property {@code "cilium_cli_upstream_source_archive"}
     * <li>variable {@code "<version>"} is replaced by the Cilium version.
     * </ul>
     */
    String getUpstreamSourceArchive() {
        def p = getScriptProperty "cilium_cli_upstream_source_archive"
        new ST(p).add("version", version).render()
    }

    /**
     * Returns the upstream source shasum URL, for example {@code "https://github.com/cilium/cilium-cli/releases/download/<version>/cilium-linux-amd64.tar.gz.sha256sum"}.
     * <ul>
     * <li>property {@code "cilium_cli_upstream_source_shasum"}
     * <li>variable {@code "<version>"} is replaced by the Cilium version.
     * </ul>
     */
    String getUpstreamSourceShasum() {
        def p = getScriptProperty "cilium_cli_upstream_source_shasum"
        new ST(p).add("version", version).render()
    }

    /**
     * Returns the upstream Cilium-CLI version, for example {@code "v0.10.0"}.
     * <p>
     * Property {@code "cilium_cli_version"}
     */
    String getVersion() {
        getScriptProperty "cilium_cli_version"
    }

    @Override
    def getLog() {
        log
    }
}
