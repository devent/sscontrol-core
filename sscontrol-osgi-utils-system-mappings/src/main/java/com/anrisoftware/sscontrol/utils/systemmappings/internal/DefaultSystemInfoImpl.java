/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
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
package com.anrisoftware.sscontrol.utils.systemmappings.internal;

import static java.lang.String.format;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.generator.qdox.parser.ParseException;

import com.anrisoftware.sscontrol.types.host.external.SystemInfo;
import com.anrisoftware.sscontrol.utils.systemmappings.external.AbstractSystemInfo;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
public class DefaultSystemInfoImpl extends AbstractSystemInfo {

    @AssistedInject
    DefaultSystemInfoImpl(SystemNameMappingsProperties mappingsProperties,
            @Assisted String string) {
        super(parseSystemInfo(mappingsProperties, string));
    }

    private static SystemInfo parseSystemInfo(
            SystemNameMappingsProperties mappingsProperties, String string) {
        String[] split = StringUtils.split(string, "/");
        final String system;
        final String name;
        final String version;
        if (split.length == 4) {
            system = split[1];
            name = split[2];
            version = split[3];
        } else if (split.length == 3) {
            name = split[1];
            version = split[2];
            system = mappingsProperties.getMapping(name);
        } else if (split.length == 2) {
            name = split[0];
            version = split[1];
            system = mappingsProperties.getMapping(name);
        } else {
            throw new ParseException(
                    format("Expected system/name/version, got '%s'", string), 0,
                    0);
        }
        return new SystemInfo() {

            @Override
            public String getVersion() {
                return version;
            }

            @Override
            public String getSystem() {
                return system;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    @AssistedInject
    DefaultSystemInfoImpl(SystemNameMappingsProperties mappingsProperties,
            @Assisted Map<String, Object> args) {
        super(parseArgs(mappingsProperties, args));
    }

    private static SystemInfo parseArgs(
            SystemNameMappingsProperties mappingsProperties,
            Map<String, Object> args) {
        final String name = parseName(args);
        final String system = parseSystem(mappingsProperties, name, args);
        final String version = parseVersion(args);
        return new SystemInfo() {

            @Override
            public String getVersion() {
                return version;
            }

            @Override
            public String getSystem() {
                return system;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    private static String parseVersion(Map<String, Object> args) {
        Object v = args.get("version");
        if (v != null) {
            return v.toString();
        } else {
            return null;
        }
    }

    private static String parseSystem(
            SystemNameMappingsProperties mappingsProperties, String name,
            Map<String, Object> args) {
        Object v = args.get("system");
        if (v != null) {
            return v.toString();
        } else {
            if (name != null) {
                return mappingsProperties.getMapping(name);
            } else {
                return null;
            }
        }
    }

    private static String parseName(Map<String, Object> args) {
        Object v = args.get("name");
        if (v != null) {
            return v.toString();
        } else {
            return null;
        }
    }

}