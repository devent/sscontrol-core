package com.anrisoftware.sscontrol.k8s.backup.service.internal;

import static com.google.inject.Guice.createInjector;

import java.util.Map;

import javax.inject.Inject;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.anrisoftware.globalpom.core.strings.ToStringService;
import com.anrisoftware.sscontrol.k8s.backup.service.internal.BackupImpl.BackupImplFactory;
import com.anrisoftware.sscontrol.k8sbase.base.service.external.K8sService;
import com.anrisoftware.sscontrol.types.host.external.HostService;
import com.anrisoftware.sscontrol.types.host.external.HostServiceService;

/**
 * Backup service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Component
@Service(HostServiceService.class)
public class BackupServiceImpl implements K8sService {

    @Inject
    private BackupImplFactory sshFactory;

    @Reference
    private ToStringService toStringService;

    @Override
    public String getName() {
        return "backup";
    }

    @Override
    public HostService create(Map<String, Object> args) {
        return sshFactory.create(args);
    }

    @Activate
    protected void start() {
        createInjector(new BackupModule()).injectMembers(this);
    }
}
