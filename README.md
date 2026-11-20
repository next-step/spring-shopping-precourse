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
회원 가입 `POST /api/members/register`
- 가입 -> 201 (Created)
- 유효성 실패 -> 400 (BadRequest)
- 이메일 중복 -> 409 (Conflict)

로그인 `POST /api/members/login`
- 로그인 -> 200(OK)
- 유효성 실패 -> 400 (BadRequest)
- 이메일/비번 불일치 -> 401 (Unauthorized)

### 상품 Product
상품 생성 `POST /api/products` 
- 생성 -> 201 (Created)
- 유효성 검증 실패 -> 400 (BadRequest)
- 상품이 이미 있음 -> 409 (Conflict)

상품 단건 조회 `GET /api/products/{productId}` 
- 성공 -> 200 (OK)
- 상품없음 -> 404 (NotFound)

상품 수정 `PUT /api/products/{productId}`
- 성공 -> 200(OK) + 수정된 값 리턴
- 유효성 검증 실패 -> 400 (BadRequest)
- 대상 없음 -> 404 (NotFound)
- 값 충돌 -> 409 (Conflict)

상품 삭제 `DELETE /api/products/{productId}`
- 삭제 성공 -> 200 (OK)
- 유효성 검증 실패 -> 400 (BadRequest)
- 대상 없음 -> 404 (NotFound)

상품 목록 조회 `GET /api/products`
- 성공 -> 200 (OK)

### 위시리스트 API
위시 리스트 상품 추가 `POST /api/wishes`
- 생성 -> 201 (Created)
- 인증실패 -> 401 (Unauthorized)
- 상품 없음 -> 404 (NotFound)
- 이미 존재 -> 409 (Conflict)

위시 리스트 상품 삭제 `DELETE /api/wishes/{wishId}`
- 삭제 -> 200 (OK)
- 인증실패 -> 401 (Unauthorized)
- 권한 없음 -> 403 (Forbidden)
- 대상없음 -> 404 (NotFound)

위시 리스트 상품 조회 `GET /api/wishes`
- 성공 -> 200 (OK)
- 인증실패 -> 401 (Unauthorized)

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


## AI 활용
기본적으로 코드 생성은 하지 않고, 검색 및 학습 도우미 역할로 사용했다.
Member 도메인과 Product 엔티티·테스트는 직접 작성하고 AI에게는 리뷰와 개념 질문만 했다.
마감 당일에는 시간이 부족해 Product의 Service/Controller, PurgoMalum 비속어 검사, 예외 핸들러 정리, Product 관련 테스트를 AI(Claude)에게 작성시켰고, 이를 이 README에 밝혀 둔다.

# 이 아래에 AI의 가이드를 받아 학습한 내용을 정리함

### 도메인별 작업 순서
1. 요구사항 API 정리. 
2. Entity + 검증규칙 | 단위 테스트
3. Repository | @DataJpaTest
4. DTO
5. Service | 서비스 테스트
6. Controller
7. 예외 처리
8. API 인수 테스트 | 전체 흐름 확인

### Member 도메인을 구현하면서 배운 것. 내 언어로 정리
1. spring 에서 Repository, Service, Controller와 같은 스프링 Bean 객체는 싱글톤(하나의 객체가 하나의 클래스를 지님) 패턴을 쓴다. Entity는 그렇지 않다.(Entity는 Bean이 아님)\
2. Entity는 DB와 관련이 크다. 자바와 DB 사이를 연결하는 계약과 비슷한 역할인듯 하다.
3. Repository에는 다양한 것을 넣을 수 있다. H2, mysql, 임시 저장용 메모리 변수.. DB를 조회할 때에 Repository를 의존하여 주입한다. 따라서 DB와 Repository가 호환만 된다면, 어떤 형태의 데이터저장소도 사용 가능하다.
4. JPA 임마는 자바에서 DB를 쉽게 다루게 도와준다.
5. Controller는 백엔드 최전선에서 사용자의 request를 서비스에 매핑해준다.
6. Service는 컨트롤러의 응답을 받아 내부 로직을 처리한다.
7. Repository는 DB와 같은 저장소에 값을 읽기, 쓰기, 수정, 삭제 등을 한다.
8. Test는 가능한 케이스에 맞게 나누어 작성한다. 테스트에는 @Trasactinal을 붙혀주는데, 이걸 붙히면 Test 종료 후 Repository를 롤백한다. 이렇게 되면 독립실행이 가능해진다.
9. Error는 ErrorCode를 이용해 관리하는 것이 좋다. 도메인별로 예외를 처리하고, 공용예외를 두고, @Valid 등에 의한 스프링 오류는 별도로 만들고, 예외처리되지 않은 오류는 500으로 처리할 수 있는 것도 설정해야한다.
10. JWT 토큰으로 인증하기 위해서는 JWTProvider를 이용한다. 해당 라이브러리를 사용하면 jwtToken과 refresh 토큰을 쉽게 생성하고 관리할 수 있다.
11. 비밀번호는 BCrypto 라이브러리를 통해 PasswordEncoder를 만들어 관리한다. 비밀번호는 절대 String 그대로 비교하지 않고, PasswordEncoder를 이용해 비교한다.
12. 로그인 성공 여부를 테스트할 때에는, 유저를 먼저 회원가입 시키고 동일한 계정으로 로그인 시도를 한다. 이때 회원가입을 했을 때에 Repository에 저장된 id와 응답으로 주어진 토큰을 jwtToken에 넣어 id로 반환한 값을 비교해 로그인 성공 여부를 판단한다.
13. 비밀번호 틀림, 성공 여부는 Service에서 관리한다. controller는 결과만 받는다. 다른 로그인 로직도 controller는 request와 response만 받는다. 내부 작동은 Service의 일이다.
14. Repository는 Requset와 Response가 어떻게 생긴지 모른다. controller나 Test의 호출에 의해서 사용된다.
15. Lombok은 @Getter, @Setter 같은 어노테이션을 제공해 반복되는 타이핑을 줄여주는 유용한 의존성? 라이브러리?이다.

