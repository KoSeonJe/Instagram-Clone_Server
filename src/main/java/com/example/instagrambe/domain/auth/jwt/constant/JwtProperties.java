package com.example.instagrambe.domain.auth.jwt.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtProperties {

  public final String accessHeader;
  public final String refreshHeader;
  public final String secretKey;
  public final Long accessTokenExpirationPeriod;
  public final Long refreshTokenExpirationPeriod;
  public static final String NO_CHECK_URL_STARTER = "/api/auth";
  public static final String LOGOUT_URI = "/api/auth/logout";
  public static final String BLACK_LIST = "logout";
  public static final String REPLACEMENT = "";
  public static final String TOKEN_TYPE = "Bearer ";
  public static final String EMAIL_CLAIM = "email";
  public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";

  public JwtProperties(
      @Value("${jwt.access.header}")
      String accessHeaderTemp,
      @Value("${jwt.refresh.header}")
      String refreshHeaderTemp,
      @Value("${jwt.secretKey}")
      String secretKeyTemp,
      @Value("${jwt.access.expiration}")
      Long accessTokenExpirationPeriodTemp,
      @Value("${jwt.refresh.expiration}")
      Long refreshTokenExpirationPeriodTemp) {
    this.accessHeader = accessHeaderTemp;
    this.refreshHeader = refreshHeaderTemp;
    this.secretKey = secretKeyTemp;
    this.accessTokenExpirationPeriod = accessTokenExpirationPeriodTemp;
    this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriodTemp;
  }
}
