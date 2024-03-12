package com.example.instagrambe.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.instagrambe.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @DisplayName("찾는 이메일 아이디가 존재할 때 멤버를 찾는다.")
  @Test
  void findMemberByEmail() {
    // given
    Member member = Member.builder()
        .email("1234")
        .password("1234")
        .nickname("고")
        .build();

    memberRepository.save(member);

    // when
    Member find = memberRepository.findByEmail("1234").orElseThrow();

    // then
    assertThat(member).isSameAs(find);
    assertThat(member.getEmail()).isEqualTo(find.getEmail());
    assertThat(member.getEmail()).isNotNull();
    assertThat(memberRepository.count()).isEqualTo(1);
  }

  @DisplayName("찾는 이메일 아이디가 존재하지 않을 때 예외를 발생시킨다.")
  @Test
  void findMemberByEmailException() {
    // given
    Member member = Member.builder()
        .email("1234")
        .password("1234")
        .nickname("고")
        .build();

    memberRepository.save(member);

    // when
    ThrowingCallable exceptionCode = () ->{
      Member find = memberRepository.findByEmail("1").orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
    };

    // then
    assertThatThrownBy(exceptionCode).isInstanceOf(IllegalArgumentException.class).hasMessage("이메일을 찾을 수 없습니다.");
  }

  @DisplayName("이메일이 존재하면 true, 없으면 false를 반환한다.")
  @Test
  void existMemberByEmailTest() {
    // given
    Member member = Member.builder()
        .email("1234")
        .password("1234")
        .nickname("고")
        .build();

    memberRepository.save(member);
    // when
    boolean result1 = memberRepository.existsByEmail("1234");
    boolean result2 = memberRepository.existsByEmail("12");
    // then
    Assertions.assertThat(result1).isTrue();
    Assertions.assertThat(result2).isFalse();
  }
}