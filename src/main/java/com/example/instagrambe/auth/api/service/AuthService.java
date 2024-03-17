package com.example.instagrambe.auth.api.service;

import com.example.instagrambe.auth.api.service.dto.request.JoinRequestServiceDto;
import com.example.instagrambe.auth.api.service.dto.request.VerifyCodeServiceDto;
import com.example.instagrambe.auth.api.service.dto.response.MemberResponseServiceDto;
import com.example.instagrambe.auth.mail.AuthCodeRepository;
import com.example.instagrambe.auth.mail.MailService;
import com.example.instagrambe.auth.security.jwt.service.JwtService;
import com.example.instagrambe.common.exception.custom.JwtValidationException;
import com.example.instagrambe.domain.member.entity.Member;
import com.example.instagrambe.domain.member.service.MemberService;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Objects;
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
  private final MailService mailService;
  private final PasswordEncoder passwordEncoder;
  private final AuthCodeRepository authCodeRepository;
  private final AuthValidator authValidator;

  private static final String SUCCESS_VERIFICATION = "SUCCESS";


  public MemberResponseServiceDto join(JoinRequestServiceDto requestServiceDto) {
    String email = requestServiceDto.getEmail();
    authValidator.validateJoin(email);
    String encodePassword = getEncodedPassword(requestServiceDto.getPassword());
    Member member = requestServiceDto.toEntity(encodePassword);
    memberService.save(member);
    log.info("회원가입 완료");
    return MemberResponseServiceDto.from(member);
  }

  public void sendCode(String email, String authCode) {
    MimeMessage mail = mailService.createMailContent(email, authCode);
    mailService.sendMail(mail);
    authCodeRepository.save(email, authCode);
  }

  public void verifyCode(VerifyCodeServiceDto verifyCodeServiceDto) {
    verify(verifyCodeServiceDto.getEmail(), verifyCodeServiceDto.getVerifyCode());
  }

  public void logout(String accessToken, Date now) {
    String extractAccessToken = jwtService.extractToken(accessToken)
        .orElseThrow(() -> new JwtValidationException("토큰을 추출하는데 실패하였습니다."));
    String email = jwtService.extractEmail(extractAccessToken);
    jwtService.accessTokenToBlackList(extractAccessToken, now);
    jwtService.expireOriginRefreshToken(email);
    log.info("엑세스토큰 블랙리스트 및 리프레시 토큰 레디스에서 삭제 완료");
  }

  private String getEncodedPassword(String password) {
    return passwordEncoder.encode(password);
  }

  private void verify(String email, String verifyCode) {
    String authCode = authCodeRepository.findByKey(email)
        .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 인증 코드를 보낸 뒤 다시 시도해주세요"));
    if (Objects.equals(authCode, verifyCode)) {
      authCodeRepository.save(email, SUCCESS_VERIFICATION);
      log.info("인증코드 확인");
      return;
    }
    throw new IllegalArgumentException("인증 코드가 적절하지 않습니다.");
  }
}
