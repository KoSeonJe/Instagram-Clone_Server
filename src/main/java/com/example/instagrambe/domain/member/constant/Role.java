package com.example.instagrambe.domain.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
  ADMIN("ADMIN"), USER("USER");

  private final String authName;
}
