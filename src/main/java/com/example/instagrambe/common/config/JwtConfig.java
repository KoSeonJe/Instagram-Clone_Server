package com.example.instagrambe.common.config;

import com.example.instagrambe.domain.auth.jwt.constant.JwtProperties;
import com.example.instagrambe.domain.auth.jwt.filter.JwtAuthenticationFilter;
import com.example.instagrambe.domain.auth.jwt.filter.JwtExceptionFilter;
import com.example.instagrambe.domain.auth.jwt.repository.TokenRepository;
import com.example.instagrambe.domain.auth.jwt.service.JwtProvider;
import com.example.instagrambe.domain.auth.jwt.service.JwtService;
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
  private final MemberService memberService;
  private final ObjectMapper objectMapper;


  @Bean
  public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
    return new JwtAuthenticationFilter(jwtService(), memberService, jwtProperties);
  }

  @Bean
  public JwtExceptionFilter jwtExceptionFilter() {
    return new JwtExceptionFilter(objectMapper);
  }

  @Bean
  public JwtService jwtService() {
    return new JwtService(tokenRepository, jwtProvider(), jwtProperties);
  }

  @Bean
  public JwtProvider jwtProvider() {
    return new JwtProvider(tokenRepository, jwtProperties);
  }
}
