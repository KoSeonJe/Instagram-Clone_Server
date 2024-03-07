package com.example.instagrambe.auth.api.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MailCodeRepository implements AuthCodeRepository {

  private static final Long EXPIRATION = 3L;

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void save(String key, String value) {
    ValueOperations<String, String> redis = redisTemplate.opsForValue();
    redis.set(key, value, EXPIRATION, TimeUnit.MINUTES);

  }

  @Override
  public Optional<String> findByKey(String key) {
    ValueOperations<String, String> redis = redisTemplate.opsForValue();
    String value = redis.get(key);
    return Optional.ofNullable(value);
  }

  @Override
  public void delete(String email) {
    ValueOperations<String, String> redis = redisTemplate.opsForValue();
    redis.getAndDelete(email);
  }
}
