package com.anrisoftware.sscontrol.k8smaster.external;

import java.io.File;

/**
 * <i>Calico</i> plugin.
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
public interface CalicoPlugin extends Plugin {

    File getDir();
}
