package com.example.instagrambe.auth.api.service.dto.response;

import com.example.instagrambe.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberResponseServiceDto {

  private String email;

  @Builder
  public MemberResponseServiceDto(String email) {
    this.email = email;
  }

  public static MemberResponseServiceDto from(Member member) {
    return MemberResponseServiceDto.builder()
        .email(member.getEmail())
        .build();
  }
}
