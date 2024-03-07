package com.example.instagrambe.auth.jwt.filter;

import com.example.instagrambe.auth.jwt.service.JwtService;
import com.example.instagrambe.auth.security.CustomUserDetails;
import com.example.instagrambe.common.exception.custom.JwtValidationException;
import com.example.instagrambe.auth.jwt.constant.JwtProperties;
import com.example.instagrambe.domain.member.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final JwtProperties jwtProperties;

  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (isNoCheckURI(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

      Optional<String> refreshToken = extractRefreshToken(request);
      if (refreshToken.isPresent()) {
        reIssueAccessTokenAndRefreshToken(response, refreshToken.get());
        log.info("리프레쉬 토큰, 엑세스 헤더 반환 완료");
        return;
      }

      Optional<String> accessToken = extractAccessToken(request);
      if(accessToken.isPresent()){
        Member member = findMemberByAccessToken(accessToken.get());
        saveAuthentication(member);
        log.info("엑세스 토큰으로 유저 정보 추출 후 authentication 객체 저장 완료");
        filterChain.doFilter(request, response);
        return;
      }
      throw new JwtValidationException("AccessToken과 RefreshToken이 둘다 유효하지 않습니다.");
  }

  private boolean isNoCheckURI(String requestURI) {
    return requestURI.contains(JwtProperties.NO_CHECK_URL_STARTER) && !requestURI.equals(JwtProperties.LOGOUT_URI);
  }

  private Optional<String> extractRefreshToken(HttpServletRequest request) {
    return jwtService.extractToken(request.getHeader(jwtProperties.getRefreshHeader()))
        .filter(jwtService::isValidRefreshToken);
  }

  private void reIssueAccessTokenAndRefreshToken(HttpServletResponse response, String refreshToken) {
    jwtService.reIssueAccessTokenAndRefreshToken(response, refreshToken);
  }

  private Optional<String> extractAccessToken(HttpServletRequest request) {
    return jwtService.extractToken(request.getHeader(jwtProperties.getAccessHeader()))
        .filter(jwtService::isValidAccessToken);
  }

  private Member findMemberByAccessToken(String accessToken) {
    return jwtService.findMemberByAccessToken(accessToken);
  }

  private void saveAuthentication(Member member) {
    CustomUserDetails customUserDetails = new CustomUserDetails(member);

    Authentication authentication
        = new UsernamePasswordAuthenticationToken(customUserDetails, null,
        authoritiesMapper.mapAuthorities(customUserDetails.getAuthorities()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
