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
package com.anrisoftware.sscontrol.utils.centos.external

/**
 * CentOS test utilities.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
class CentosTestUtils {

    static final URL yumCommand = CentosTestUtils.class.getResource('/com/anrisoftware/sscontrol/utils/centos/external/tests/yum_cmd.txt')

    static final URL catCommand = CentosTestUtils.class.getResource('/com/anrisoftware/sscontrol/utils/centos/external/tests/centos_7_cat_cmd.txt')
}