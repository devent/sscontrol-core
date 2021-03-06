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
package com.anrisoftware.sscontrol.haproxy.script.haproxy_2_x;

import static com.google.inject.multibindings.MapBinder.newMapBinder;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 *
 *
 * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
 * @version 1.0
 */
public class HAProxy_2_x_Module extends AbstractModule {

    @Override
    protected void configure() {
        bindApplyProxyDefaults();
    }

    private void bindApplyProxyDefaults() {
        MapBinder<String, ApplyProxyDefaults> map = newMapBinder(binder(), String.class, ApplyProxyDefaults.class);
        map.addBinding("http").to(HttpApplyProxyDefaults.class);
        map.addBinding("https").to(HttpsApplyProxyDefaults.class);
        map.addBinding("ssh").to(SshApplyProxyDefaults.class);
    }

}
