# spring-shopping-precourse

상품 관리, 회원 가입과 로그인, 위시 리스트를 제공하는 쇼핑몰 HTTP API.
요구사항은 [`docs/requirements.md`](docs/requirements.md), API 명세는 [`docs/api-spec.md`](docs/api-spec.md)를 따른다.

## 구현할 기능 목록

- [x] **상품** 조회, 추가, 수정, 삭제 (이름, 가격, 이미지 URL)
  - [x] 이름은 공백 포함 15자 이하, 한글, 영문, 숫자, 공백, `( ) [ ] + - & / _`만 허용
  - [x] 이름에 비속어 포함 불가 ([PurgoMalum](https://www.purgomalum.com/) API로 검사)
- [x] **회원** 이메일과 비밀번호로 가입, 가입과 로그인 시 토큰(JWT) 발급
  - [x] 이메일 형식 검사, 대소문자 구분 안 함(소문자로 저장)
  - [x] 비밀번호 8자 이상, BCrypt 해시로 저장
- [x] **위시 리스트** 상품 추가, 목록 조회, 삭제 (Bearer 토큰 인증 필요)

### 명세에 없어서 직접 정한 규칙
| 상황 | 결정 |
|---|---|
| 이미 가입된 이메일, 이미 담은 상품 | 409 (DB 유니크 제약 위반도 같은 409) |
| 로그인 시 없는 이메일 / 틀린 비밀번호 | 같은 메시지로 401 (가입 여부를 숨김) |
| 다른 회원의 위시 삭제 | 없는 위시와 같은 404 (존재 여부를 숨김) |
| 위시에 담긴 상품 삭제 | 위시도 함께 삭제 (`ON DELETE CASCADE`) |
| 비속어 검사 API 장애 | 503 (검사를 건너뛰지 않고 등록을 막음) |
| 인증 범위 | `/api/wishes/**`만 |

토큰 응답은 `{"accessToken", "tokenType": "Bearer", "expiresIn"(초)}`, 오류 응답은 `ProblemDetail` 형식이다.

## 구현 전략

1. **도메인 모델(엔티티)을 직접 설계하고 구현한다.** 검증 규칙은 팩토리 메서드(`of`)에 두고 도메인 단위 테스트로 고정한다.
2. **그 이후 슬라이스 테스트로 바깥 계층을 쌓는다.** Repository는 `@DataJpaTest`, Controller는 `@WebMvcTest`, Service(오케스트레이션)는 협력 객체를 Mockito로 대체한다.
3. 실패하는 테스트를 먼저 쓰고(레드) 통과시킨 뒤(그린) 정리하며, 작은 단위로 커밋한다.

```
shopping
├── entity       도메인 모델, 도메인이 쓰는 인터페이스(PasswordEncoder)
├── repository   Spring Data JPA
├── service      유스케이스, JwtProvider 인터페이스
├── controller   HTTP 입출력
├── dto          요청, 응답 DTO
├── exception    예외, GlobalExceptionHandler
├── external     외부 API 연동 (PurgoMalum)
├── security     JWT, BCrypt 어댑터, 인증 인터셉터
└── config       인터셉터 등록
```

**설계 판단**
- **인터페이스는 사용처 옆에 둔다.** `PasswordEncoder`는 `Member`가 쓰므로 `entity`에, `JwtProvider`는 `MemberService`가 쓰므로 `service`에 두고 구현은 `security`에 둔다. `ProfanityChecker`만 구현체가 하나뿐인 규모라 구현체와 같은 `external`에 둔다.
- **엔티티가 도메인 모델을 겸한다.** 이 규모에서 분리하면 변환 코드만 늘어나는 오버엔지니어링이라고 판단했다. 대신 응답에는 DTO를 쓰고, `equals`는 id 기반으로 직접 작성했다.
- **스키마는 Flyway가 관리한다.** 유니크, 외래키 제약도 마이그레이션 SQL에 직접 쓴다.
- **인증은 Spring Security 없이** jjwt와 `HandlerInterceptor`로 처리한다. 인터셉터가 회원 id를 요청 속성에 담고 컨트롤러가 서비스에 넘긴다.
- **조회는** 위시 목록에 `@EntityGraph`를 써서 N+1을 피하고, `open-in-view=false`로 지연 로딩에 기대지 않는다.

## 실행

```
./gradlew clean test
./gradlew bootRun
```

환경 변수 `JWT_SECRET`(32바이트 이상)으로 서명 키를 반드시 덮어쓴다. 기본값은 개발용이다. 비속어 API 주소는 `PURGOMALUM_BASE_URL`로 바꿀 수 있다.

## AI 도구 활용

### 활용한 방식
- Claude Code와 설계를 논의하고(패키지 구조, 인터페이스 위치, 인증 방식), 장단점을 듣고 직접 선택했다.
- TDD 레드 단계의 테스트 초안, 구현 코드 리뷰, 개념 질문(Flyway, OSIV, 슬라이스 테스트, Mockito)에 활용했다.

### 코드를 어떻게 수정했는가
- `PasswordEncoder`를 서비스 패키지에 두었다가 `entity`와의 순환 의존을 확인해 도메인 쪽으로 옮겼다.
- 가입 응답의 토큰을 의심하고 명세를 확인해 유지했고, 로그인을 `AuthService`로 나누려던 계획은 과하다 판단해 `MemberService`로 합쳤다.
- 이메일 중복을 409로 구분하고, 토큰 응답을 `accessToken`, `tokenType`, `expiresIn`으로 확장했다.
- Lombok `@EqualsAndHashCode`가 id 기반 동등성 테스트를 통과시키지 못해 `equals/hashCode`를 직접 작성했다.
- 위시 응답이 엔티티를 그대로 담아 프록시 내부 필드가 노출되고 N+1이 발생한다는 것을 AI가 실제 호출로 확인해 알려 주었다. 이를 이해한 뒤 DTO 변환과 `@EntityGraph`로 수정하도록 요청해 반영했다.
- AI가 작성한 컨트롤러 테스트에 Mockito 스텁 설정 오류(`given(...)` 안에서 다른 목 호출)가 있었고, 인터셉터를 추가하자 기존 `@WebMvcTest`가 깨졌다(슬라이스가 `HandlerInterceptor` 빈을 로딩해 `JwtProvider`가 필요해짐). 원인은 AI의 설명으로 이해했고, 수정(스텁 순서 조정, `@MockitoBean JwtProvider` 추가)을 반영했다.

### 학습한 점

> 결과적으로 개인이 아는 만큼 질문을 하고 선택하여 결과물을 만들 수 있다는 점에서 지속적인 학습의 중요성을 깨달음

- TDD 사이클에 기반한 도메인 설계와 계층 설계
- Spring Security를 사용하지 않고, 인터셉터 방식으로 JWT를 검증하는 방식을 배움
- Mockito 의 ArgumentCaptor, InOrder, verifyNoMoreInteractions에 대해 배움
- @DataJpaTest 슬라이스가 로딩하는 범위(@Component 제외, @Import 필요)와 Flyway가 있을 때 ddl-auto 동작
- 위시 리스트 조회시 N+1과 @EntityGraph
