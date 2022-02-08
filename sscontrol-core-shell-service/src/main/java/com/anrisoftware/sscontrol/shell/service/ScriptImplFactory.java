package com.anrisoftware.sscontrol.shell.service;

import java.util.Map;

/**
 *
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public interface ScriptImplFactory {

    Script create(Map<String, Object> args);
}
