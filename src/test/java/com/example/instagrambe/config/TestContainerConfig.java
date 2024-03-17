package com.example.instagrambe.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerConfig implements BeforeAllCallback {

  private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
  private static final int REDIS_PORT = 6379;
  private GenericContainer redisContainer;

  @Override
  public void beforeAll(ExtensionContext context) {
    redisContainer = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
        .withExposedPorts(REDIS_PORT);
    redisContainer.start();
    System.setProperty("spring.data.redis.host", redisContainer.getHost());
    System.setProperty("spring.data.redis.port", String.valueOf(redisContainer.getMappedPort(REDIS_PORT
    )));
  }
}
