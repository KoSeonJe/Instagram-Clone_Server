package com.example.instagrambe.auth.mail;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthCodeFactoryTest {

  @DisplayName("영어 소문자, 대문자, 숫자를 섞어 8개 문자를 만든다.")
  @Test
  void test() {
    // given
    String key = AuthCodeFactory.createKey();
    String pattern = "^[a-zA-Z0-9]{8}$";
    // when
    // then
    Assertions.assertThat(key.length()).isEqualTo(8);
    Assertions.assertThat(key.matches(pattern)).isTrue();
  }
}