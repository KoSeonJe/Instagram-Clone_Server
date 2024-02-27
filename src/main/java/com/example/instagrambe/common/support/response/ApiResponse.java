package com.example.instagrambe.common.support.response;

import com.example.instagrambe.common.support.error.ErrorMessage;
import com.example.instagrambe.common.support.error.ErrorType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ApiResponse<T> {

  private ResultType resultType;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ErrorMessage errorMessage;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T data;

  public static <T>ApiResponse<T> success() {
    return ApiResponse.<T>builder()
        .resultType(ResultType.SUCCESS)
        .build();
  }

  public static <T>ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .resultType(ResultType.SUCCESS)
        .data(data)
        .build();
  }

  public static <T>ApiResponse<T> of(ErrorType errorType) {
    return ApiResponse.<T>builder()
        .resultType(ResultType.ERROR)
        .errorMessage(new ErrorMessage(errorType))
        .build();
  }

  public static <T>ApiResponse<T> of(ErrorType errorType, T data) {
    return ApiResponse.<T>builder()
        .resultType(ResultType.ERROR)
        .errorMessage(new ErrorMessage(errorType, data))
        .build();
  }
}
