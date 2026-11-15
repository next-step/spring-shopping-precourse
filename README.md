# spring-shopping-precourse

온라인 쇼핑몰을 위한 간단한 HTTP API. 요청과 응답은 JSON으로 주고받는다.

## 기능 목록

### 공통
- [x] 예외를 HTTP 상태 코드와 `{"message": "..."}` 형식의 오류 응답으로 변환한다.
  - 잘못된 입력 값 → 400 Bad Request
  - 인증 실패 → 401 Unauthorized
  - 다른 회원의 리소스 접근 → 403 Forbidden
  - 존재하지 않는 리소스 → 404 Not Found
  - 중복 등록 → 409 Conflict
  - 외부 API(PurgoMalum) 호출 실패 → 503 Service Unavailable

### 상품
- [x] 상품은 이름, 가격, 이미지 URL을 가진다.
- [x] 상품 이름을 검증한다.
  - 비어 있을 수 없다.
  - 공백을 포함하여 최대 15자까지 입력할 수 있다.
  - 특수 문자는 `( ) [ ] + - & / _`만 사용할 수 있다.
  - 비속어를 포함할 수 없다. (PurgoMalum API로 검사)
- [x] 상품 가격은 1원 이상이어야 한다.
- [x] 상품 이미지 URL은 `http://` 또는 `https://`로 시작해야 한다.
- [x] 상품을 생성한다. `POST /api/products`
- [x] 상품 목록을 조회한다. `GET /api/products`
- [x] 특정 상품을 조회한다. `GET /api/products/{productId}`
- [x] 상품을 수정한다. `PUT /api/products/{productId}`
- [x] 상품을 삭제한다. `DELETE /api/products/{productId}`
  - 상품이 삭제되면 그 상품이 담긴 위시 리스트 항목도 함께 삭제된다.

### 회원
- [x] 이메일과 비밀번호로 회원 가입하고 토큰을 받는다. `POST /api/members/register`
  - 이메일 형식이 올바르지 않으면 가입할 수 없다.
  - 비밀번호는 8자 이상이어야 한다.
  - 이미 가입된 이메일로는 가입할 수 없다.
- [x] 이메일과 비밀번호로 로그인하고 토큰을 받는다. `POST /api/members/login`
  - 이메일이 없거나 비밀번호가 틀리면 401을 반환한다.
- [x] 비밀번호는 SHA-256 해시로 저장한다.

### 인증
- [x] `Authorization: Bearer {token}` 헤더로 회원을 식별한다.
- [x] 토큰이 없거나 유효하지 않으면 401을 반환한다.

### 위시 리스트 (인증 필요)
- [x] 위시 리스트에 상품을 추가한다. `POST /api/wishes`
  - 존재하지 않는 상품은 추가할 수 없다.
  - 같은 상품을 두 번 추가할 수 없다.
- [x] 위시 리스트의 상품 목록을 조회한다. `GET /api/wishes`
- [x] 위시 리스트에서 상품을 삭제한다. `DELETE /api/wishes/{wishId}`
  - 다른 회원의 위시 리스트 항목은 삭제할 수 없다.

## 구현 전략

- **패키지를 기능 단위로 나눈다.** `product`, `member`, `auth`, `wish`, `common`
- **검증 규칙은 도메인 객체가 가진다.** `Product`, `Member` 생성자에서 값을 검증해, 스프링 없이 단위 테스트할 수 있게 한다.
- **외부 API는 인터페이스 뒤에 둔다.** `ProfanityChecker` 인터페이스를 두고 실제 구현은 `PurgoMalumProfanityChecker`가 맡는다. 테스트에서는 가짜 구현으로 바꿔 네트워크 없이 검증한다.
- **토큰은 UUID로 발급해 DB에 저장한다.** 로그인할 때마다 새 토큰을 발급하고, 요청의 토큰으로 회원을 찾는다.
- **인증은 `HandlerMethodArgumentResolver`로 처리한다.** 컨트롤러 파라미터에 `@LoginMember Member member`를 붙이면 토큰으로 찾은 회원이 들어온다.
- **스키마는 Flyway 마이그레이션으로 관리한다.** (`src/main/resources/db/migration`)
- **프로그래밍 요구 사항을 지킨다.** `else`, `switch`, 3항 연산자를 쓰지 않고, 메서드는 15줄 이하, 들여쓰기는 2단계 이하로 유지한다.

## API 요약

| 기능 | 요청 | 성공 응답 |
|---|---|---|
| 회원 가입 | `POST /api/members/register` `{"email", "password"}` | 201 `{"token"}` |
| 로그인 | `POST /api/members/login` `{"email", "password"}` | 200 `{"token"}` |
| 상품 생성 | `POST /api/products` `{"name", "price", "imageUrl"}` | 201 상품 |
| 상품 목록 | `GET /api/products` | 200 상품 목록 |
| 상품 조회 | `GET /api/products/{productId}` | 200 상품 |
| 상품 수정 | `PUT /api/products/{productId}` `{"name", "price", "imageUrl"}` | 200 상품 |
| 상품 삭제 | `DELETE /api/products/{productId}` | 204 |
| 위시 추가 | `POST /api/wishes` `{"productId"}` + 토큰 | 201 위시 항목 |
| 위시 목록 | `GET /api/wishes` + 토큰 | 200 위시 항목 목록 |
| 위시 삭제 | `DELETE /api/wishes/{wishId}` + 토큰 | 204 |

## AI 활용

- Claude를 사용해 전체 구현 초안과 기능 단위 커밋을 만들었다.
- (직접 작성) 초안에서 이해하고 수정한 부분:
- (직접 작성) 새로 배운 점:
