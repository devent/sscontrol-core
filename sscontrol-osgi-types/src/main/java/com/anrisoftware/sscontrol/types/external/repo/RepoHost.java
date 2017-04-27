package com.anrisoftware.sscontrol.types.external.repo;

import com.anrisoftware.sscontrol.types.external.ssh.TargetHost;

/**
 * Code repository target host.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface RepoHost extends TargetHost {

    /**
     * The type of the repository.
     */
    String getType();
}
