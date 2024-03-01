package com.example.instagrambe.domain.auth.api.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MailCodeRepository implements AuthCodeRepository {

  private static final Long EXPIRATION = 3L;

  private final RedisTemplate<String, String> repository;

  @Override
  public void save(String key, String value) {
    ValueOperations<String, String> redis = repository.opsForValue();
    redis.set(key, value, EXPIRATION, TimeUnit.MINUTES);

  }

  @Override
  public void findByKey(String key) {

  }
}
