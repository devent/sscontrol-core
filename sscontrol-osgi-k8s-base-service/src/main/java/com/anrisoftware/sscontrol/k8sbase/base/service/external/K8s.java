package com.anrisoftware.sscontrol.k8sbase.base.service.external;

import java.util.List;
import java.util.Map;

import com.anrisoftware.sscontrol.tls.external.Tls;
import com.anrisoftware.sscontrol.types.cluster.external.ClusterService;
import com.anrisoftware.sscontrol.types.host.external.TargetHost;
import com.anrisoftware.sscontrol.types.misc.external.DebugLogging;

/**
 * <i>K8s</i> service.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public interface K8s extends ClusterService {

    DebugLogging getDebugLogging();

    /**
     * Returns the plugins to use. A plugin can be the etcd cluster or the pod
     * network plugin.
     */
    Map<String, Plugin> getPlugins();

    /**
     * Returns information about the Kubernetes cluster.
     */
    Cluster getCluster();

    Boolean isAllowPrivileged();

    Kubelet getKubelet();

    /**
     * Returns the api server TLS certificates.
     */
    Tls getTls();

    /**
     * Returns the container runtime: docker or rkt.
     */
    String getContainerRuntime();

    /**
     * Returns labels for the node.
     */
    Map<String, Label> getLabels();

    /**
     * Returns taints for the node.
     */
    Map<String, Taint> getTaints();

    List<String> getProperty();

    void target(Map<String, Object> args);

    void debug(Map<String, Object> args, String name);

    void debug(Map<String, Object> args);

    List<Object> getDebug();

    void cluster(Map<String, Object> args);

    Plugin plugin(String name);

    Plugin plugin(Map<String, Object> args, String name);

    Plugin plugin(Map<String, Object> args);

    /**
     * Set if allow containers to request privileged mode.
     *
     * <pre>
     * privileged true
     * </pre>
     */
    void privileged(boolean allow);

    /**
     * Sets the api server TLS certificates.
     *
     * <pre>
     * tls ca: "ca.pem", cert: "cert.pem", key: "key.pem"
     * </pre>
     */
    void tls(Map<String, Object> args);

    void addTargets(List<TargetHost> list);

    void setContainerRuntime(String runtime);

    /**
     * Set kubelet properties.
     *
     * <pre>
     * kubelet port: 10250
     * </pre>
     */
    Kubelet kubelet(Map<String, Object> args);

    /**
     * Adds node label.
     *
     * <pre>
     * label key: "muellerpublic.de/some", value: "foo"
     * </pre>
     */
    void label(Map<String, Object> args);

    /**
     * Adds node label.
     *
     * <pre>
     * label << 'name=value'
     * </pre>
     */
    List<String> getLabel();

    /**
     * Adds node taint.
     *
     * <pre>
     * taint key: "extra", value: "foo", effect: "aaa"
     * </pre>
     */
    void taint(Map<String, Object> args);

    /**
     * Adds node taint.
     *
     * <pre>
     * taint << "dedicated=mail:NoSchedule"
     * </pre>
     */
    List<String> getTaint();

}
