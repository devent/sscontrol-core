package com.anrisoftware.sscontrol.docker.service.internal;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.anrisoftware.sscontrol.docker.service.external.DockerPreScriptService;
import com.anrisoftware.sscontrol.docker.service.internal.DockerPreScriptImpl.DockerPreScriptImplFactory;
import com.anrisoftware.sscontrol.types.host.external.PreHost;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * <i>Docker</i> pre-script service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Component(service = DockerPreScriptService.class)
public class DockerPreScriptServiceImpl implements DockerPreScriptService {

    @Inject
    private DockerPreScriptImplFactory preScriptFactory;

    @Override
    public PreHost create() {
        return preScriptFactory.create();
    }

    @Activate
    protected void start() {
        Guice.createInjector(new DockerPreModule(), new AbstractModule() {

            @Override
            protected void configure() {
            }
        }).injectMembers(this);
    }
}
