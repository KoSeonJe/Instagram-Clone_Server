package com.example.instagrambe.auth.api.repository;

import com.example.instagrambe.config.TestContainerConfig;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(TestContainerConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MailCodeRepositoryTest {

  @Autowired
  MailCodeRepository mailCodeRepository;

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  @DisplayName("redis에 저장한 뒤 key로 value를 추출한다.")
  @Test
  void saveTest() {
    // given
    String key = "key";
    String value = "value";
    ValueOperations<String, String> redis = redisTemplate.opsForValue();
    // when
    mailCodeRepository.save(key, value);
    // then
    String find = redis.get(key);
    Assertions.assertThat(find).isEqualTo("value");
  }

  @DisplayName("redis에 저장된 key는 만료시간이 지나면 사라진다.")
  @Test
  void expireTest() throws InterruptedException {
    // given
    String key = "key";
    String value = "value";
    ValueOperations<String, String> redis = redisTemplate.opsForValue();
    // when
    redis.set(key, value);
    redisTemplate.expire(key, 3, TimeUnit.SECONDS);
    String beforeFind = redis.get(key);
    Thread.sleep(3500);
    String afterFind = redis.get(key);
    // then
    Assertions.assertThat(beforeFind).isEqualTo("value");
    Assertions.assertThat(afterFind).isNull();
  }

  @DisplayName("Redis에 저장되어 있는 값을 key를 이용하여 찾는다.")
  @Test
  void findValueByKeyTest() {
    // given
    String key = "key";
    String value = "value";
    ValueOperations<String, String> redis = redisTemplate.opsForValue();
    // when
    redis.set(key, value);
    String findValue = mailCodeRepository.findByKey(key).orElse(null);
    // then
    Assertions.assertThat(findValue).isEqualTo("value");
  }
}