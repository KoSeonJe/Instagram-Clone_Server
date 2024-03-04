package com.example.instagrambe.domain.auth.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.instagrambe.common.exception.custom.JwtExpiredException;
import com.example.instagrambe.domain.auth.jwt.constant.JwtProperties;
import com.example.instagrambe.domain.auth.jwt.repository.TokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

  private final TokenRepository tokenRepository;
  private final JwtProvider jwtProvider;
  private final JwtProperties jwtProperties;

  public Optional<String> extractEmail(String token) {
    return jwtProvider.extractEmail(token);
  }

  public Optional<String> extractToken(String requestTokenHeader) {
    return jwtProvider.extractToken(requestTokenHeader);
  }

  public String createAccessToken(String username, Date now) {
    return jwtProvider.createAccessToken(username, now);
  }

  public String createRefreshToken(String username, Date now) {
    return jwtProvider.createRefreshToken(username, now);
  }

  public void expireOriginRefreshToken(String email) {
    tokenRepository.delete(email);
  }

  public void accessTokenToBlackList(String accessToken, Date now) {
    Date expiresAt = JWT.decode(accessToken).getExpiresAt();
    Long expiration = expiresAt.getTime() - now.getTime();
    tokenRepository.save(accessToken, JwtProperties.BLACK_LIST, expiration);
  }

  public void sendAccessTokenAndRefreshToken(String accessToken, String refreshToken,
      HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setHeader(jwtProperties.getAccessHeader(), accessToken);
    response.setHeader(jwtProperties.getRefreshHeader(), refreshToken);
    log.info("엑세스 토큰, 리프레쉬 토큰 응답 헤더 전달 완료");
  }

  public boolean isValidAccessToken(String accessToken) {
    try {
      Date expiresAt = JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build()
          .verify(accessToken)
          .getExpiresAt();
      validateExpiration(expiresAt);
      if (!isLogoutToken(accessToken)) {
        return true;
      }
      throw new JWTVerificationException("로그아웃 된 토큰입니다.");
    } catch (JWTVerificationException e) {
      log.warn("[Warn] 유효하지 않은 토큰입니다. {}", e.getMessage());
      return false;
    }
  }

  public boolean isValidRefreshToken(String refreshToken) {
    try {
      Date expiresAt = JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build()
          .verify(refreshToken)
          .getExpiresAt();
      validateExpiration(expiresAt);
      if (existTokenByEmailInRedis(refreshToken)) {
        return true;
      }
      throw new JWTVerificationException("Redis에 저장되지 않은 토큰입니다");
    } catch (JWTVerificationException e) {
      log.warn("[Warn] 유효하지 않은 토큰입니다. {}", e.getMessage());
      return false;
    }
  }

  private void validateExpiration(Date expiresAt) {
    if(expiresAt.before(new Date())) {
      throw new JwtExpiredException("토큰이 만료되었습니다.");
    }
  }

  private boolean existTokenByEmailInRedis(String refreshToken) {
    Optional<String> emailByToken = extractEmail(refreshToken);
    return emailByToken.filter(s -> Objects.equals(refreshToken, tokenRepository.findValueByKey(s)))
        .isPresent();
  }

  private boolean isLogoutToken(String token) {
    Optional<String> accessTokenValue = tokenRepository.findValueByKey(token);
    return accessTokenValue.filter(value -> Objects.equals(JwtProperties.BLACK_LIST, value))
        .isPresent();
  }
}
