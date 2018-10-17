/*-
 * #%L
 * sscontrol-osgi - collectd-script-debian
 * %%
 * Copyright (C) 2016 - 2018 Advanced Natural Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.anrisoftware.sscontrol.collectd.script.debian.internal.debian_9.internal

import static com.anrisoftware.globalpom.utils.TestUtils.*
import static com.anrisoftware.sscontrol.shell.external.utils.UnixTestUtil.*
import static com.anrisoftware.sscontrol.shell.external.utils.RobobeeSocketCondition.*
import static org.junit.jupiter.api.Assumptions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import com.anrisoftware.sscontrol.shell.external.utils.RobobeeSocketCondition

import groovy.util.logging.Slf4j

/**
 *
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
@Slf4j
@ExtendWith(RobobeeSocketCondition.class)
class CollectdServerTest extends AbstractCollectdRunnerTest {

    @Test
    void "collectd_server"() {
	def test = [
	    name: "collectd_server",
	    script: '''
service "ssh", host: "robobee@robobee-test", socket: robobeeSocket
service "collectd", version: "5.7" with {
    config name: "99-write-graphite", script: """
LoadPlugin "write_graphite"
<Plugin "write_graphite">
<Node "graphite">
  Host "graphite"
  Port "2003"
  Prefix "collectd."
  #Postfix ""
  Protocol "tcp"
  LogSendErrors true
  EscapeCharacter "_"
  SeparateInstances true
  StoreRates true
  AlwaysAppendDS false
</Node>
</Plugin>
"""
    config name: "99-write-influxdb", script: """
LoadPlugin "network"
<Plugin "network">
  Server "myinfluxdb.com" "25826"
</Plugin>
"""
}
''',
	    scriptVars: [robobeeSocket: robobeeSocket],
	    expectedServicesSize: 2,
	    generatedDir: folder.newFolder(),
	    expected: { Map args ->
		assertStringResource CollectdServerTest, readRemoteFile('/etc/collectd/collectd.conf.d/99-write-graphite.conf'), "${args.test.name}_write_graphite_conf_expected.txt"
		assertStringResource CollectdServerTest, readRemoteFile('/etc/collectd/collectd.conf.d/99-write-influxdb.conf'), "${args.test.name}_write_influxdb_conf_expected.txt"
	    },
	]
	doTest test
    }

    @BeforeEach
    void beforeMethod() {
	checkRobobeeSocket()
    }

    @Override
    void createDummyCommands(File dir) {
    }

    @Override
    Map getScriptEnv(Map args) {
	getEmptyScriptEnv args
    }
}
