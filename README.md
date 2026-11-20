# spring-shopping-precourse

## 구현할 기능 목록

### 1. 상품 (Product)
- [x] 상품 생성 `POST /api/products`
- [x] 상품 단건 조회 `GET /api/products/{productId}`
- [x] 상품 목록 조회 `GET /api/products`
- [x] 상품 수정 `PUT /api/products/{productId}`
- [x] 상품 삭제 `DELETE /api/products/{productId}`
- [x] 존재하지 않는 상품을 조회·수정·삭제하면 404 응답

### 2. 상품 유효성 검사
상품 추가·수정 시 아래를 검사하고, 잘못되면 400 응답과 오류 메시지를 반환한다.
- [x] 이름이 비어 있으면 예외
- [x] 이름이 공백 포함 15자를 넘으면 예외
- [x] 이름에 허용되지 않은 특수 문자가 있으면 예외
  - 허용: `( ) [ ] + - & / _`
- [x] 이름에 비속어가 있으면 예외 (PurgoMalum API로 검사)
- [x] 가격이 0 이하면 예외
- [x] 이미지 URL이 비어 있으면 예외

### 3. 회원 (Member)
- [x] 회원 가입 `POST /api/members/register` → 토큰 발급
  - [x] 이메일 형식이 올바르지 않으면 400
  - [x] 비밀번호가 비어 있으면 400
  - [x] 이미 가입된 이메일이면 409
- [x] 로그인 `POST /api/members/login` → 토큰 발급
  - [x] 이메일 또는 비밀번호가 일치하지 않으면 401

### 4. 인증
- [x] 요청 헤더 `Authorization: Bearer {토큰}`으로 회원을 식별
- [x] 토큰이 없거나 유효하지 않으면 401
- [x] 토큰이 만료되었으면 401 추가

### 5. 위시 리스트 (Wish) — 로그인한 회원만 사용
- [x] 위시 리스트에 상품 추가 `POST /api/wishes`
  - [x] 존재하지 않는 상품이면 404
  - [x] 이미 담긴 상품이면 409
- [x] 위시 리스트 조회 `GET /api/wishes`
- [x] 위시 리스트에서 상품 삭제 `DELETE /api/wishes/{wishId}`
  - [x] 다른 회원의 위시를 삭제하려 하면 403
  - [x] 존재하지 않는 위시면 404

---

## 구현 전략

### 계층 구조
요청 처리 흐름을 역할별로 나눈다.

Controller → Service → Repository → DB

| 계층 | 역할 |
|---|---|
| Controller | 요청을 받고 응답 형식(JSON, 상태 코드)을 정한다 |
| Service | 비즈니스 로직 (중복 확인, 권한 확인 등) |
| Repository | 데이터 저장과 조회 |
| Domain | 상품·회원·위시 객체와 값 검증 규칙 |

### 패키지 구성
    shopping
    ├── product   (상품)
    ├── member    (회원, 로그인)
    ├── wish      (위시 리스트)
    ├── auth (JWT 발급, 인증 처리)
    └── common    (공통 예외 처리)

### 유효성 검사는 도메인 객체가 직접 한다
- 상품 이름을 `ProductName` 같은 값 객체로 감싸고, 생성자에서 길이·특수 문자 규칙을 검사한다.
- 이렇게 하면 검사 규칙이 한곳에 모이고, 스프링 없이 단위 테스트를 할 수 있다.
- 비속어 검사는 외부 API를 호출해야 하므로 `ProfanityChecker` 인터페이스로 분리한다.
  - 실제 구현: PurgoMalum API 호출
  - 테스트: 실제 PurgoMalum API를 호출해 비속어 판정과 서비스 동작을 확인한다 (인터넷 연결 필요)

