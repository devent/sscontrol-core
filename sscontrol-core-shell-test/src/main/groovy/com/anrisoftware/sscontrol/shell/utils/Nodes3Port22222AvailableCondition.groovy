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
package com.anrisoftware.sscontrol.shell.utils;

/**
 * Checks that the sockets to the cluster nodes are available for tests. Nodes:
 * <ul>
 * <li>node-0.robobee-test.test:22
 * <li>node-1.robobee-test.test:22222
 * <li>node-2.robobee-test.test:22222
 * </ul>
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
class Nodes3Port22222AvailableCondition extends AbstractSocketsCondition {

    static final Map nodesSockets = [
        controls: [
            "/tmp/robobee@robobee-3-test:22"
        ],
        workers: [
            "/tmp/robobee@robobee-4-test:22222",
            //"/tmp/robobee@robobee-5-test:22222",
        ]
    ]

    Nodes3Port22222AvailableCondition() {
        super(nodesSockets.values() as List)
    }
}
