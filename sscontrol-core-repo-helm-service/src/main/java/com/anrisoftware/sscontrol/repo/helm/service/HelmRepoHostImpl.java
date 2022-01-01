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
package com.anrisoftware.sscontrol.repo.helm.service;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.google.inject.assistedinject.Assisted;

import lombok.Data;

/**
 * <i>Helm</i> repository host.
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
@Data
public class HelmRepoHostImpl implements HelmRepoHost {

    private final HelmRepo repo;

    private String host;

    private String proto;

    private Integer port;

    /**
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface HelmRepoHostImplFactory {

        HelmRepoHost create(HelmRepo repo, TargetHost target);
    }

    @Inject
    HelmRepoHostImpl(@Assisted HelmRepo repo, @Assisted TargetHost target) {
        this.repo = repo;
        this.host = target.getHost();
        this.proto = target.getProto();
        this.port = target.getPort();
    }

    @Override
    public String getType() {
        return "helm";
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
