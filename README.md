# spring-shopping-precourse

## 구현할 기능 목록

### 상품

- [x] 상품을 생성한다. (`POST /api/products`)
- [x] 특정 상품을 조회한다. (`GET /api/products/{productId}`)
- [x] 모든 상품의 목록을 조회한다. (`GET /api/products`)
- [x] 기존 상품의 정보를 수정한다. (`PUT /api/products/{productId}`)
- [x] 특정 상품을 삭제한다. (`DELETE /api/products/{productId}`)
- [x] 상품은 이름, 가격, 이미지 URL을 가진다.
- [x] 상품 이미지는 파일 업로드 없이 URL로 입력받는다.
- [x] 존재하지 않는 상품을 조회·수정·삭제하면 오류 응답을 반환한다.

### 상품 유효성 검사

- [x] 상품 이름은 비어 있을 수 없다.
- [x] 상품 이름은 공백을 포함해 최대 15자까지 입력할 수 있다.
- [x] 상품 이름에는 `( )`, `[ ]`, `+`, `-`, `&`, `/`, `_` 외의 특수 문자를 사용할 수 없다.
- [x] 상품 이름에 비속어가 포함되어 있는지 PurgoMalum API로 검사한다.
- [x] 상품 가격은 0 이상이어야 한다.
- [x] 상품 이미지 URL은 비어 있을 수 없다.
- [x] 상품을 생성하거나 수정할 때 값이 잘못되면 오류 메시지가 담긴 오류 응답을 반환한다.

### 회원

- [x] 이메일과 비밀번호로 회원 가입을 하고 토큰을 받는다. (`POST /api/members/register`)
- [x] 이메일과 비밀번호로 로그인하고 토큰을 받는다. (`POST /api/members/login`)
- [x] 이메일과 비밀번호는 비어 있을 수 없고, 이메일은 올바른 형식이어야 한다.
- [x] 이미 가입된 이메일로는 가입할 수 없다.
- [x] 이메일이 존재하지 않거나 비밀번호가 일치하지 않으면 로그인에 실패한다.
- [x] 회원 인증 토큰을 생성한다.
- [x] 요청 헤더의 토큰을 검증해 인증된 회원을 확인한다.
- [x] 토큰이 없거나 올바르지 않으면 인증 오류 응답을 반환한다.

### 위시 리스트

- [x] 인증된 회원의 위시 리스트에 상품을 추가한다. (`POST /api/wishes`)
- [x] 인증된 회원의 위시 리스트에 있는 상품 목록을 조회한다. (`GET /api/wishes`)
- [x] 인증된 회원의 위시 리스트에서 상품을 삭제한다. (`DELETE /api/wishes/{wishId}`)
- [x] 존재하지 않는 상품은 위시 리스트에 추가할 수 없다.
- [x] 이미 위시 리스트에 있는 상품은 중복으로 추가할 수 없다.
- [x] 다른 회원의 위시 리스트 항목은 삭제할 수 없다.

### 예외 처리

- [x] 모든 오류 응답은 JSON 형식으로 반환한다.

### 구현 순서

1. [x] 상품 값 객체
    - [x] 상품 이름 (`ProductName`): 비어 있음, 15자 초과, 허용되지 않은 특수 문자 검증
    - [x] 상품 가격 (`Price`): 0 이상 검증
    - [x] 상품 이미지 URL (`ImageUrl`): 비어 있음, http/https 형식 검증
2. [x] 상품 도메인과 저장
    - [x] 상품 엔티티 (`Product`)와 테이블 매핑 (Flyway `V1__create_products.sql`)
    - [x] 상품 저장소 (`ProductRepository`)와 서비스 (`ProductService`)
3. [x] 상품 CRUD API (`ProductController`, `products.http`)
4. [x] 공통 예외 처리
    - [x] JSON 오류 응답 (`ErrorResponse`)과 상태 코드를 가진 예외 (`ShoppingException` 하위 400/401/403/404/409)
    - [x] 전역 예외 처리기 (`GlobalExceptionHandler`)와 상품 API 적용 (없는 상품 404, 잘못된 값 400)
5. [x] 상품 API 테스트 (`ApiTest`, `ProductApiTest`)
6. [x] 상품 이름 비속어 검사
    - [x] PurgoMalum 비속어 검사기 (`ProfanityChecker`, `PurgoMalumProfanityChecker`)
    - [x] 상품 생성·수정에 비속어 검사 적용, 테스트용 가짜 검사기 (`FakeProfanityCheckerConfig`)
7. [x] JWT 토큰 생성과 검증 (`TokenProvider`, `JwtTokenProvider`)
8. [x] 회원 가입과 로그인 (`Member`, `MemberService`, `MemberController`, BCrypt 비밀번호 암호화, `V2__create_members.sql`)
9. [x] 로그인 인증 처리 (`@LoginMember`, `LoginMemberArgumentResolver`)
10. [x] 위시 리스트 API (`Wish`, `WishService`, `WishController`, `V3__create_wishes.sql`)


소감

일단 처음 프리코스 PDF 를 봤을때 어떻게 시작해야할지 감도 오지 않았다. 
그래서 일단 PDF 내용을 천천히 읽어보고 난 후에 구현할 우선순위를 AI를 이용해서 정리했다. 
정리후에 AI 에게 코드를 어떻게 만들면 좋을지 물어보면서 어떤 방식으로 기능을 구현하는지 
손으로 따라서 코딩해보며 이해하려고 노력했다. 
테스트는 항상 AI에게 만들어달라고 하면 뚝딱 만들어 줬는데 직접 치려니까 생각보다 어려웠다.
그래도 하다보니 점점 익숙해지는것을 느꼈던것 같다. 
다른 언어를 공부할때에도 이런방식을 시도해 보면 좋을 것 같다. 