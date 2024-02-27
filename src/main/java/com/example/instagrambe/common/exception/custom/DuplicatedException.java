package com.example.instagrambe.common.exception.custom;

public class DuplicatedException extends RuntimeException {

  public DuplicatedException(String message) {
    super(message);
  }
}
