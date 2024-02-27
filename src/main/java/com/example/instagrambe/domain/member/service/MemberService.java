package com.example.instagrambe.domain.member.service;

import com.example.instagrambe.common.exception.custom.DuplicatedException;
import com.example.instagrambe.domain.member.entity.Member;
import com.example.instagrambe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;

  public Member findMemberByEmail(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(()-> new IllegalArgumentException("해당 이메일로 멤버를 찾을 수 없습니다."));
  }

  @Transactional
  public void save(Member member) {
    memberRepository.save(member);
  }

  public void validateDuplicatedEmail(String email) {
    if(memberRepository.findByEmail(email).isPresent()){
     throw new DuplicatedException("중복된 이메일 입니다.");
    }
  }
}
