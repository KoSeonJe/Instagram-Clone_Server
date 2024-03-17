package com.example.instagrambe.auth.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import com.example.instagrambe.auth.api.service.dto.request.JoinRequestServiceDto;
import com.example.instagrambe.auth.api.service.dto.request.VerifyCodeServiceDto;
import com.example.instagrambe.auth.mail.AuthCodeRepository;
import com.example.instagrambe.auth.mail.MailService;
import com.example.instagrambe.auth.security.jwt.service.JwtService;
import com.example.instagrambe.domain.member.entity.Member;
import com.example.instagrambe.domain.member.repository.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AuthServiceTest {

  @Autowired
  AuthService authService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @MockBean
  AuthValidator authValidator;

  @MockBean
  AuthCodeRepository authCodeRepository;

  @MockBean
  MailService mailService;

  @MockBean
  JwtService jwtService;

  @DisplayName("중복된 이메일, 이메일 인증이 완료되면 회원가입을 성공한다.")
  @Test
  void joinTest() {
    // given
    JoinRequestServiceDto dto = JoinRequestServiceDto.builder()
        .email("kosunje1344@naver.com")
        .password("12")
        .nickname("고선제")
        .build();
    willDoNothing().given(authValidator).validateJoin("kosunje1344@naver.com");
    // when
    authService.join(dto);
    // then
    Member member = memberRepository.findByEmail("kosunje1344@naver.com").orElse(null);
    assertThat(member).isNotNull();
    assertThat(member.getEmail()).isEqualTo("kosunje1344@naver.com");
  }

  @DisplayName("인증 코드를 전송하고, authCodeRepository에 이메일과 인증코드를 저장한다.")
  @Test
  void sendCodeTest() {
    // given
    String email = "kosunje1344@naver.com";
    String authCode = "1234";
    MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
    given(mailService.createMailContent(email, authCode)).willReturn(mimeMessage);
    // when
    authService.sendCode(email, authCode);
    // then
    verify(mailService).createMailContent(eq(email), eq(authCode));
    verify(mailService).sendMail(mimeMessage);
    verify(authCodeRepository).save(eq(email), eq(authCode));
  }

  @DisplayName("인증 코드가 일치하면 redis에 이메일과 성공문자를 저장한다.")
  @Test
  void verifyCodeTest() {
    // given
    String email = "1234";
    String code = "12";
    VerifyCodeServiceDto dto = VerifyCodeServiceDto.builder()
        .email(email)
        .verifyCode(code)
        .build();

    given(authCodeRepository.findByKey(email)).willReturn(Optional.of(code));
    // when
    authService.verifyCode(dto);
    // then
    verify(authCodeRepository).save(eq(email), eq("SUCCESS"));
  }

  @DisplayName("엑세스토큰 블랙리스트 및 리프레시토큰 만료를 수행한다.")
  @Test
  void logoutTest() {
    // given
    String accessHeader = "Bearer token";
    Date now = new Date();
    String accessToken = "token";
    String email = "email@example.com";

    given(jwtService.extractToken(accessHeader)).willReturn(Optional.of(accessToken));
    given(jwtService.extractEmail(accessToken)).willReturn(email);

    // when
    authService.logout(accessHeader, now);

    // then
    verify(jwtService).extractToken(accessHeader);
    verify(jwtService).extractEmail(accessToken);
    verify(jwtService).accessTokenToBlackList(accessToken, now);
    verify(jwtService).expireOriginRefreshToken(email);
  }
}