package com.example.instagrambe.domain.member.entity;

import com.example.instagrambe.common.support.entity.BaseEntity;
import com.example.instagrambe.domain.member.constant.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NonNull
  private String email;

  @NonNull
  private String password;

  @NonNull
  private String nickname;

  private String profile_image;

  private String introduction;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Builder
  public Member(@NonNull String email, @NonNull String password, @NonNull String nickname,
      String profile_image, String introduction) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.profile_image = profile_image;
    this.introduction = introduction;
    this.role = Role.USER;
  }
}
