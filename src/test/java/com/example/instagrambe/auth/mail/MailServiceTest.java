package com.example.instagrambe.auth.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
  @Mock
  JavaMailSender javaMailSender;
  @InjectMocks
  MailService mailService;

  @DisplayName("입력한 이메일에 코드를 보낸다.")
  @Test
  void sendCodeToMailTest() {
    // given
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    // when
    mailService.sendMail(mimeMessage);
    // then
    verify(javaMailSender, times(1)).send(mimeMessage);
  }

  @DisplayName("메일 보낼 컨텐츠를 생성한다.")
  @Test
  void createMailContentTest() throws MessagingException, IOException {
    // given
    String to = "kosunje1344@naver.com";
    String authCode ="인증코드";
    MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
    given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
    // when
    MimeMessage createMessage = mailService.createMailContent(to, authCode);
    // then
    verify(javaMailSender, times(1)).createMimeMessage();
    assertThat(createMessage).isNotNull();
    assertThat(createMessage.getAllRecipients()[0].toString()).isEqualTo(to);
    assertThat((String)createMessage.getContent()).contains(authCode);
  }
}