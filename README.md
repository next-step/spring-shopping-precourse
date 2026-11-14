# spring-shopping-precourse

## 프로젝트 구조

### 계층구조
Controller -> Service -> Repository -> DB

### 도메인별 폴더 나누기
- member
- product
- wishlist
- auth
- global

### 도메인 별 폴더 구조
- controller
- dto
- entity
- repository
- service

## API 명세서
### 회원 API
회원 가입 `Post api/members/register` \
로그인 `Post api/members/login` \


### 상품 Product
상품 생성 `Post /api/products` \
상품 단건 조회 `Get /api/products/{productId}` \
상품 수정 `Put /api/products/{productId}`\
상품 삭제 `DELETE /api/products/{productId}`\
상품 목록 조회 `GET /api/products`

### 위시리스트 API
위시 리스트 상품 추가 `POST /api/wishes`\
위시 리스트 상품 삭제 `DELETE /api/wishes/{wishId}`\
위시 리스트 상품 조회 `GET /api/wishes`\

## 기능구현
### 상품
상품을 조회 추가 수정 삭제하는 기능을 구현한다.
- 상품에는 이름, 가격, 이미지 URL이 포함된다.
- 상품 이미지는 파일 업로드가 아닌 URL 입력 방식으로 관리한다.

#### 유효성 검사 및 예외 처리
상품을 추가하거나 수정할 때 클라이언트로부터 전달된 값의 유효성을 검사하고 잘못된 경우 적절한 오류 응답을 반환한다
- 상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있다.
- 상품 이름에는 아래 특수문자만 허용
  - 허용 : (), [], +, -, &, /, _
  - 그 외 특수문자 사용 불가
- 상품의 이름에는 비속어가 포함될 수 없다.
  - `PurgoMalum` API를 호출하여 비속어 포함 여부를 검사한다.

### 회원 로그인
회원가입과 로그인을 통해 사용자 인증 기능을 구현한다.
- 회원은 이메일과 비밀번호로 가입할 수 있다.
- 로그인 시 이메일과 비밀번호를 검증하고, 일치하면 인증 토큰을 발급한다.
- 토큰 생성방식은 JWT 토큰 방식으로 한다.

### 위시리스트 
인증된 사용자를 대상으로 위시 리스트 기능을 구현한다.
- 위시 리스트에 등록된 상품 목록을 조회할 수 있다.
- 위시 리스트에 상품을 추가할 수 있다.
- 위시 리스트에서 상품을 삭제할 수 있다.

## 테스트
- JUnit5로 테스트를 실행하고, AssertJ로 검증한다.
- 상품 이름 유효성 검사, JWT 발급/검증은 단위 테스트로 작성한다.
- 회원/상품/위시리스트 API는 통합 테스트로 작성한다.

## AI 활용.
코드 생성은 하지 않고, 검색 및 학습 도우미 역할로 사용.
코드는 구현 중 막히는 부분이 있을 때에 예제를 보여달라는 식으로 사용 예정.


