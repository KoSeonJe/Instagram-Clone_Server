package com.example.instagrambe.auth.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

@Slf4j
public class CustomJsonUsernamePasswordFilter extends AbstractAuthenticationProcessingFilter {

  private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/auth/login";
  private static final String LOGIN_HTTP_METHOD = "POST";
  private static final String CONTENT_TYPE_JSON = "application/json";
  private static final String USERNAME_KEY = "email";
  private static final String PASSWORD_KEY = "password";
  private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
      new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, LOGIN_HTTP_METHOD);

  private final ObjectMapper objectMapper;

  public CustomJsonUsernamePasswordFilter(ObjectMapper objectMapper) {
    super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException {

    validateContentType(request);

    String messageBody = getRequestBody(request);

    Map<String, String> usernamePasswordBoard = convertMessageToMap(messageBody);

    String email = usernamePasswordBoard.get(USERNAME_KEY);
    String password = usernamePasswordBoard.get(PASSWORD_KEY);

    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);//principal 과 credentials 전달

    return this.getAuthenticationManager().authenticate(authRequest);
  }

  private void validateContentType(HttpServletRequest request) {
    if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE_JSON)) {
      log.info("content type 유효하지 않다.");
      throw new AuthenticationServiceException(
          "Authentication Content-Type not supported: " + request.getContentType());
    }
  }

  private String getRequestBody(HttpServletRequest request) throws IOException {
    return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
  }

  private Map<String, String> convertMessageToMap(String messageBody)
      throws JsonProcessingException {
    return objectMapper.readValue(messageBody, Map.class);
  }
}
