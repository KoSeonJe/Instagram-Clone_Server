package com.example.instagrambe.auth.mail;

import java.util.Optional;

public interface AuthCodeRepository {

  void save(String key, String value);

  Optional<String> findByKey(String key);

  void delete(String email);
}
