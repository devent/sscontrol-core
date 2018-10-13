package com.anrisoftware.sscontrol.k8s.glusterfsheketi.service.internal;

import static com.google.inject.Guice.createInjector;

import java.util.Map;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.anrisoftware.globalpom.core.strings.ToStringService;
import com.anrisoftware.sscontrol.k8s.glusterfsheketi.service.internal.GlusterfsHeketiImpl.GlusterfsHeketiImplFactory;
import com.anrisoftware.sscontrol.k8sbase.base.service.external.K8sService;
import com.anrisoftware.sscontrol.types.host.external.HostService;
import com.anrisoftware.sscontrol.types.host.external.HostServiceService;

/**
 * <i>glusterfs-heket</i> service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Component(service = HostServiceService.class)
public class GlusterfsHeketiServiceImpl implements K8sService {

    @Inject
    private GlusterfsHeketiImplFactory serviceFactory;

    @Reference
    private ToStringService toStringService;

    @Override
    public String getName() {
        return "glusterfs-heketi";
    }

    @Override
    public HostService create(Map<String, Object> args) {
        return serviceFactory.create(args);
    }

    @Activate
    protected void start() {
        createInjector(new GlusterfsHeketiModule()).injectMembers(this);
    }
}
