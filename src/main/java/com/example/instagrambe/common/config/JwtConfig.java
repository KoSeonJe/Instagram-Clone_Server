package com.example.instagrambe.common.config;

import com.example.instagrambe.auth.security.jwt.constant.JwtProperties;
import com.example.instagrambe.auth.security.jwt.filter.JwtAuthenticationFilter;
import com.example.instagrambe.auth.security.jwt.filter.JwtExceptionFilter;
import com.example.instagrambe.auth.security.jwt.repository.TokenRepository;
import com.example.instagrambe.auth.security.jwt.service.JwtService;
import com.example.instagrambe.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

  private final TokenRepository tokenRepository;
  private final JwtProperties jwtProperties;
  private final JwtService jwtService;
  private final MemberService memberService;
  private final ObjectMapper objectMapper;


  @Bean
  public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
    return new JwtAuthenticationFilter(jwtService, jwtProperties);
  }

  @Bean
  public JwtExceptionFilter jwtExceptionFilter() {
    return new JwtExceptionFilter(objectMapper);
  }
}
