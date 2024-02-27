package com.example.instagrambe.domain.auth.jwt.repository;

import java.util.Optional;

public interface TokenRepository {

  void save(String key, String value, Long expiration);

  void delete(String key);

  Optional<String> findValueByKey(String key);
}
