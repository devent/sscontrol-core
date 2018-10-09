package com.anrisoftware.sscontrol.k8scluster.service.external;

import java.util.Map;

import com.anrisoftware.sscontrol.types.cluster.external.Credentials;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
public interface CredentialsFactory {

    Credentials create(@Assisted Map<String, Object> args);

}
