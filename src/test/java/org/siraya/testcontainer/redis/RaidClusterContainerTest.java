package org.siraya.testcontainer.redis;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

@Testcontainers
class RaidClusterContainerTest {

    @Container
    private static RedisClusterContainer raidClusterContainer = new RedisClusterContainer();

    @Test
    public void testUp() {
        List<String> redisNodes = raidClusterContainer.getRedisNodes();
        Arrays.asList(redisNodes).stream().forEach(node -> System.out.println(node));
    }
}