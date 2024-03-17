package com.example.instagrambe.auth.api.service;

import com.example.instagrambe.auth.mail.AuthCodeRepository;
import com.example.instagrambe.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthValidator {

  private final AuthCodeRepository authCodeRepository;
  private final MemberService memberService;

  private static final String SUCCESS_VERIFICATION = "SUCCESS";

  public void validateJoin(String email) {
    memberService.validateDuplicatedEmail(email);
    validateEmailAuthSuccess(email);
  }
  private void validateEmailAuthSuccess(String email) {
    String authResult = authCodeRepository.findByKey(email)
        .orElseThrow(() -> new AuthenticationServiceException("이메일 인증을 완료해주세요"));

    if (!authResult.equals(SUCCESS_VERIFICATION)) {
      throw new AuthenticationServiceException("이메일 인증을 완료해주세요");
    }
  }
}
