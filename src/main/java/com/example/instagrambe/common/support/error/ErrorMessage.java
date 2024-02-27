package com.example.instagrambe.common.support.error;

import lombok.Getter;

@Getter
public class ErrorMessage {

  private final int code;

  private final String message;

  private final Object data;

  public ErrorMessage(ErrorType errorType) {
    this.code = errorType.getStatusCode();
    this.message = errorType.getMessage();
    this.data = null;
  }

  public ErrorMessage(ErrorType errorType, Object data) {
    this.code = errorType.getStatusCode();
    this.message = errorType.getMessage();
    this.data = data;
  }
}