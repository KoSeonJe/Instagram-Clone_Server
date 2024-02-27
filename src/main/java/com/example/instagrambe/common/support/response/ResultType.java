package com.example.instagrambe.common.support.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultType{
  SUCCESS("요청 성공"),
  ERROR("요청 에러 발생");
  private final String message;
}
