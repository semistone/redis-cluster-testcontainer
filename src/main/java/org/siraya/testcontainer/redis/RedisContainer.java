
package org.siraya.testcontainer.redis;

import org.testcontainers.containers.GenericContainer;

public class RedisContainer extends GenericContainer<RedisContainer> {

    public RedisContainer() {
        super("semistone/redisclustercompose:redis");
            withCommand("--cluster-enabled yes --bind 0.0.0.0 --loglevel warning");
            withExposedPorts(6379, 16379);
            withLabel("redis", "");
    }

    public String getRedisAddress() {
        return "redis://" + getContainerIpAddress() + ":" + getFirstMappedPort();
    }
}
