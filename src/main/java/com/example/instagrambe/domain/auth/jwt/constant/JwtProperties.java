package com.example.instagrambe.domain.auth.jwt.constant;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {
  @Value("${jwt.access.header}")
  private String accessHeaderTemp;

  @Value("${jwt.refresh.header}")
  private String refreshHeaderTemp;

  @Value("${jwt.secretKey}")
  private String secretKeyTemp;

  @Value("${jwt.access.expiration}")
  private Long accessTokenExpirationPeriodTemp;

  @Value("${jwt.refresh.expiration}")
  private Long refreshTokenExpirationPeriodTemp;

  public String accessHeader;
  public String refreshHeader;
  public String secretKey;
  public Long accessTokenExpirationPeriod;
  public Long refreshTokenExpirationPeriod;
  public static final String BLACK_LIST = "logout";
  public static final String REPLACEMENT = "";
  public static final String BEARER = "Bearer ";
  public static final String EMAIL_CLAIM = "email";
  public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";

  @PostConstruct
  public void init() {
    this.accessHeader = accessHeaderTemp;
    this.refreshHeader = refreshHeaderTemp;
    this.secretKey = secretKeyTemp;
    this.accessTokenExpirationPeriod = accessTokenExpirationPeriodTemp;
    this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriodTemp;
  }
}
