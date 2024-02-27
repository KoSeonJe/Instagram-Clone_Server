package com.example.instagrambe.common.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorType {
  DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 에러가 발생하였습니다."),
  JWT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, 400, "jwt가 유효하지 않습니다."),
  AUTHENTICATION_EXCEPTION(HttpStatus.BAD_REQUEST ,400, "인증을 실패하였습니다.."),
  ILLEGAL_VALUE(HttpStatus.BAD_REQUEST, 400, "유효하지 않은 값입니다."),
  DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, 400, "중복된 이메일입니다."
      );

  private final HttpStatus status;

  private final int statusCode;

  private final String message;
}
