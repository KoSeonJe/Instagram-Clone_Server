package com.example.instagrambe.common.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisProperties {
  public final String host;
  public final int port;

  private RedisProperties(
      @Value("${spring.data.redis.host}") String host,
      @Value("${spring.data.redis.port}") int port
      ){
    this.host = host;
    this.port = port;
  }
}
