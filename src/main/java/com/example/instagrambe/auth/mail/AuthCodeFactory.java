package com.example.instagrambe.auth.mail;

import java.util.Random;

public class AuthCodeFactory {
  public static String createKey() {
    StringBuilder key = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < 8; i++) {
      int index = random.nextInt(3); // 0~2 까지 랜덤

      switch (index) {
        case 0 -> key.append((char) ((random.nextInt(26)) + 97));
        //  a~z  (ex. 1+97=98 => (char)98 = 'b')
        case 1 -> key.append((char) ((random.nextInt(26)) + 65));
        //  A~Z
        case 2 -> key.append(random.nextInt(10));
        // 0~9
      }
    }
    return key.toString();
  }
}
