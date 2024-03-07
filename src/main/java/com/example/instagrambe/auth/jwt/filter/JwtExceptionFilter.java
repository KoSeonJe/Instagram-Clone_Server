package com.example.instagrambe.auth.jwt.filter;

import com.example.instagrambe.common.exception.custom.JwtExpiredException;
import com.example.instagrambe.common.exception.custom.JwtValidationException;
import com.example.instagrambe.common.support.error.ErrorMessage;
import com.example.instagrambe.common.support.error.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

  private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=UTF-8";

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (JwtValidationException e) {
      responseErrorMessage(request, response, ErrorType.JWT_VALID_EXCEPTION);
    } catch (IllegalArgumentException e) {
      responseErrorMessage(request, response, ErrorType.NO_SUCH_MEMBER);
    } catch (JwtExpiredException e) {
      responseErrorMessage(request, response, ErrorType.JWT_EXPIRED_EXCEPTION);
    }
  }

  private void responseErrorMessage(HttpServletRequest request, HttpServletResponse response,
      ErrorType errorType) throws IOException {
    response.setContentType(RESPONSE_CONTENT_TYPE);
    response.setStatus(errorType.getStatusCode());
    ObjectMapper objectMapper = new ObjectMapper();
    String jwtErrorMessage = objectMapper.writeValueAsString(new ErrorMessage(errorType));
    response.getWriter().write(jwtErrorMessage);
  }
}
