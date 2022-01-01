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
package com.anrisoftware.sscontrol.repo.helm.script.linux

import org.stringtemplate.v4.ST

import com.anrisoftware.sscontrol.groovy.script.ScriptBase

import groovy.util.logging.Slf4j

/**
 * Installs <i>Helm 3</i> from the upstream source.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Helm_3_RepoUpstreamLinux extends ScriptBase {

    /**
     * Downloads and installs Helm.
     */
    def installHelm() {
        log.info 'Installs Helm.'
        withRemoteTempDir {
            shell chdir: it, privileged: false, """
if ! helm version | grep ${version}; then
    curl -L -O ${upstreamSourceArchive}
    curl -L -O ${upstreamSourceShasum}
    sha256sum --check helm-${version}-linux-amd64.tar.gz.sha256sum
    tar xzvfC helm-${version}-linux-amd64.tar.gz .
    sudo mv linux-amd64/helm $binDir/helm
    sudo chmod o+rx $binDir/helm
fi
""" call()
        }
    }

    /**
     * Returns the upstream source URL, for example {@code "https://get.helm.sh/helm-<version>-linux-amd64.tar.gz"}.
     * <ul>
     * <li>property {@code "helm_upstream_source_archive"}
     * <li>variable {@code "<version>"} is replaced by the Helm version.
     * </ul>
     */
    String getUpstreamSourceArchive() {
        def p = getScriptProperty "helm_upstream_source_archive"
        new ST(p).add("version", version).render()
    }

    /**
     * Returns the upstream source shasum URL, for example {@code "https://get.helm.sh/helm-<version>-linux-amd64.tar.gz.sha256sum"}.
     * <ul>
     * <li>property {@code "helm_upstream_source_shasum"}
     * <li>variable {@code "<version>"} is replaced by the Helm version.
     * </ul>
     */
    String getUpstreamSourceShasum() {
        def p = getScriptProperty "helm_upstream_source_shasum"
        new ST(p).add("version", version).render()
    }

    /**
     * Returns the upstream Helm version, for example {@code "v3.7.2"}.
     * <p>
     * Property {@code "helm_version"}
     */
    String getVersion() {
        getScriptProperty "helm_version"
    }

    /**
     * Returns the bin directory, for example {@code "/usr/local/bin"}.
     * <p>
     * Property {@code "bin_dir"}
     */
    File getBinDir() {
        getScriptFileProperty "bin_dir"
    }

    @Override
    def getLog() {
        log
    }
}
