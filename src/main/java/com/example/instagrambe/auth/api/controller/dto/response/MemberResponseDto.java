package com.example.instagrambe.auth.api.controller.dto.response;

import com.example.instagrambe.auth.api.service.dto.response.MemberResponseServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberResponseDto {

  private String email;

  @Builder
  private MemberResponseDto(String email) {
    this.email = email;
  }

  public static MemberResponseDto from(MemberResponseServiceDto memberResponseServiceDto) {
    return MemberResponseDto.builder()
        .email(memberResponseServiceDto.getEmail())
        .build();
  }
}
