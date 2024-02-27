package com.example.instagrambe.common.exception.custom;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JwtValidationException extends RuntimeException{
  public JwtValidationException(String message){
    super(message);
  }
}
