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
package com.anrisoftware.sscontrol.k8s.fromhelm.service;

import static com.anrisoftware.sscontrol.types.host.StringUtils.indentString
import static com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.stringListStatement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.anrisoftware.resources.templates.external.TemplateResource
import com.anrisoftware.resources.templates.external.TemplatesFactory
import com.anrisoftware.sscontrol.k8s.fromhelm.service.ReleaseImpl.ReleaseImplFactory;
import com.anrisoftware.sscontrol.types.host.HostServiceProperties;
import com.anrisoftware.sscontrol.types.host.HostServicePropertiesService;
import com.anrisoftware.sscontrol.types.host.TargetHost;
import com.anrisoftware.sscontrol.types.misc.StringListPropertyUtil.ListProperty;
import com.anrisoftware.sscontrol.types.repo.external.RepoHost;
import com.google.inject.assistedinject.Assisted;

import groovy.util.logging.Slf4j

/**
 * From Helm service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@Slf4j
class FromHelmImpl implements FromHelm {

    HostServiceProperties serviceProperties;

    private final List<TargetHost> targets;

    private final List<RepoHost> repos;

    Object configYaml;

    boolean dryrun;

    boolean debug;

    boolean wait;

    String chart;

    String version;

    Release release;

    private transient ReleaseImplFactory releaseFactory;

    private TemplateResource configPartsTemplate

    @Inject
    FromHelmImpl(HostServicePropertiesService propertiesService, ReleaseImplFactory releaseFactory, @Assisted Map<String, Object> args) {
        this.serviceProperties = propertiesService.create();
        this.targets = new ArrayList<>();
        this.repos = new ArrayList<>();
        this.dryrun = false;
        this.releaseFactory = releaseFactory;
        this.release = releaseFactory.create();
        parseArgs(args);
    }

    @Inject
    void loadTemplates(TemplatesFactory templatesFactory) {
        def templates = templatesFactory.create('FromHelmImpl_Templates')
        this.configPartsTemplate = templates.getResource('config_parts')
    }

    /**
     * <pre>
     * property << 'name=value'
     * </pre>
     */
    public List<String> getProperty() {
        return stringListStatement(new ListProperty() {

                    @Override
                    public void add(String property) {
                        serviceProperties.addProperty(property);
                    }
                });
    }

    @Override
    public TargetHost getTarget() {
        return getTargets().get(0);
    }

    public void addTargets(List<TargetHost> list) {
        this.targets.addAll(list);
    }

    @Override
    public List<TargetHost> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    @Override
    public RepoHost getRepo() {
        List<RepoHost> repos = getRepos();
        if (repos.isEmpty()) {
            return null;
        }
        return repos.get(0);
    }

    @Override
    public void addRepos(List<RepoHost> list) {
        this.repos.addAll(list);
        log.debug("Repos added: {}", list);
    }

    @Override
    public List<RepoHost> getRepos() {
        return Collections.unmodifiableList(repos);
    }

    @Override
    public boolean getUseRepo() {
        return !getRepos().isEmpty();
    }

    public void setServiceProperties(HostServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    @Override
    public String getName() {
        return "from-helm";
    }

    /**
     * <pre>
     * config << "{mariadbUser: user0, mariadbDatabase: user0db}"
     * </pre>
     */
    public List<String> getConfig() {
        return stringListStatement(new ListProperty() {

                    @Override
                    public void add(String text) {
                        parseYaml(text);
                    }
                });
    }

    /**
     * <pre>
     * release ns: "helm-test", name: "wordpress"
     * </pre>
     */
    public void release(Map<String, Object> args) {
        Release release = releaseFactory.create(this.release, args);
        setRelease(release);
    }

    /**
     * <pre>
     * dryrun true
     * </pre>
     */
    public void dryrun(boolean dryrun) {
        setDryrun(dryrun);
    }

    /**
     * <pre>
     * debug true
     * </pre>
     */
    public void debug(boolean debug) {
        setDebug(debug);
    }

    /**
     * <pre>
     * wait true
     * </pre>
     */
    public void wait(boolean wait) {
        setWait(wait);
    }

    def insertPodAffinityTerm(int n, def key, def value, def topologyKey = null) {
        indentString configPartsTemplate.getText(true, "podAffinityTerm", "vars", [key: key, value: value, topologyKey: topologyKey]), n
    }

    def insertWeightedPodAffinityTerm(int n, def key, def value, def weight = 100, def topologyKey = null) {
        indentString configPartsTemplate.getText(true, "weightedPodAffinityTerm", "vars", [key: key, value: value, weight: weight, topologyKey: topologyKey]), n
    }

    def insertNodeSelector(int n, def key, def value) {
        indentString configPartsTemplate.getText(true, "nodeSelector", "vars", [key: key, value: value]), n
    }

    def insertPreferredSchedulingTerm(int n, def key, def value, def weight = 100) {
        indentString configPartsTemplate.getText(true, "preferredSchedulingTerm", "vars", [key: key, value: value, weight: weight]), n
    }

    def insertTolerationControlPane(int n) {
        indentString configPartsTemplate.getText(true, "tolerationControlPane", "vars", [:]), n
    }

    def insertToleration(int n, def key, def value = null, def effect = "NoSchedule", def operator = "Equal") {
        indentString configPartsTemplate.getText(true, "tolerationTerm", "vars", [effect: effect, value: value, key: key, operator: operator]), n
    }

    def insertResources(int n, def limits, def requests = null) {
        if (!requests) {
            requests = limits
        }
        indentString configPartsTemplate.getText(true, "resources", "vars", [limits: limits, requests: requests]), n
    }

    def insertIndent(int n, def string) {
        indentString string, n
    }

    def insertPersistentVolumeClaimSpec(int n, def requests, def className = null, List accessModes = null) {
        def vars = [:]
        vars.requests = requests
        vars.className = className
        vars.accessModes = accessModes ? accessModes : ["ReadWriteOnce"]
        indentString configPartsTemplate.getText(true, "persistentVolumeClaimSpec", "vars", vars), n
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append("targets", getTargets())
                .append("repos", getRepos()).toString();
    }

    private void parseArgs(Map<String, Object> args) {
        parseTargets(args);
        parseRepos(args);
        Object v = args.get("name");
        if (v != null) {
            Release release = releaseFactory.create(this.release, args);
            setRelease(release);
        }
        v = args.get("ns");
        if (v != null) {
            Release release = releaseFactory.create(this.release, args);
            setRelease(release);
        }
        v = args.get("version");
        if (v != null) {
            setVersion(v.toString());
        }
        v = args.get("chart");
        if (v != null) {
            setChart(v.toString());
        }
        v = args.get("dryrun");
        if (v != null) {
            setDryrun((boolean) v);
        }
        v = args.get("debug");
        if (v != null) {
            setDebug((boolean) v);
        }
        v = args.get("wait");
        if (v != null) {
            setWait((boolean) v);
        }
    }

    private void parseTargets(Map<String, Object> args) {
        Object v = args.get("targets");
        assertThat("targets=null", v, notNullValue());
        addTargets((List<TargetHost>) v);
    }

    private void parseRepos(Map<String, Object> args) {
        Object v = args.get("repos");
        assertThat("repos=null", v, notNullValue());
        addRepos((List<RepoHost>) v);
    }

    private void parseYaml(String text) {
        StringBuilder b = new StringBuilder();
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        if (configYaml != null) {
            String output = yaml.dump(configYaml);
            b.append(output);
        }
        b.append(text);
        this.configYaml = yaml.load(b.toString());
    }
}
