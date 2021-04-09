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
package com.anrisoftware.sscontrol.fail2ban.script.fail2ban_0_10.external

import com.anrisoftware.sscontrol.fail2ban.script.fail2ban_0_x.external.Ufw_Fail2ban_0_x

import groovy.util.logging.Slf4j

/**
 * Configures the <i>Ufw</i> service for <i>Fail2ban 0.10</i>.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
abstract class Ufw_Fail2ban_0_10 extends Ufw_Fail2ban_0_x {

    @Override
    def getLog() {
        log
    }
}
