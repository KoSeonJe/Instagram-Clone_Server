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
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final MemberService memberService;
  private final JwtProperties jwtProperties;

  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String requestURI = request.getRequestURI();
    if (requestURI.contains(JwtProperties.NO_CHECK_URL_STARTER) && !requestURI.equals(JwtProperties.LOGOUT_URI)) {
      filterChain.doFilter(request, response);
      return;
    }

      Optional<String> refreshToken = jwtService.extractToken(request.getHeader(jwtProperties.getRefreshHeader()))
          .filter(jwtService::isValidRefreshToken);

      if (refreshToken.isPresent()) {
        reIssueAccessTokenAndRefreshToken(response, refreshToken.get());
        log.info("리프레쉬 토큰, 엑세스 헤더 반환 완료");
        return;
      }

      Optional<String> accessToken = jwtService.extractToken(request.getHeader(jwtProperties.getAccessHeader()))
          .filter(jwtService::isValidAccessToken);
      if(accessToken.isPresent()){
        accessToken.ifPresent(this::authenticate);
        filterChain.doFilter(request, response);
        return;
      }
      throw new JwtValidationException("AccessToken과 RefreshToken이 둘다 유효하지 않습니다.");
  }

  private void authenticate(String accessToken) {
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

  private void reIssueAccessTokenAndRefreshToken(HttpServletResponse response, String refreshToken) {
    String email = extractEmail(refreshToken);
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
