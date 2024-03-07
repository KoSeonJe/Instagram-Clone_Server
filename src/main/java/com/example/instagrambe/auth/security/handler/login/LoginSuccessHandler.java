package com.example.instagrambe.auth.security.handler.login;

import com.example.instagrambe.auth.jwt.service.JwtService;
import com.example.instagrambe.common.support.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    String email = extractUserName(authentication);
    String accessToken = jwtService.createAccessToken(email, new Date());
    String refreshToken = jwtService.createRefreshToken(email, new Date());

    jwtService.sendAccessTokenAndRefreshToken(accessToken, refreshToken, response);
    log.info("로그인에 성공하였습니다! 헤더에 accessToken, refreshToken이 반환되었습니다.");
    sendResponseMessage(response);
  }

  private void sendResponseMessage(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(LoginHandlerProperties.CONTENT_TYPE);
    response.setCharacterEncoding(LoginHandlerProperties.CHARSET);
    ApiResponse<String> resultResponse = ApiResponse.success(LoginHandlerProperties.SUCCESS_MESSAGE);
    String responseMessage = objectMapper.writeValueAsString(resultResponse);
    response.getWriter().write(responseMessage);
  }

  private String extractUserName(Authentication authentication) {
    UserDetails userDetails = (UserDetails)authentication.getPrincipal();
    return userDetails.getUsername();
  }
}
