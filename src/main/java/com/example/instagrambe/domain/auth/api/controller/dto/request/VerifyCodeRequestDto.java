package com.example.instagrambe.domain.auth.api.controller.dto.request;

import com.example.instagrambe.domain.auth.api.service.dto.request.VerifyCodeServiceDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyCodeRequestDto {

  @Email(message = "이메일 형식을 지켜주세요")
  @NotNull(message = "이메일을 입력해주세요")
  private String email;

  @NotNull(message = "인증 코드를 입력해주세요")
  private String verifyCode;

  public VerifyCodeServiceDto toServiceDto() {
    return VerifyCodeServiceDto.builder()
        .email(email)
        .verifyCode(verifyCode)
        .build();
  }
}
