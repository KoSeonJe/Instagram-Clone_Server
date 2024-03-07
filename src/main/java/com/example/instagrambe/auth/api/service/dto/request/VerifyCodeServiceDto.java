package com.example.instagrambe.auth.api.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class VerifyCodeServiceDto {

  private String email;

  private String verifyCode;

  @Builder
  public VerifyCodeServiceDto(String email, String verifyCode) {
    this.email = email;
    this.verifyCode = verifyCode;
  }
}
