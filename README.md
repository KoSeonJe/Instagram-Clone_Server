# Instagram-BE (기술 연습)

## 1. Auth

- 기술
    - JWT(AccessToken, RefreshToken)
    - Spring Security
    - Redis

### 회원가입

1. Security FilterChain은 거치지만 인증 필요 x
2. 회원가입 요청 url은 jwtFilter를 거치지 않는다.
3. 패스워드 암호화
4. 이메일 인증
    - Java MailSender API 사용
    - Redis를 통한 인증 코드 및 만료 시간 관리

### 로그인

1. Security FilterChain은 거치지만 인증 필요 x
2. 로그인 요청 url은 jwtFilter를 거치지 않는다.
3. 사용자 아이디(email)은 credential, 사용자 비밀번호(password)는 principal의 역할을 맡아 인증을 한다.

- 로그인 성공 시
1. 인증이 완료되어 authentication 객체가 생기고 securityContext에 저장된다.
2. 인증 성공 후 AccessToken과 RefreshToken이 응답 헤더를 통해 발급된다.
3. RefreshToken은 Redis를 통해 관리한다.
    - key - 사용자 email
    - value - Refreshtoken
    - Redis에 만료시간을 RefreshToken의 만료시간과 같게 저장한다.

- 로그인 실패 시
    - 실패 메시지 반환

### 토큰 인증

1. 응답 헤더의 AccessToken이 유효할 경우
    - AccessToken을 생성할 때 클레임으로 저장해놓은 사용자 이메일을 통해 사용자 객체를 찾아 Authentication 객체를 생성할 때 사용
    - Authentication 객체 생성 후 다음 인증 필터로 이동
    
2. 응답 헤더의 AccessToken이 만료된 경우
    - 예외 처리 후 클라이언트에게 RefreshToken을 요청한다.
    - 받은 RefreshToken 유효성 검사 후 토큰을 재발급 해준다.
        - 유효성 검사 시 Redis에 존재하는지 검사
    - 이때 RTR(RefreshToken Rotation) 전략을 사용하여 리프레쉬 토큰 탈취 위험을 줄인다.
        - AccessToken과 RefreshToken 모두 재발급하여 응답헤더로 전송
        - RefreshToken 재발급 시 Redis에 저장되어 있던 기존의 RefreshToken을 삭제 후 새로 발급받은 RefreshToken을 다시 저장한다.

### 로그아웃

1. 현재 AccessToken을 강제 만료시킬 수 없기 때문에, Redis를 이용하여 블랙리스트 처리 한다.
    - key - AccessToken
    - value - “logout”
    - Redis의 만료시간은 AccessToken의 남은 만료 시간으로 저장한다.
2. Redis에 저장된 RefreshToken을 삭제한다.