### 인증 방식 (JWT)
- 가입·로그인에 성공하면 회원 ID를 담은 JWT를 발급한다.
- JWT는 서버에 저장하지 않고, 요청마다 서명과 만료 시간을 검증해 회원을 식별한다.
- 서명용 비밀키와 만료 시간은 코드에 직접 쓰지 않고 `application.properties`에서 읽어 온다.
- JWT 생성·검증은 `JwtTokenProvider` 한 클래스가 담당한다.
- 토큰 확인 로직은 컨트롤러마다 반복하지 않고 `LoginMemberArgumentResolver`에서 한 번에 처리한다.
- 컨트롤러에서는 `@LoginMember Member member` 형태로 로그인한 회원을 바로 받는다.
- 비밀번호는 평문과 비교하지 않도록 저장 방식을 고려한다.

### 예외 처리
- 상황별로 예외 클래스를 만들고, `@RestControllerAdvice`에서 한 번에 상태 코드와 메시지로 바꾼다.
- 오류 응답 형식을 통일한다.

    { "message": "상품 이름은 15자를 넘을 수 없습니다." }

| 상태 코드 | 상황 |
|---|---|
| 400 | 입력값이 잘못됨 |
| 401 | 로그인 실패, 토큰 없음/잘못됨/만료됨 |
| 403 | 남의 위시를 삭제하려 함 |
| 404 | 상품·위시가 없음 |
| 409 | 이메일 중복, 위시 중복 |

### 테스트 전략 (JUnit 5 + AssertJ)
- 도메인 단위 테스트: 상품 이름 길이, 특수 문자, 가격 검증
  - 여러 입력값은 `@ParameterizedTest`로 한 번에 검사
  - 예외는 `assertThatThrownBy`로 검사
- JWT 테스트: 발급한 토큰에서 회원 ID를 다시 꺼낼 수 있는지, 위조·만료 토큰은 거부되는지
- 서비스 테스트: 중복 이메일, 중복 위시, 권한 없는 삭제
- API 테스트: 실제 요청을 보내 상태 코드와 응답 JSON 확인

### 구현 순서
1. 상품 CRUD
2. 상품 유효성 검사 (길이 → 특수 문자 → 비속어)
3. 공통 예외 처리
4. 회원 가입·로그인과 JWT 발급
5. 인증 처리 (토큰 검증, @LoginMember)
6. 위시 리스트

---

## AI 활용 기록

