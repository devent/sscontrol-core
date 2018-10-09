package com.anrisoftware.sscontrol.k8smaster.service.external;

import com.anrisoftware.sscontrol.tls.external.Tls;

/**
 * ServiceAccount.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface Account {

    Tls getTls();
}
