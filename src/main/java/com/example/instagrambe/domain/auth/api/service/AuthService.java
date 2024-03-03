package com.example.instagrambe.domain.auth.api.service;

import com.example.instagrambe.common.exception.custom.JwtValidationException;
import com.example.instagrambe.domain.auth.api.service.dto.request.JoinRequestServiceDto;
import com.example.instagrambe.domain.auth.api.service.dto.request.VerifyCodeServiceDto;
import com.example.instagrambe.domain.auth.api.service.dto.response.MemberResponseServiceDto;
import com.example.instagrambe.domain.auth.jwt.service.JwtService;
import com.example.instagrambe.domain.member.entity.Member;
import com.example.instagrambe.domain.member.service.MemberService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final MemberService memberService;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;

  public MemberResponseServiceDto join(JoinRequestServiceDto requestServiceDto) {
    memberService.validateDuplicatedEmail(requestServiceDto.getEmail());
    String encodePassword = passwordEncoder.encode(requestServiceDto.getPassword());
    Member member = requestServiceDto.toEntity(encodePassword);
    memberService.save(member);
    log.info("회원가입 완료");
    return MemberResponseServiceDto.from(member);
  }

  public void sendCode(String email) {
    mailService.sendCodeToEmail(email);
  }

  public void verifyCode(VerifyCodeServiceDto verifyCodeServiceDto) {
    mailService.verify(verifyCodeServiceDto.getEmail(), verifyCodeServiceDto.getVerifyCode());
  }

  public void logout(String accessHeader, Date now) {
    String accessToken = jwtService.extractToken(accessHeader)
        .orElseThrow(() -> new JwtValidationException("토큰을 추출하는데 실패하였습니다."));
    String email = jwtService.extractEmail(accessToken)
        .orElseThrow(() -> new JwtValidationException("해당 토큰으로 이메일을 찾을 수 없습니다."));
    jwtService.accessTokenToBlackList(accessToken, now);
    jwtService.expireOriginRefreshToken(email);
    log.info("엑세스토큰 블랙리스트 및 리프레시 토큰 레디스에서 삭제 완료");
  }
}
