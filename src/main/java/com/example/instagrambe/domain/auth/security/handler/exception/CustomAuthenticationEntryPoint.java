package com.example.instagrambe.domain.auth.security.handler.exception;

import com.example.instagrambe.common.exception.custom.JwtValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final HandlerExceptionResolver resolver;

  public CustomAuthenticationEntryPoint(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.resolver = resolver;
  }
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException e) throws IOException, ServletException {
    if(request.getAttribute("exception") instanceof AuthenticationException){
      resolver.resolveException(request, response, null, e);
      return;
    }

    if(request.getAttribute("exception") instanceof JwtValidationException){
      resolver.resolveException(request, response, null, (JwtValidationException) request.getAttribute("exception"));
      return;
    }

    if(request.getAttribute("exception") instanceof IllegalArgumentException){
      resolver.resolveException(request, response, null, (IllegalArgumentException) request.getAttribute("exception"));
    }
  }
}
