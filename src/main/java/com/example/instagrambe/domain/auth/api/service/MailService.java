package com.example.instagrambe.domain.auth.api.service;

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

  private final JavaMailSender emailSender;

  public void sendCodeToEmail(String email) {
    String authCode = createKey();
    String emailText = getEmailText(authCode);
    MimeMessage mail = createMailContent(email, emailText);
    emailSender.send(mail);
    emailBoards.putEmailBoard(toEmail, authCode);
  }

}
