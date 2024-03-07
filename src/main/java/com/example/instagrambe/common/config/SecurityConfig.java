package com.example.instagrambe.common.config;

import com.example.instagrambe.auth.jwt.filter.JwtAuthenticationFilter;
import com.example.instagrambe.auth.jwt.filter.JwtExceptionFilter;
import com.example.instagrambe.auth.jwt.service.JwtService;
import com.example.instagrambe.auth.security.filter.CustomJsonUsernamePasswordFilter;
import com.example.instagrambe.auth.security.handler.login.LoginFailureHandler;
import com.example.instagrambe.auth.security.handler.login.LoginSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;
  private final JwtService jwtService;
  private final UserDetailsService customUserDetailsService;
  private final AuthenticationEntryPoint customAuthenticationEntryPoint;
  private final AccessDeniedHandler customAccessDeniedHandler;
  private final ObjectMapper objectMapper;

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .requestMatchers(new AntPathRequestMatcher("/resource/**"))
        .requestMatchers(new AntPathRequestMatcher("/favicon.ico"))
        .requestMatchers(new AntPathRequestMatcher("/css/**"))
        .requestMatchers(new AntPathRequestMatcher("/js/**"))
        .requestMatchers(new AntPathRequestMatcher("/img/**"))
        .requestMatchers(new AntPathRequestMatcher("/lib/**"));
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      HandlerMappingIntrospector introspector) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)

        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        .authorizeHttpRequests(authorizeHttpRequests ->
            authorizeHttpRequests
                .requestMatchers(new MvcRequestMatcher(introspector, "/")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/api/auth/*")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/api/auth/*")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/api/auth/logout"))
                .authenticated()
                .anyRequest().authenticated()
        )
        .exceptionHandling(
            (exception) -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
        .exceptionHandling((exception) -> exception.accessDeniedHandler(customAccessDeniedHandler));

    //security와 jwt 토큰을 사용하기 때문에, jwt토큰 검증이 필요함.
    //토큰을 받아오고 토큰의 내용으로 인증하기 때문에, 토큰 검증 먼저 해야함.
    http.addFilterAfter(customJsonUsernamePasswordFilter(), LogoutFilter.class)
        .addFilterBefore(jwtAuthenticationFilter, CustomJsonUsernamePasswordFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CustomJsonUsernamePasswordFilter customJsonUsernamePasswordFilter() {
    CustomJsonUsernamePasswordFilter customJsonUsernamePasswordFilter = new CustomJsonUsernamePasswordFilter(
        objectMapper);
    customJsonUsernamePasswordFilter.setAuthenticationManager(authenticationManager());
    customJsonUsernamePasswordFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
    customJsonUsernamePasswordFilter.setAuthenticationFailureHandler(loginFailureHandler());
    return customJsonUsernamePasswordFilter;
  }

  @Bean
  public LoginSuccessHandler loginSuccessHandler() {
    return new LoginSuccessHandler(jwtService, objectMapper);
  }

  @Bean
  public LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler(objectMapper);
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(customUserDetailsService);
    return new ProviderManager(provider);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
