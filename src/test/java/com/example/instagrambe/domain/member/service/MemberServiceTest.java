package com.example.instagrambe.domain.member.service;

import com.example.instagrambe.common.exception.custom.DuplicatedException;
import com.example.instagrambe.domain.member.entity.Member;
import com.example.instagrambe.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  @DisplayName("이메일을 가지고 멤버를 찾는다.")
  @Test
  void findMemberByEmailTest() {
    // given
    Member member = Member.builder()
        .email("1234")
        .password("1234")
        .nickname("고")
        .build();
    memberRepository.save(member);
    // when
    Member find = memberService.findMemberByEmail("1234");
    // then
    Assertions.assertThat(find.getEmail()).isEqualTo("1234");
  }

  @DisplayName("멤버를 저장한다")
  @Test
  void saveTest() {
    // given
    Member member = Member.builder()
        .email("1234")
        .password("1234")
        .nickname("고")
        .build();
    // when
    memberService.save(member);
    // then
    Assertions.assertThat(memberRepository.findAll()).contains(member);
  }

  @DisplayName("해당 이메일이 repository에 이미 저장되어 있는 이메일이라면 예외를 발생시킨다.")
  @Test
  void validateDuplicatedEmailTest() {
    // given
    Member member = Member.builder()
        .email("1234")
        .password("1234")
        .nickname("고")
        .build();
    memberRepository.save(member);
    // when
    ThrowingCallable exceptionCode = () ->{
      memberService.validateDuplicatedEmail("1234");
    };
    // then
    Assertions.assertThatThrownBy(exceptionCode).isInstanceOf(DuplicatedException.class);
  }
}