| 활용한 부분 | 활용 방식 | 직접 수정한 내용 | 배운 점 |
|---|---|---|---|
| README 기능 목록 초안 | 과제 요구 사항을 주고 초안 작성을 요청 | 올바르지 않은 형식과 잘못된 계획 부분 수정 | 자바와 백엔드에 대해 무지한 부분이 많은데 초안 작성한 글을 보고 대략적으로 쇼핑 사이트가 돌아가는 형식을 배움 |
| 토큰 방식 선택 | JWT와 랜덤 토큰의 차이 설명을 요청 | 수정 사항 없음 | JWT의 개념과 랜덤 토큰 차이점 배움 |
| 1단계 계층 구조 이해 | Controller·Service·Repository·Entity·DTO의 역할 설명을 요청하고, 과제와 무관한 예시(Book, Dog 등) 코드를 받아 참고 | 예시를 보고 Product 관련 6개 파일을 직접 작성하고, 작성한 코드가 올바른지 체크 후 올바르지 않은 부분 수정 | 요청이 Controller → Service → Repository 순서로 흐른다는 것과 DTO를 따로 두는 이유를 배움 |
| product 테이블 SQL 작성 | 다른 예시(Dog)로 `CREATE TABLE` 형식을 배운 뒤, Product용 SQL 정답을 받아 직접 타이핑하고 서버를 재시작해 마이그레이션 적용을 확인 | 예시를 보고 직접 코드 작성 이후, 이해되지 않은 부분은 정답지를 받아 타이핑| SQL 자료형과 자바 타입의 대응, 자바 `imageUrl`이 DB에서 `image_url`로 바뀌는 이유를 배움 |
| API 동작 확인 | `.http` 파일로 요청 보내는 방법과 결과 해석을 질문 | 수정 사항 없음 | 201·200·500 상태 코드의 의미와, 없는 상품 조회가 지금은 500인 이유(예외 처리 전)를 배움 |
| 2단계 수정·삭제·목록 이해 | Dog 예시 코드를 한 줄씩, Java·Spring 기본 제공 기능과 직접 만든 부분을 구분해 설명해 달라고 요청 | 예시를 보고 Product의 수정·삭제·목록 메서드를 직접 작성하고, 리뷰받은 부분(update 인자 3개, 예외 메시지 통일, 변수 이름)을 직접 수정 | `@Transactional`과 변경 감지로 `save()` 없이 수정이 저장되는 것, 삭제 성공 시 204를 돌려주는 이유, `stream().map().toList()`가 파이썬 리스트 컴프리헨션과 같다는 것을 배움 |
| 컴파일 오류 해결 | `cannot find symbol` 오류 화면을 보여 주고 원인을 질문 | 빠진 import(`PutMapping`, `DeleteMapping`, `List`)를 직접 추가하고, IntelliJ가 자동으로 만든 `*` import를 풀어서 정리 | import가 없으면 클래스를 찾지 못한다는 것과, Google Style에서 `*` import를 쓰지 않는 이유를 배움 |
| 2단계 API 동작 확인 | 추가할 `.http` 요청과 기대 상태 코드를 요청 | 요청을 직접 실행하고, 두 번째 삭제가 500이 나온 이유를 요청 기록으로 확인 | 204는 응답 내용이 없다는 것과, 이미 삭제한 상품을 다시 지우면 오류가 나는 이유를 배움 |
| 상품 이름 검사(ProductName) | DogName 예시로 값 객체, 정규식, `throw`, early return 설명을 요청하고, 내장 기능과 직접 만든 부분을 구분해 달라고 요청 | 예시를 보고 ProductName을 직접 작성하고, 리뷰받은 부분(생성자 이름, `checkLength` 매개변수 타입)을 직접 수정. 과제 특수문자 정규식은 정답을 받아 반영 | 값 객체가 생성될 때 규칙을 검사하면 잘못된 값이 아예 만들어지지 않는다는 것, 정규식에서 `[ ]`는 `\\`로 감싸야 하고 `-`는 맨 끝에 둬야 한다는 것을 배움 |
| 첫 단위 테스트 작성 | DogNameTest 예시로 JUnit5·AssertJ 사용법과 영어 메서드 이름 + `@DisplayName` 방식을 질문 | 예시를 보고 ProductNameTest를 직접 작성하고, 리뷰받은 부분(메서드 이름 오타, 경계값 16자, 허용 특수문자 통과 테스트 추가)을 수정 | 테스트로 서버 없이 규칙을 자동 확인할 수 있다는 것, 예외 테스트는 람다로 감싸야 한다는 것, 경계값(15/16자)과 통과하는 경우도 시험해야 한다는 것을 배움 |
| Product에 이름 검사 연결 | Dog 예시로 생성자에서 값 객체를 거쳐 저장하는 방법을 질문하고, 서버 연결 오류(`Connection refused`) 원인을 질문 | 생성자와 update 두 곳을 직접 수정하고, 서버를 켠 뒤 잘못된 이름이 막히는지 직접 확인 | 생성자와 수정 메서드 둘 다 검사해야 구멍이 없다는 것과, `Connection refused`는 서버가 꺼져 있다는 뜻임을 배움 |
| 가격·이미지 URL 검사 | DogAge·DogPhoto 예시로 값 객체 만드는 방법과 한 줄씩 설명을 요청하고, 가격 0 허용 여부를 질문 | ProductPrice, ProductImageUrl을 직접 작성하고 Product에 연결. 검사 메서드 이름을 클래스 이름과 같게 써서 난 오류와, 이름 바꾸기(Shift+F6)로 다른 파일까지 바뀐 문제를 직접 수정 | `int`는 값이 없으면 0이 들어가서 `0 이하`로 막아야 한다는 것, 생성자 이름은 클래스 이름 전용이라는 것, Shift+F6는 사용하는 모든 곳을 바꾼다는 것을 배움 |
| 비속어 검사 인터페이스 | 외부 API를 바로 호출하지 않고 인터페이스로 나누는 이유를 질문하고, BadWordChecker 예시로 `interface` 문법 설명을 요청 | 예시를 보고 ProfanityChecker를 직접 작성. 처음에 Class로 만든 것을 `interface`로 직접 고침 | 인터페이스는 "할 일 약속"이고, 실제 구현을 진짜/가짜로 바꿔 끼울 수 있어서 테스트가 인터넷에 의존하지 않게 된다는 것을 배움 |
| PurgoMalum API 호출 | RealBadWordChecker 예시로 `implements`, `@Override`, `@Component`, `RestClient` 설명을 요청 | 예시를 보고 PurgoMalumProfanityChecker를 직접 작성하고, 브라우저로 API 응답(`true`/`false`)을 직접 확인 | `RestClient`로 외부 API를 부르는 흐름(get → uri → retrieve → body)과, `{text}` 자리 표시로 주소를 안전하게 만드는 방법을 배움 |
| Service에 비속어 검사 연결 | ProductService 수정 정답과 바뀐 부분 설명을 요청하고, 비속어가 통과된 원인을 질문 | 정답을 보고 생성자 주입과 검사 메서드를 직접 반영. 테스트 단어가 PurgoMalum 목록에 없어서 통과된 것을 브라우저로 확인하고 단어를 바꿔 다시 테스트 | 생성자에 인터페이스 타입으로 받으면 Spring이 구현체를 자동으로 넣어 준다는 것, 외부 API 결과는 직접 확인해 봐야 한다는 것을 배움 |
| 공통 예외 처리 (NotFoundException, GlobalExceptionHandler) | NoMemoException·ErrorDesk 예시로 `extends`, `super()`, `record`, `@RestControllerAdvice`, `@ExceptionHandler` 설명을 요청. 뼈대만 받아 채우는 방식은 어려워서 예시를 보고 고치는 방식으로 진행 | 예시를 보고 두 파일을 직접 작성하고, 리뷰받은 부분(`RuntimeException` 오타, 영수증 대신 오류를 담은 `body`, 400 메서드의 매개변수 타입)을 직접 수정. ProductService의 없음 오류 세 곳을 직접 교체 | 오류 종류를 나눠야 상태 코드를 다르게 줄 수 있다는 것, `super(message)`로 넘긴 문장을 `getMessage()`로 꺼낸다는 것, 오류를 한 곳에서 모아 처리하면 컨트롤러가 깔끔해진다는 것을 배움 |
| 4단계 API 동작 확인 | 서버 재시작부터 `.http` 1~17번 예상 상태 코드까지 순서대로 안내를 요청 | 직접 실행해 500이던 요청이 404·400으로 바뀐 것을 확인 | 삭제한 상품을 수정하면 값 검사보다 "없음"이 먼저 걸려 404가 나온다는 것처럼, 검사 순서에 따라 응답이 달라진다는 것을 배움 (+15번은 주의)|
| 회원 저장소와 값 검사 (Member, MemberEmail, MemberPassword) | Reader 예시로 `V2` 테이블, Entity, `findByEmail`·`existsByEmail` 이름 규칙, 이메일 정규식 설명을 요청하고 `final`·`static`·상수 이름 규칙을 질문 | 예시를 보고 직접 작성하고, 리뷰받은 부분(`existsMyEmail` 오타, `Optional` import, 한 줄 getter, `public` 검사 메서드)을 직접 수정 | Spring Data는 메서드 이름(`동작+By+칸 이름`)만으로 쿼리를 만든다는 것, `static`은 클래스에 하나만 있고 `final`은 한 번 넣으면 못 바꾼다는 것, 상수는 대문자로 쓴다는 것을 배움 |
| JWT 발급 (JwtTokenProvider) | jjwt 라이브러리 추가 방법과 TicketMaker 예시로 `@Value`, `Keys.hmacShaKeyFor`, `Jwts.builder()`·`parser()` 설명을 요청 | 예시를 보고 직접 작성하고, 리뷰받은 부분(`@Component` 누락, `SecretKey` 오타, properties 이름 `jwt.expiration` 불일치, `Long`→`long`)을 직접 수정 | 비밀키와 만료 시간을 설정 파일에서 읽어 오는 방법, `@Value` 이름이 틀리면 빨간 줄 없이 서버 실행 때 실패한다는 것, 토큰에 회원 번호와 만료 시간을 담고 서명한다는 것을 배움 |
| 회원 가입·로그인 API (MemberService, MemberController) | ReaderService 예시를 파이썬 코드와 비교해 한 줄씩 설명해 달라고 요청하고, Controller는 정답을 받아 반영 | 서비스를 직접 작성하고, 리뷰받은 부분(Alt+Enter로 잘못 생긴 클래스, 칸 이름 대문자, 오류 문장 통일, import)을 수정. `member.http`로 201·409·400·200·401을 직접 확인  | 중복 가입은 409, 로그인 실패는 401이고, 이메일이 없을 때와 비밀번호가 틀릴 때 같은 문장을 써야 가입 여부가 드러나지 않는다는 것, 글자 비교는 `equals`를 쓴다는 것을 배움 |
| 개념 복습 (계층 구조, 생성자 주입, HTTP 메서드, 타입 검사) | MemberController 정답지와 README 계층 표를 조각별로 다시 설명해 달라고 요청하고, 카페(Bean·Barista·Counter) 예시로 생성자 주입, GET·POST·PUT·DELETE 차이, 컴파일러의 타입 검사를 질문 | 수정 사항 없음 | Spring이 생성자를 보고 필요한 객체를 대신 만들어 넣어 준다는 것, 같은 주소라도 요청 방식에 따라 다른 기능이 된다는 것, 클래스를 만들면 곧 새 타입이 되고 컴파일러가 실행 전에 타입을 검사한다는 것을 배움 |
| 로그인 회원 식별 (@LoginMember, LoginMemberArgumentResolver, WebConfig) | VisitorResolver 예시로 `try`/`catch`, 직접 만드는 어노테이션(`@Target`, `@Retention`), `HandlerMethodArgumentResolver`, `WebMvcConfigurer` 설명을 요청 | 예시를 보고 직접 작성하고, 리뷰받은 부분(새 메서드를 따로 만든 것, 클래스 이름으로 메서드 호출, `"Bearer"` 뒤 공백 누락, 100자 넘는 줄)을 직접 수정 | 라이브러리 오류를 우리 예외로 바꿔 401로 보내는 방법, 어노테이션은 이름표일 뿐이고 그걸 읽는 코드가 따로 있어야 한다는 것, 따옴표 안 오타는 컴파일러가 못 잡는다는 것을 배움 |
| 위시 저장소 (V3 SQL, Wish, WishRepository) | Loan 예시로 외래 키(`FOREIGN KEY`, `ON DELETE CASCADE`, `UNIQUE`)와 `@ManyToOne`·`@JoinColumn` 설명을 요청 | 예시를 보고 직접 작성하고, 처음에 토큰을 표에 넣으려 한 SQL과 import 순서를 직접 수정 | 토큰은 DB에 저장하지 않는다는 것, 위시는 회원과 상품을 잇는 표라는 것, `@ManyToOne`으로 다른 Entity를 칸처럼 들고 있을 수 있다는 것을 배움 |
| 위시 기능 (ForbiddenException, DTO, WishService, WishController) | LoanService·LoanController 예시로 주인 검사, `stream().map().toList()`, `@LoginMember` 사용법 설명을 요청 | 예시를 보고 직접 작성하고, 리뷰받은 부분(요청 칸 이름 `productId`, 전부 401로 쓴 예외 종류, 삭제 때 중복 검사를 부른 것, 필드 타입, `@RequestBody`의 `@` 누락, `{wishId}` 누락, 주소 오타)을 직접 수정 | 401(누군지 모름)과 403(남의 것)의 차이, 컨트롤러는 `@LoginMember` 하나로 로그인 회원을 받는다는 것, 주소·JSON 칸 이름 같은 문자열 오타는 컴파일러가 못 잡는다는 것을 배움 |
| 6단계 API 동작 확인 | `wish.http` 요청과 예상 결과표를 요청 | 직접 실행해 401·201·409·404·200·403·204가 모두 예상대로 나오는 것을 확인 | `client.global.set`으로 응답 토큰을 저장해 `{{tokenA}}`로 다시 쓰는 방법, 회원 두 명으로 권한(403)을 시험하는 방법을 배움 |
| 어노테이션 개념 정리 | 자주 쓰는 어노테이션 표와 Spring·어노테이션의 차이를 질문하고, 직접 정리한 표의 검토를 요청 | 정리한 표에서 오타(`@RequestBody` 등)와 `@Repository`·`@Transactional` 설명을 직접 수정 | 어노테이션은 자바 문법(쪽지)이고 Spring은 그걸 읽고 일하는 프레임워크(매니저)라는 것, 어노테이션마다 읽는 주인(자바·Spring·JPA·JUnit·우리 코드)이 다르다는 것을 배움 |
| 값 객체 단위 테스트 (ProductPrice, ProductImageUrl, MemberEmail) | DogAge·DogPhoto·ReaderEmail 테스트 예시와 경계값, `null` 검사 설명을 요청하고, MemberEmailTest는 정답을 받아 반영 | 예시를 보고 직접 작성하고, 리뷰받은 부분(자동완성으로 잘못 고른 `IllegalAccessError`, 빠진 `;`, 다른 패키지에 둔 테스트 파일, import 순서)을 직접 수정한 뒤 `gradlew clean test`로 통과 확인 | 경계값(1원과 0원)을 꼭 시험해야 한다는 것, 테스트는 원래 코드와 같은 패키지에 둬야 한다는 것, 비슷한 이름의 자동완성은 끝까지 확인해야 한다는 것을 배움 |
| 기능 테스트 (JWT, 서비스, API) | 과제 문서와 README 테스트 전략을 대조해 빠진 테스트를 점검해 달라고 요청하고, 테스트 코드는 정답을 받아 개념과 한 줄씩 설명을 요청 | 테스트 파일을 직접 만들어 넣고, 비속어 검사는 가짜 대신 실제 PurgoMalum API를 쓰기로 직접 결정해 README 테스트 전략을 함께 수정 | 만료 시간을 음수로 줘서 만료 토큰을 시험하는 방법, `@SpringBootTest`와 `@Transactional`로 테스트마다 DB를 되돌리는 것, MockMvc로 서버를 켜지 않고 상태 코드와 JSON을 확인하는 방법을 배움 |
| 여러 값 테스트 (@ParameterizedTest) | 값마다 테스트를 따로 만들지 않는 방법을 질문 | ProductNameTest 특수문자 테스트를 바꾸고 MemberPasswordTest를 추가 | `@ValueSource`, `@NullAndEmptySource`로 메서드 하나에 값 여러 개를 넣어 반복 시험하는 방법을 배움 |

---

# 과제 소감

- 아직 1학년 학생이라 자바를 제대로 배워본적이 없어서 과제를 할 때 AI의 많은 도움을 받았습니다. 하지만 AI의 도움을 받았음에도 불구하고 미션을 완수하기까지 많은 시간이 걸렸습니다. 그러나 시간이 많이 걸린만큼 자바에 대해 많은 걸 알 수 있었고, 단기간에 프로그래밍 언어를 이해할 수 있었던 활동이기도 합니다. 특히, 웹 서버가 어떻게 작동하는지 조차 몰랐던 저는 요청이 처리되는 대략적인 원리와 SQL까지 배울 수 있었습니다. 아직까지 AI 없이 혼자의 힘으로만 개발을 해보라고 하면 최소 일주일 이상은 걸릴 것 같기는 합니다. 하지만 이번 미션 활동을 통해 이틀동안 미친듯이 개발에만 몰두 했었는데 그 시간이 짧게 느껴질만큼 몹시 재밌었습니다. 이번 특강이 끝나고 나서, 다음번에는 AI 도움 없이 제 힘으로만 새로 개발해보려고 합니다.