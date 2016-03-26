/*
 * Copyright 2016 Erwin Müller <erwin.mueller@deventm.org>
 *
 * This file is part of sscontrol-osgi-parser.
 *
 * sscontrol-osgi-parser is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * sscontrol-osgi-parser is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with sscontrol-osgi-parser. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.sscontrol.parser.internal;

import java.net.URI;

import com.anrisoftware.sscontrol.core.external.Service;
import com.anrisoftware.sscontrol.parser.external.ParserService;

/**
 * Internal implementation of our example OSGi service
 */
public class ParserServiceImpl implements ParserService {

    @Override
    public <T extends Service> T parse(URI resource) {
        // TODO Auto-generated method stub
        return null;
    }
}
