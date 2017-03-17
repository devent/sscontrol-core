package com.anrisoftware.sscontrol.k8smaster.internal;

import static com.anrisoftware.sscontrol.k8smaster.internal.FlannelPluginImplLogger.m.rangeSet;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;

/**
 * Logging for {@link FlannelPluginImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class FlannelPluginImplLogger extends AbstractLogger {

    enum m {

        rangeSet("Range {} set for {}");

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
     * Sets the context of the logger to {@link FlannelPluginImpl}.
     */
    public FlannelPluginImplLogger() {
        super(FlannelPluginImpl.class);
    }

    void rangeSet(FlannelPluginImpl flannel, String range) {
        debug(rangeSet, range, flannel);
    }
}
