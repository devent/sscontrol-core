/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
 *
 * This file is part of sscontrol-osgi-command-shell-openssh.
 *
 * sscontrol-osgi-command-shell-openssh is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * sscontrol-osgi-command-shell-openssh is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with sscontrol-osgi-command-shell-openssh. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.sscontrol.command.shell.internal.ssh;

import static java.lang.String.format;

import java.util.Map;

import javax.inject.Inject;

import com.anrisoftware.globalpom.threads.external.core.Threads;
import com.anrisoftware.sscontrol.command.shell.external.ssh.AbstractSshRun;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
public class SshRun extends AbstractSshRun {

    /**
     * 
     *
     * @author Erwin Müller <erwin.mueller@deventm.de>
     * @version 1.0
     */
    public interface SshRunFactory {

        SshRun create(@Assisted Map<String, Object> args,
                @Assisted Object parent, @Assisted Threads threads,
                @Assisted String command);

    }

    @Inject
    SshRun(@Assisted Map<String, Object> args, @Assisted Object parent,
            @Assisted Threads threads, @Assisted String command) {
        super(args, parent, threads);
        argsMap.put(COMMAND_ARG, command);
    }

    @Override
    protected String getCmdTemplate() {
        String sshshell = getShellName(args);
        String template = format(COMMAND_NAME_FORMAT, "ssh_wrap_", sshshell);
        return template;
    }

    private static final String COMMAND_NAME_FORMAT = "%s%s";

}