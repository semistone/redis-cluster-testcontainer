
package org.siraya.testcontainer.redis;

import static org.testcontainers.containers.output.OutputFrame.OutputType.STDOUT;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.WaitingConsumer;

@Slf4j
public class RedisClusterContainer extends GenericContainer<RedisClusterContainer> {

    private static final int DEFAULT_SIZE = 6;
    private int clusterSize;
    protected RedisContainer[] redisArray = new RedisContainer[clusterSize];

    @Getter
    private List<String> redisNodes = new ArrayList<>();

    public RedisClusterContainer() {
        this(DEFAULT_SIZE);
    }

    public RedisClusterContainer(int clusterSize) {
        super("semistone/redisclustercompose:discover");
        this.clusterSize = clusterSize;
        log.info("prepare discover");
        withFileSystemBind("/var/run/docker.sock", "/var/run/docker.sock");
        withNetworkMode("host");
        log.info("discover started");
    }

    @Override
    protected void doStart() {
        super.doStart();
        redisArray = new RedisContainer[clusterSize];
        for (int i = 0; i < clusterSize; i++) {
            redisArray[i] = new RedisContainer()
                    .withNetwork(getNetwork());
            redisArray[i].start();
            redisNodes.add(redisArray[i].getRedisAddress());

        }
        DockerContainer docker = new DockerContainer()
                .withNetwork(getNetwork());
        docker.start();
        WaitingConsumer consumer = new WaitingConsumer();
        docker.followOutput(consumer, STDOUT);

        try {
            consumer.waitUntil(frame -> frame.getUtf8String().contains("172"), 30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        String cluster = docker.getLogs();
        log.info("cluster is " + cluster);
        try {
            redisArray[0].execInContainer("/bin/sh", "-c",
                    "echo 'yes' |redis-cli --cluster create  " + cluster + " --cluster-replicas 1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        ArrayList<Runnable> runnableStop = new ArrayList<>();
        runnableStop.add(super::stop);
        for (int i = 0; i < redisArray.length; i++) {
            runnableStop.add(redisArray[i]::stop);
        }
        runnableStop.stream().parallel().forEach(Runnable::run);
    }
}
