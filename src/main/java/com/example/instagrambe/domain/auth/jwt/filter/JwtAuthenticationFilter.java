package com.example.instagrambe.domain.auth.jwt.filter;

import com.example.instagrambe.common.exception.custom.JwtValidationException;
import com.example.instagrambe.domain.auth.jwt.constant.JwtProperties;
import com.example.instagrambe.domain.auth.jwt.service.JwtService;
import com.example.instagrambe.domain.auth.security.CustomUserDetails;
import com.example.instagrambe.domain.member.entity.Member;
import com.example.instagrambe.domain.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String NO_CHECK_LOGIN_URL = "/api/auth/login";
  private static final String NO_CHECK_JOIN_URL = "/api/auth/join";

  private final JwtService jwtService;
  private final MemberService memberService;
  private final JwtProperties jwtProperties;

  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (request.getRequestURI().equals(NO_CHECK_LOGIN_URL) || request.getRequestURI().equals(NO_CHECK_JOIN_URL)) {
      doFilter(request, response, filterChain);
      return;
    }

    try {
      Optional<String> refreshToken = jwtService.extractToken(request.getHeader(jwtProperties.getRefreshHeader()))
          .filter(jwtService::isValidRefreshToken);

      if (refreshToken.isPresent()) {
        String email = extractEmail(refreshToken.get());
        reIssueAccessTokenAndRefreshToken(response, email);
        log.info("리프레쉬 토큰, 엑세스 헤더 반환 완료");
        return;
      }

      Optional<String> accessToken = jwtService.extractToken(request.getHeader(jwtProperties.getAccessHeader()))
          .filter(jwtService::isValidAccessToken);
      accessToken.ifPresent(this::authenticate);
      doFilter(request, response, filterChain);
    } catch (JwtValidationException | IllegalArgumentException | AuthenticationException exception) {
      request.setAttribute("exception", exception);
    }
  }

  private void authenticate(String accessToken) throws JwtValidationException {
    String email = extractEmail(accessToken);
    saveAuthentication(memberService.findMemberByEmail(email));
    log.info("authentication 객체 생성 및 저장 완료");
  }

  private void saveAuthentication(Member member) {
    CustomUserDetails customUserDetails = new CustomUserDetails(member);

    Authentication authentication
        = new UsernamePasswordAuthenticationToken(customUserDetails, null,
        authoritiesMapper.mapAuthorities(customUserDetails.getAuthorities()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void reIssueAccessTokenAndRefreshToken(HttpServletResponse response, String email) {
    String accessToken = jwtService.createAccessToken(email, new Date());
    jwtService.expireOriginRefreshToken(email);
    String reIssuedRefreshToken = jwtService.createRefreshToken(email, new Date());
    jwtService.sendAccessTokenAndRefreshToken(accessToken, reIssuedRefreshToken, response);
  }

  private String extractEmail(String token) {
    return jwtService.extractEmail(token)
        .orElseThrow(() -> new JwtValidationException("토큰으로 이메일을 찾을 수 없습니다."));
  }
}
