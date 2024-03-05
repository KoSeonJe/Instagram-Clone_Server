package com.example.instagrambe.common.support.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

@Getter
public class ErrorMessage {

  private final int code;

  private final String message;

  @JsonInclude(Include.NON_NULL)
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