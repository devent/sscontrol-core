/*
 * Copyright 2016 Erwin Müller <erwin.mueller@deventm.org>
 *
 * This file is part of sscontrol-osgi-shell-openssh.
 *
 * sscontrol-osgi-shell-openssh is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * sscontrol-osgi-shell-openssh is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with sscontrol-osgi-shell-openssh. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.sscontrol.shell.internal.facts;

import com.anrisoftware.sscontrol.facts.external.Facts;
import com.anrisoftware.sscontrol.facts.external.Facts.FactsFactory;
import com.anrisoftware.sscontrol.shell.internal.facts.CatReleaseParse.CatReleaseParseFactory;
import com.anrisoftware.sscontrol.shell.internal.facts.DefaultHostSystem.DefaultHostSystemFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public class FactsModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Facts.class, FactsImpl.class)
                .build(FactsFactory.class));
        install(new FactoryModuleBuilder()
                .implement(CatReleaseParse.class, CatReleaseParse.class)
                .build(CatReleaseParseFactory.class));
        install(new FactoryModuleBuilder()
                .implement(DefaultHostSystem.class, DefaultHostSystem.class)
                .build(DefaultHostSystemFactory.class));
    }

}
