package com.anrisoftware.sscontrol.properties.internal;

/*-
 * #%L
 * sscontrol-osgi - services-properties
 * %%
 * Copyright (C) 2016 - 2018 Advanced Natural Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.anrisoftware.sscontrol.properties.internal.HostServicePropertiesImplLogger.m.propertyAdded;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.log.AbstractLogger;

/**
 * Logging for {@link HostServicePropertiesImpl}.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 1.0
 */
@Singleton
final class HostServicePropertiesImplLogger extends AbstractLogger {

    enum m {

        propertyAdded("Property {} = {} added to {}");

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
     * Sets the context of the logger to {@link HostServicePropertiesImpl}.
     */
    public HostServicePropertiesImplLogger() {
        super(HostServicePropertiesImpl.class);
    }

    void propertyAdded(HostServicePropertiesImpl properties, String name,
            Object value) {
        debug(propertyAdded, name, value, properties);
    }
}