### 클로드의 피드백을 추가한 버전
### 구조와 역할
- **Bean 싱글톤**: 스프링 컨테이너가 Bean(Controller, Service, Repository) 클래스당 객체를 하나만 만들어 공유한다. Entity는 Bean이 아니라 `new`로 만들며, 행 하나당 객체 하나다.
- **Entity**: 자바 객체와 DB 테이블 행을 이어 주는 매핑이다.
- **JPA**: 표준 규격이고 구현체는 Hibernate다. Spring Data JPA가 `Repository` 인터페이스만으로 SQL을 만들어 준다.
- **Repository**: Service가 호출하며, 호환되는 저장소(JPA 지원 DB)로 교체할 수 있다. 메모리 저장은 직접 구현해야 한다.
- **Controller**: 요청을 받아 `@Valid`로 형식을 검증하고 Service에 넘긴다.
- **Service**: 컨트롤러가 넘긴 데이터로 비즈니스 로직을 처리한다. 로그인 성공/실패 판단도 여기서 한다.
- 의존 방향은 `Controller → Service → Repository` 한 방향이다.

### 예외 처리
- 도메인마다 예외 클래스를 만들지 않고 **공용 `BusinessException` + `ErrorCode`**로 구분한다.
- `GlobalExceptionHandler`가 ErrorCode를 HTTP 상태 코드와 `ErrorResponse(code, message)`로 변환한다.
- `@Valid` 실패(`MethodArgumentNotValidException`)는 400으로 따로 처리하고, 예상 못 한 예외는 500으로 처리하되 서버에서 `log.error`로 남긴다.
- 로그인 실패는 이메일 없음/비밀번호 틀림 모두 `AUTHENTICATION_FAILED`(401) 하나로 통일한다(사용자 열거 방지).

### 인증 / 보안
- `JwtProvider`는 직접 만든 클래스이고, 라이브러리는 jjwt다. 현재는 access 토큰만 구현했다(refresh 토큰 없음).
- JWT는 서명만 되고 암호화되지 않으므로 비밀 정보를 넣지 않는다.
- 비밀번호는 `BCryptPasswordEncoder`로 `encode`해 저장하고, 비교는 `matches(평문, 해시)`로 한다. 같은 비밀번호도 매번 해시가 달라(salt) `equals`로는 비교할 수 없다.

### 테스트
- 케이스(성공/실패)별로 테스트를 나눠 작성한다.
- `@Transactional`을 붙이면 테스트 종료 후 그 테스트의 DB 변경이 롤백되어 독립 실행이 된다(id 증가값은 되돌아가지 않는다).
- 로그인 테스트는 먼저 가입시킨 뒤 같은 계정으로 로그인한다. 성공은 **200 + 토큰 존재**로 판단하고, 토큰 속 id와 DB의 회원 id 비교는 추가 검증이다.
- 서비스 테스트는 로직(예외 타입, errorCode)을, MockMvc 테스트는 API 명세(상태 코드, JSON)를 검증한다. 보는 층이 다르다.
- JSON 키는 대소문자를 구분한다. `Code`와 `code`는 다르다.

### Lombok
- 컴파일 시 getter, 생성자, 로그 객체 등을 자동 생성하는 라이브러리(의존성)다.
- 우리는 `@Getter`, `@RequiredArgsConstructor`, `@Slf4j`를 주로 쓰며, Entity에는 `@Setter`를 붙이지 않는다(도메인 규칙 보호).