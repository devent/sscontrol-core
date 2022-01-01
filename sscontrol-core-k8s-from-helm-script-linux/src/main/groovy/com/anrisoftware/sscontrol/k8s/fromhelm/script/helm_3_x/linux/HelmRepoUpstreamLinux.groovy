package com.anrisoftware.sscontrol.k8s.fromhelm.script.helm_3_x.linux;

import javax.inject.Inject;

import com.anrisoftware.sscontrol.repo.helm.script.linux.Helm_3_RepoUpstreamLinux;

/**
 * Installs <i>Helm 3</i> from the upstream source.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
class HelmRepoUpstreamLinux extends Helm_3_RepoUpstreamLinux {

    @Inject
    FromHelmLinuxProperties linuxPropertiesProvider

    @Override
    Properties getDefaultProperties() {
        linuxPropertiesProvider.get()
    }

    @Override
    def run() {
    }
}
