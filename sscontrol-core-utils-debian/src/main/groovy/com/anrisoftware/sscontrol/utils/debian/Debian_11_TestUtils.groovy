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
package com.anrisoftware.sscontrol.utils.debian

/**
 * Debian 11 test utilities.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
class Debian_11_TestUtils {

    static final URL catCommand = Debian_11_TestUtils.class.getResource('/com/anrisoftware/sscontrol/utils/debian/tests/debian_11_cat_cmd.txt')

    static final URL grepCommand = Debian_11_TestUtils.class.getResource('/com/anrisoftware/sscontrol/utils/debian/tests/debian_11_grep_cmd.txt')

    static final URL whichufwnotfoundCommand = Debian_11_TestUtils.class.getResource('/com/anrisoftware/sscontrol/utils/debian/tests/debian_11_which_ufw_not_found_cmd.txt')

    static final URL ufwActiveCommand = Debian_11_TestUtils.class.getResource('/com/anrisoftware/sscontrol/utils/debian/tests/debian_11_ufw_active_cmd.txt')
}
