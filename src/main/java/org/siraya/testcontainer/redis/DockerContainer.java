
package org.siraya.testcontainer.redis;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

public class DockerContainer extends GenericContainer<DockerContainer> {

    public DockerContainer() {
        super("docker");
        withFileSystemBind("/var/run/docker.sock", "/var/run/docker.sock");
        withClasspathResourceMapping("docker_cmd.sh", "/docker_cmd.sh", BindMode.READ_ONLY);
        withCommand("/bin/sh", "/docker_cmd.sh");
    }

}
