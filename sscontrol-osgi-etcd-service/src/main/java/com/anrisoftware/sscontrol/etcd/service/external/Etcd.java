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
package com.anrisoftware.sscontrol.etcd.service.external;

import java.util.List;

import com.anrisoftware.sscontrol.tls.external.Tls;
import com.anrisoftware.sscontrol.types.host.external.HostService;
import com.anrisoftware.sscontrol.types.misc.external.DebugLogging;
import com.anrisoftware.sscontrol.types.ssh.external.SshHost;

/**
 * <i>Etcd</i> service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface Etcd extends HostService {

    String getMemberName();

    DebugLogging getDebugLogging();

    List<Binding> getBindings();

    List<Binding> getAdvertises();

    Tls getTls();

    List<Authentication> getAuthentications();

    Peer getPeer();

    Client getClient();

    Proxy getProxy();

    Gateway getGateway();

    SshHost getCheckOn();
}
