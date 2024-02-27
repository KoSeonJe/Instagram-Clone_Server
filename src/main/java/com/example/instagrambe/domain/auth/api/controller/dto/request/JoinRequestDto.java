package com.example.instagrambe.domain.auth.api.controller.dto.request;

import com.example.instagrambe.domain.auth.api.service.dto.request.JoinRequestServiceDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JoinRequestDto {
  @NotNull(message = "이메일을 입력해주세요")
  @Email(message = "이메일 형식으로 작성해주세요")
  private String email;

  @NotNull(message = "비밀번호를 입력해주세요")
  @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
      message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
  private String password;

  @NotNull(message = "닉네임을 입력해주세요")
  @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$",
      message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
  private String nickname;

  public JoinRequestServiceDto toServiceDto() {
    return JoinRequestServiceDto.builder()
        .email(email)
        .password(password)
        .nickname(nickname)
        .build();
  }
}
