package com.example.instagrambe.domain.auth.api.repository;

public interface AuthCodeRepository {

  void save(String key, String value);

  void findByKey(String key);
}
