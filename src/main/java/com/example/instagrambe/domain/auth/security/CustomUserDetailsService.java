package com.example.instagrambe.domain.auth.security;

import com.example.instagrambe.domain.member.entity.Member;
import com.example.instagrambe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일입니다."));

    return User.builder()
        .username(member.getEmail())
        .password(member.getPassword())
        .roles(member.getRole().getAuthName())
        .build();
  }
}
