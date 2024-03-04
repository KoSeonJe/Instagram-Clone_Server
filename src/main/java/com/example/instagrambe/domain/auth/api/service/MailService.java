package com.example.instagrambe.domain.auth.api.service;

import com.example.instagrambe.common.exception.custom.CustomMessagingException;
import com.example.instagrambe.domain.auth.api.repository.AuthCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.IOException;
import java.util.Random;
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
  private final AuthCodeRepository authCodeRepository;

  public void sendCodeToEmail(String email) {
    try {
      String authCode = createKey();
      MimeMessage mail = createMailContent(email, authCode);
      emailSender.send(mail);
      authCodeRepository.save(email, authCode);
      log.info("인증 코드 전송 성공");
    } catch (MessagingException | IOException e) {
      log.error("인증 코드 전송에 실패하였습니다.");
      throw new CustomMessagingException("인증 코드 전송 실패");
    }
  }

  private MimeMessage createMailContent(String to, String authCode) throws MessagingException, IOException {
    MimeMessage message = emailSender.createMimeMessage();
    message.addRecipients(RecipientType.TO, to);
    message.setSubject(MAIL_SUBJECT);
    message.setText(EMAIL_CONTENT + authCode, CHARSET);
    message.setFrom(new InternetAddress(ADDRESS_EMAIL, ADDRESS_NAME));
    return message;
  }

  private String createKey() {
    StringBuilder key = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < 8; i++) {
      int index = random.nextInt(3); // 0~2 까지 랜덤

      switch (index) {
        case 0 -> key.append((char) ((random.nextInt(26)) + 97));
        //  a~z  (ex. 1+97=98 => (char)98 = 'b')
        case 1 -> key.append((char) ((random.nextInt(26)) + 65));
        //  A~Z
        case 2 -> key.append(random.nextInt(10));
        // 0~9
      }
    }
    return key.toString();
  }
}
