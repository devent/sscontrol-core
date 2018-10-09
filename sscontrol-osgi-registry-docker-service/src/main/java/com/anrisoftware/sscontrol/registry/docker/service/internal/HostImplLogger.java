package com.anrisoftware.sscontrol.registry.docker.service.internal;

import static com.anrisoftware.sscontrol.registry.docker.service.internal.HostImplLogger.m.addressSet;

import java.net.URI;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;

/**
 * Logging for {@link HostImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class HostImplLogger extends AbstractLogger {

    enum m {

        addressSet("Address {} set for {}");

        private String name;

        private m(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Sets the context of the logger to {@link HostImpl}.
     */
    public HostImplLogger() {
        super(HostImpl.class);
    }

    void addressSet(HostImpl remote, URI uri) {
        debug(addressSet, uri, remote);
    }
}
