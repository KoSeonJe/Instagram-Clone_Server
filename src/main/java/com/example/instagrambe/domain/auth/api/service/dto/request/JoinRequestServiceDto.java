package com.example.instagrambe.domain.auth.api.service.dto.request;

import com.example.instagrambe.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JoinRequestServiceDto {

  private String email;
  private String password;
  private String nickname;

  @Builder
  public JoinRequestServiceDto(String email, String password, String nickname) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
  }

  public Member toEntity(String password) {
    return Member.builder()
        .email(email)
        .password(password)
        .nickname(nickname)
        .build();
  }
}
