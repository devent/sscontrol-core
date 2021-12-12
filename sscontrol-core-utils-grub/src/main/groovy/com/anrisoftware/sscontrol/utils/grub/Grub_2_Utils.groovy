/**
 * Copyright © 2020 Erwin Müller (erwin.mueller@anrisoftware.com)
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
package com.anrisoftware.sscontrol.utils.grub

import javax.inject.Inject

import com.anrisoftware.sscontrol.types.host.HostServiceScript
import com.google.inject.assistedinject.Assisted

/**
 * Grub 2 utilities.
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
class Grub_2_Utils extends GrubUtils {

    @Inject
    Grub_2_Properties propertiesProvider

    @Inject
    Grub_2_Utils(@Assisted HostServiceScript script) {
        super(script)
    }

    @Override
    public Properties getDefaultProperties() {
        propertiesProvider.get()
    }

    /**
     * Attaches the commands to the default Linux command line.
     */
    @Override
    def attachGrubCommandLine(List commands) {
        def cmds = commands.join(" ")
        def file = defaultGrubFile
        script.shell privileged: true, target: script.target, """
if ! grep "$cmds" $file; then
sed -Ei 's/^GRUB_CMDLINE_LINUX_DEFAULT="(.*)"/GRUB_CMDLINE_LINUX_DEFAULT="\\1 $cmds"/' $file
fi
""" call()
    }

    /**
     * Returns the default grub configuration file, for example {@code /etc/default/grub}.
     *
     * <ul>
     * <li>profile property {@code grub_default_file}</li>
     * </ul>
     *
     * @see #getDefaultProperties()
     */
    File getDefaultGrubFile() {
        script.getFileProperty "grub_default_file", script.base, defaultProperties
    }
}
