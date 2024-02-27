package com.example.instagrambe.domain.auth.security.handler;

import com.example.instagrambe.domain.auth.jwt.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtService jwtService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    String email = extractUserName(authentication);
    String accessToken = jwtService.createAccessToken(email, new Date());
    String refreshToken = jwtService.createRefreshToken(email, new Date());

    jwtService.sendAccessTokenAndRefreshToken(accessToken, refreshToken, response);
    response.setContentType("text/plain; charset=UTF-8");
    response.getWriter().write("로그인 성공! 엑세스 토큰, 리프레시 토큰 발급 완료");
  }

  private String extractUserName(Authentication authentication) {
    UserDetails userDetails = (UserDetails)authentication.getPrincipal();
    return userDetails.getUsername();
  }
}
