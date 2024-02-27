package com.example.instagrambe.domain.auth.api.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthCodeRequestDto {

  @Email
  @NotNull
  private String email;
}
