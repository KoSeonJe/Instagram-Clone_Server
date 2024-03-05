package com.example.instagrambe.domain.auth.security.handler.login;

import com.example.instagrambe.common.support.error.ErrorType;
import com.example.instagrambe.common.support.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    sendResponseMessage(response);
  }

  private void sendResponseMessage(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    response.setContentType(LoginHandlerProperties.CONTENT_TYPE);
    response.setCharacterEncoding(LoginHandlerProperties.CHARSET);
    ApiResponse<String> resultResponse = ApiResponse.of(ErrorType.ILLEGAL_VALUE, LoginHandlerProperties.FAIL_MESSAGE);
    String responseMessage = objectMapper.writeValueAsString(resultResponse);
    response.getWriter().write(responseMessage);
  }
}
