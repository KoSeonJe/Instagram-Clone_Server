package com.example.instagrambe.auth.mail;

import com.example.instagrambe.common.exception.custom.CustomMessagingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MailService {
  private static final String MAIL_SUBJECT = "Instagram_Sever 인증 코드";
  private static final String EMAIL_CONTENT = "인증 코드 : ";
  private static final String CHARSET = "utf-8";
  private static final String ADDRESS_NAME = "Instagram_Clone";
  private static final String ADDRESS_EMAIL = "a01039261344@gmail.com";

  private final JavaMailSender emailSender;

  public void sendMail(MimeMessage mailContent) {
      emailSender.send(mailContent);
      log.info("인증 코드 전송 성공");
  }

  public MimeMessage createMailContent(String to, String authCode) {
    try {
      MimeMessage message = emailSender.createMimeMessage();
      message.addRecipients(RecipientType.TO, to);
      message.setSubject(MAIL_SUBJECT);
      message.setText(EMAIL_CONTENT + authCode, CHARSET);
      message.setFrom(new InternetAddress(ADDRESS_EMAIL, ADDRESS_NAME));
      return message;
    } catch (MessagingException | IOException e) {
      log.error("인증 코드 전송에 실패하였습니다.");
      throw new CustomMessagingException("인증 코드 전송 실패");
    }
  }
}
