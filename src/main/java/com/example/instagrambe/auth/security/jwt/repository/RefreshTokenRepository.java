package com.example.instagrambe.auth.security.jwt.repository;

import com.example.instagrambe.common.exception.custom.JwtValidationException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository implements TokenRepository{

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void save(String key, String value, Long expiration) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(key, value, expiration, TimeUnit.MILLISECONDS);
  }

  @Override
  public void delete(String key) {
    Optional<String> value = findValueByKey(key);
    if(value.isPresent()){
      redisTemplate.opsForValue().getAndDelete(key);
      return;
    }
    throw new JwtValidationException("해당 키로 값을 찾을 수 없습니다.");
  }

  @Override
  public Optional<String> findValueByKey(String key) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    return Optional.ofNullable(valueOperations.get(key));
  }
}
