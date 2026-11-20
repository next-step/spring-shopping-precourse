# spring-shopping-precourse

온라인 쇼핑몰을 위한 간단한 HTTP API를 스프링 프레임워크로 구현한다. 요청과 응답은 JSON 형식이다.

> 이 문서는 `쇼핑 - 프리코스` 과제 문서에 적힌 내용(**[문서]**)과, 문서에 없어서 직접 정한 내용(**[결정]**)을 구분해서 적는다.
> 개발은 TDD(테스트 작성 → 실패 확인 → 구현 → 리팩터링)로 진행한다.

---

## 1. 프로젝트 현황 (저장소 확인 결과)

- Gradle(Kotlin DSL), Spring Boot 4.1.2, Java toolchain 21
- 의존성: JPA, Flyway(+MySQL), Thymeleaf, Validation, WebMVC, H2(runtime), MySQL(runtime)
- 소스: `src/main/java/shopping/Application.java` 존재, 그 외 패키지는 `.gitkeep`만 있음
- `src/main/resources/db/migration/` 존재 (Flyway 마이그레이션 위치)

## 2. 요구 사항 요약

### 과제 진행 요구 사항 [문서]
- 구현 전에 README.md에 기능 목록과 구현 전략을 작성한다.
- 커밋은 README.md에 정리한 기능 목록 단위로 한다.
- 커밋 메시지는 AngularJS Git Commit Message Conventions를 따른다.
- AI 도구를 활용하면 README.md에 활용 방식, 코드 수정 내용, 학습 내용을 기록한다.
- PR 제목: `[$이름] 프리코스 미션 제출합니다.`
- 제출 전: `java -version`이 21인지 확인, `./gradlew clean test` 전체 통과 확인

### 프로그래밍 요구 사항 [문서]
- Google Java Style Guide, 단 들여쓰기는 4 spaces
- 들여쓰기 depth 2까지만 허용 (3 이상 금지)
- 메서드 길이 15줄 이하, 메서드는 한 가지 일만 수행
- `else` 사용 금지, `switch` 사용 금지, 3항 연산자 사용 금지
- JUnit과 AssertJ로 기능 목록의 동작을 테스트한다.

### API 명세 [문서]

| 구분 | URL | 메서드 | 기능 |
|---|---|---|---|
| 회원 | `/api/members/register` | POST | 회원 가입, 토큰 발급 |
| 회원 | `/api/members/login` | POST | 로그인, 토큰 발급 |
| 상품 | `/api/products` | POST | 상품 생성 |
| 상품 | `/api/products/{productId}` | GET | 상품 조회 |
| 상품 | `/api/products/{productId}` | PUT | 상품 수정 |
| 상품 | `/api/products/{productId}` | DELETE | 상품 삭제 |
| 상품 | `/api/products` | GET | 상품 목록 조회 |
| 위시 리스트 | `/api/wishes` | POST | 위시 리스트에 상품 추가 |
| 위시 리스트 | `/api/wishes/{wishId}` | DELETE | 위시 리스트에서 상품 삭제 |
| 위시 리스트 | `/api/wishes` | GET | 위시 리스트 상품 조회 |

## 3. 결정 사항 (문서에 없는 부분)

문서에는 "기능 요구 사항에 기재되지 않은 내용은 스스로 판단하여 구현한다"고 되어 있다. 아래는 직접 정한 내용이다.

| 항목 | 결정 | 이유 |
|---|---|---|
| 구현 언어 | Java | 문서의 프로그래밍 요구 사항이 Java 코드 컨벤션 기준이고, 기존 `Application.java`가 Java임 (빌드에 Kotlin 플러그인이 있으나 사용하지 않음) |
| 구조 | 도메인 객체는 `shopping.domain` 패키지(`src/main/java/shopping/domain/`)에 둔다. 테스트는 같은 패키지 경로(`src/test/java/shopping/domain/`)에 둔다. controller / service / repository 위치는 API 단계에서 확정 | 도메인 규칙을 다른 계층과 분리하기 위함 |
| 토큰 방식 | 로그인/가입 시 랜덤 UUID 토큰을 발급하고 메모리에 보관해 검증 | 문서에서 "자유롭게 선택"이라고만 함. 추가 의존성 없이 구현 가능 |
| 토큰 전달 | `Authorization: Bearer {token}` 헤더 | 일반적인 관례 |
| 비밀번호 저장 | 메모리에 입력값 그대로 보관한다 (해시 미적용, **미해결**) | 처음에는 "평문 저장 금지, 해시 보관"으로 정했으나, 회원 도메인(`Member`)이 검증된 `Password` 값을 그대로 가지는 구조라 해시하려면 도메인 변경이 필요하다. 영속 저장을 하지 않으므로 우선 보류하고 "14. 회원 API"의 한계로 기록한다 |
| 인증 대상 | 위시 리스트 API만 인증 필요 | 문서가 "인증된 사용자를 대상으로" 위시 리스트를 명시함. 상품 API 인증은 문서에 없음 |
| 이메일 중복 | 이미 가입된 이메일로 가입 시 400 응답 | 문서에 규칙 없음 |
| 이메일/비밀번호 검증 | 이메일 형식 검증, 비밀번호 빈 값 금지 | 문서에 규칙 없음 |
| 가격 | 0 이상의 정수 | 문서에 규칙 없음 |
| 이미지 URL | 빈 값/공백만 있는 값 금지, `https`만 허용(`http` 불가) | 문서에 규칙 없음. 값 객체 `ProductImage`로 검증 |
| 위시 중복 추가 | 같은 상품 중복 추가 시 400 응답 | 문서에 규칙 없음 |
| 위시 삭제 권한 | 본인 위시만 삭제 가능, 타인 것이면 403 | 문서에 규칙 없음 |
| 오류 응답 형식 | 상태 코드 + `{"message": "..."}` | 문서에 "적절한 오류 응답"이라고만 함 |
| `null` 검사 방식 | `Objects.requireNonNull` 대신 각 클래스가 자기 `private static validateNotNull(value, message)`를 가진다. `null`이면 메시지와 함께 `NullPointerException`을 던진다 | 검증을 이름 있는 `validate` 메서드로 두는 다른 도메인 클래스(`validateNotBlank` 등)와 읽는 방식을 맞추고, 공용 도구에 기대지 않기 위함 (사용자 결정) |
| `null` 예외 종류 | 사용자가 보낸 값(문자열 등)의 `null`/빈 값은 `IllegalArgumentException`(API 400), 이미 만들어진 객체를 받는 곳의 `null`은 호출한 코드의 버그이므로 `NullPointerException` | 입력 오류와 프로그래밍 오류를 구분하기 위함 |
| 상품 이름 허용 문자 | 영문(A-Z, a-z), 숫자, 공백, `( ) [ ] + - & / _`만 허용. 한글 등 그 외 문자는 예외 | 문서에 범위가 없음. PurgoMalum이 영문 비속어만 걸러냄을 직접 확인했기 때문에 검사할 수 없는 문자는 입력받지 않기로 함 |
| 상품 이름의 숫자 | 허용 (**사용자 확인 필요**) | 요청에서는 "영문과 허용 특수문자"만 언급했으나 `Galaxy S24` 같은 이름을 위해 포함. 불허로 바꾸려면 정규식에서 `0-9`만 제거하고 테스트를 수정하면 된다 |
| 비속어 검사 호출 | 외부 호출부를 인터페이스로 분리하고 테스트에서는 대체 구현(Fake) 사용 | 테스트가 외부 네트워크에 의존하지 않도록 함 |
| 저장 | DB를 사용하지 않고 메모리에만 보관한다. 영속적으로 저장하는 것은 목적이 아니다 (JPA, MySQL, Flyway 사용 안 함) | 사용자 결정. 과제 문서에 DB·저장 방식 요구가 없고, 목표는 HTTP API를 제공하는 것이다 ("3-2. 저장 방침" 참고) |

### 아직 확정하지 않은 것 (구현 중 확인 후 이 표에 추가)
- PurgoMalum의 요청 URL과 응답 형식: 공식 사이트와 직접 호출로 `GET https://www.purgomalum.com/service/containsprofanity?text=...`가 본문에 `true`/`false`만 돌려주는 것을 확인했다 (문서에는 "PurgoMalum API를 호출한다"고만 있음). 상태 코드와 오류 응답은 아래와 같이 직접 확인했다 (2026-11-19 호출 결과).

  | 요청 | 상태 | 본문 |
  |---|---|---|
  | `text=hello` | 200 (`text/plain`) | `false` |
  | `text=shit` | 200 (`text/plain`) | `true` |
  | `text` 파라미터 없음 / 빈 값 / 공백만 | 200 | `false` (오류가 아님) |
  | 허용 특수문자 `Item(1)[a]+b-c&d/e_f` | 200 | `false` |
  | 1000자 텍스트 | 200 | `false` |
  | `add` 단어 11개(문서상 최대 10개) | 200 | `false` (오류가 아님) |
  | `POST` | 405 (`text/html`) | 빈 본문 |
  | 없는 경로 | 404 (`text/html`) | 빈 본문 |
  | `http://` (비TLS) | 200 | 그대로 응답함 (HTTPS 사용 필요) |

  - 입력이 잘못되어도 오류 대신 `200 false`가 올 수 있다. 그래서 `ProductName`이 빈 값을 먼저 막는 현재 순서(비속어 검사는 마지막)가 안전하다.
  - 본문이 `true`/`false`가 아니거나 상태가 200이 아니면 응답을 신뢰할 수 없으므로 실패로 다뤄야 한다 (처리 방식은 아래 "호출 실패 시 처리 방식"에서 결정).
  - 5xx, 타임아웃은 재현하지 못했다. 확인되지 않은 상태로 남는다.
  - 판정 특성: 대소문자 무시(`SHIT` true), 숫자 치환(`sh1t` true), 띄어쓰기(`s h i t` true)도 걸러낸다. 반면 정상 단어가 오탐된다 (`Scunthorpe`, `Shiitake`가 true). `Classic`, `Bass`, `Assassin`은 false였다. 즉 `Shiitake` 같은 이름의 상품은 등록할 수 없다. 이 한계는 과제 요구 사항(PurgoMalum 사용)에 따른 것으로 받아들인다 *[결정]*.
- PurgoMalum의 한계: 영문 비속어만 걸러낸다 (`shit`, `fuck`은 true, `시발`, `씨발`, `ㅅㅂ`, `병신`, `좆`은 false). 공식 사이트에 지원 언어 설명은 없고 위 호출 결과로만 확인했다. 한국어 비속어는 걸러지지 않으므로, 한국어 입력을 막는 현재 문자 제한으로 우회한다. 이 한계를 제출 소감에 기록한다.
- ~~PurgoMalum 호출 실패(타임아웃 등) 시 처리 방식~~ → 아래 "3. 비속어 검사 구현"의 오류 처리 표로 결정 (타임아웃 시간 값은 구현 시 확정)
- 비밀번호 해시 방식과 필요한 의존성: 보류 ("14. 회원 API"의 한계 참고)
- ~~상품 이름이 허용하는 문자 범위~~ → 영문, 숫자, 공백, 허용 특수문자로 결정 (4번 `ProductName` 참고)

## 3-0. 코드 컨벤션 결정 (null 검사)

- 처음에는 일부 클래스(`Member`, `Product`, `Products`, `WishList`, 서비스 등)가 `Objects.requireNonNull`로 `null`을 검사하고, 다른 클래스(`ProductName`, `Email`, `Password` 등)는 `validateXxx` 메서드를 써서 방식이 섞여 있었다. 사용자의 요청으로 `validate` 메서드 방식으로 통일했다.
- 각 클래스가 `private static void validateNotNull(Object value, String message)`를 **자기 안에** 가진다 *[결정, 사용자]*. 한때 공용 클래스(`shopping.support.Validations`)로 한 곳에 모았다가, 사용자의 요청으로 클래스마다 두는 방식으로 되돌리고 공용 클래스와 그 테스트는 삭제했다.
- 같은 5줄짜리 메서드가 12개 클래스에 반복된다. 이는 알고 선택한 중복이다 (클래스가 다른 클래스의 내부 도구에 기대지 않도록 하기 위함).
- 대상 클래스 12개: `Member`, `Members`, `Product`, `Products`, `ProductName`, `WishList`, `ProductService`, `MemberService`, `AuthService`, `WishService`, `TokenStore`, `LoginMemberArgumentResolver`.
- 동작은 처음과 같다: `null`이면 같은 메시지의 `NullPointerException`. 운영 코드의 `Objects.requireNonNull`은 0개다.
- 생성자에서는 검증을 모두 먼저 하고 그다음 필드를 대입한다 (검증 한 줄, 대입 한 줄이 번갈아 나오지 않게).
- 테스트로 직접 확인되는 `null` 검사: `Member`, `Members`, `Product`, `Products`, `WishList`, `ProductName`(검사기), `TokenStore`, `WishService`(회원). 서비스 생성자와 `LoginMemberArgumentResolver`의 `null` 검사는 별도 테스트가 없다.

## 3-0-1. 컬렉션 방침 (중복을 담지 않는 목록)

- 키로 중복을 막는 목록(`Members`, `Products`, `WishList`)은 `List` 대신 `LinkedHashMap<키, 값>`을 쓴다 *[결정, 사용자 요청]*. `Set`은 객체의 `equals`로 중복을 판단하는데 우리가 막으려는 것은 "객체 전체가 같음"이 아니라 "키(ID, 이메일)가 같음"이어서, 키를 직접 표현하는 `Map`이 의도에 더 맞는다.
- 키: `Members`는 `MemberId`, `Products`와 `WishList`는 `ProductId`. ID는 `final`이라 키가 바뀌지 않는다.
- `LinkedHashMap`이라 담은 순서가 유지된다 (조회는 담은 순서대로, 삭제 후 다시 담으면 맨 뒤).
- `Map`으로 ID 조회가 단건 조회(`get`)가 되었고, 기존의 전체 순회(`stream`)는 `Members`의 이메일 조회와 `Products`의 이름 중복 검사에만 남았다. 이 두 가지는 키가 아닌 값으로 찾기 때문이다.
- 바꾼 뒤에도 외부 동작은 같아서 기존 테스트(동시성 테스트 포함)가 수정 없이 통과했다.

## 3-0-2. 오류 메시지 방침

- 오류 메시지는 IT를 모르는 사용자가 읽고 무엇을 고쳐야 하는지 알 수 있는 말로 쓴다 *[결정, 사용자 요청]*. 개발 용어(`null`, `ID`, `URL`, `JSON`, `id=`, `Exception`)를 쓰지 않는다.
- 용어는 이렇게 바꿔 쓴다: ID → 번호, URL → 주소, 공백 → 띄어쓰기, 토큰 → 로그인.
- 메시지는 "무엇이 문제인지"와 "어떻게 하면 되는지"를 함께 담는다. 예: "상품 가격은 0원 이상이어야 합니다.", "상품 이미지 주소는 https://로 시작해야 합니다.", "상품 이름을 입력해 주세요."
- 사용자가 입력하지 않은 내부 값(처리 중 없는 값)의 메시지도 같은 방침으로 바꿨다. 예: "상품은 null일 수 없습니다." → "상품 정보가 없습니다." 이런 오류는 프로그램의 문제라서 보통 사용자에게 보이지 않는다.
- 비밀번호 규칙은 항목별로 따로 알려준다 (영문 없음, 숫자 없음, 특수문자 없음, 8자 미만, 쓸 수 없는 문자). 이전에는 "영문가", "숫자가"처럼 조사가 어색했던 문장도 고쳤다.
- 로그인 실패는 이유를 구분하지 않고 같은 문장("이메일 또는 비밀번호가 올바르지 않습니다.")으로 답한다 (보안 결정은 "14. 회원 API" 참고).
- 비속어 검사 서비스 오류는 내부 정보(주소, 상태 코드)를 숨긴 문장으로 답한다 (표는 "3. 비속어 검사 구현" 참고). 개발자용 상세 원인은 예외 메시지에만 남는다.
- 테스트
  - `ErrorMessageTest`(62개): 상품, 회원, 위시 리스트 API의 요청 오류 30가지(+ 조회·삭제·인증·중복 위시 오류)가 정해진 문장으로 나오는지, 그 문장에 개발 용어가 없는지 확인한다.
  - `NullMessageTest`(1개): 값이 없을 때의 메시지 28곳이 비어 있지 않고 개발 용어가 없는지 확인한다. 확인용으로 메시지 하나를 `null일 수 없습니다.`로 되돌려 보니 이 테스트가 실패했고, 다시 고쳐 통과시켰다.
  - 문장 자체는 위 두 테스트에 적힌 그대로가 기준이다. 문장을 바꾸면 이 테스트도 같이 고쳐야 한다.
- 한계: 검증은 개발 용어 목록(`null`, `ID`, `id=`, `URL`, `JSON`, `Exception`)이 들어 있는지 보는 것이라 문장이 자연스러운지까지는 사람이 읽어봐야 한다. 오류 메시지는 한국어만 지원한다.

## 3-0-3. 객체에게 시킨다 (묻지 말고 시키기)

- 다른 객체의 값을 꺼내 와서 밖에서 판단하는 코드(`entry.getValue().name().equals(name)`처럼 점으로 길게 이어지는 체인)를 쓰지 않는다. 판단은 그 값을 가진 객체가 한다 *[결정, 사용자 요청]*.
- 바꾼 곳:

  | 전 (밖에서 꺼내 판단) | 후 (객체가 스스로 답함) |
  |---|---|
  | `product.name().equals(name)` (`Products`의 이름 중복 검사) | `product.hasName(name)` |
  | `!entry.getKey().equals(ownId)` | `!product.hasId(ownId)` |
  | `member.email().equals(email)` (`Members`의 이메일 조회) | `member.hasEmail(email)` |
  | `member.password().value().getBytes(...)`와 `MessageDigest.isEqual`을 `MemberService`가 직접 비교 | `member.matchesPassword(password)` → `password.matches(other)`. 비교(상수 시간 비교)는 비밀번호 클래스 안으로 옮겼다 |
  | `member.wishList().add(product)`, `.remove(id)`, `.all()` (`WishService`) | `member.addWish(product)`, `member.removeWish(id)`, `member.wishes()` |

- `Member.wishList()` 접근자는 없앴다. 위시 리스트는 `Member`가 감싸고 있고 바깥은 `addWish`, `removeWish`, `wishes`로만 다룬다.
- 질문 메서드는 `null`을 받으면 `NullPointerException`을 던진다 (예: `hasName(null)`). 메시지도 쉬운 말이다 ("비교할 상품 이름이 없습니다.").
- 저장 전 상품(ID 없음)은 `hasId`에서 어떤 ID와도 같지 않다고 답한다.
- 테스트: `ProductTest`, `MemberTest`, `PasswordTest`에 질문 메서드 테스트 추가, `NullMessageTest`에 5곳 추가(총 33곳). 기존 `MemberTest`는 `wishList()` 대신 새 메서드를 쓰도록 고쳤다.
- 아직 남은 체인 (의도적으로 둠): `ProductResponse.from`의 `product.id().orElseThrow().value()`, `product.name().value()`, `product.price().value()`는 응답용 객체로 값을 옮기는 변환이라 값을 꺼내야 한다. `Optional`을 풀어내는 `id().orElseThrow()`도 같은 이유로 남겼다. 이것까지 없애려면 `Product`가 응답 형태로 스스로 변환하게 해야 하는데 도메인이 응답 형식을 알게 되어 오히려 좋지 않다고 판단했다 *[결정]*.

## 3-1. 상품 도메인 설계

상품(`Product`)은 이름, 가격, 이미지 URL을 가진다 **[문서]**. 이름, 가격, 이미지, ID는 각자의 규칙을 스스로 지키는 값 객체(`ProductName`, `ProductPrice`, `ProductImage`, `ProductId`)로 분리한다 *[결정]*.

| 도메인 | 구성 | 규칙 |
|---|---|---|
| `ProductName` | 문자열 | 공백 포함 최대 15자 **[문서]**, 허용 특수문자는 `( ) [ ] + - & / _` 뿐 **[문서]**, 비속어 포함 불가 **[문서]**, 빈 값/공백만 있는 값 불가 *[결정]*, 영문·숫자·공백·허용 특수문자 외 문자 불가(한글 등 불가) *[결정]* |
| `ProductPrice` | 정수 | 음수 불가 (0은 허용) *[결정: 0 허용 여부는 문서에 없음]* |
| `ProductImage` | 문자열(URL) | 파일 업로드가 아닌 URL 입력 방식 **[문서]**, 빈 값/공백만 있는 값 불가 *[결정]*, `https`만 허용(`http` 등 그 외 스킴 불가) *[결정]*, 호스트가 있어야 하며 형식이 올바른 URL이어야 함 *[결정]* |
| `ProductId` | 정수(`long`) | 1 이상 *[결정]*, 같은 값이면 같은 ID |
| `Products` | `Product` 목록 | 같은 ID, 같은 이름의 상품을 둘 이상 담을 수 없음 *[결정]* |
| `Product` | `ProductId`(저장 전에는 없음) + `ProductName` + `ProductPrice` + `ProductImage` | 위 값들이 모두 유효해야 생성/수정 가능 |

구현 순서 (TDD):
1. `ProductName` (비속어 검사 없이 먼저) → 2. 비속어 검사기를 `ProductName`에 연결 → 3. PurgoMalum 구현체 → 4. `ProductPrice` → 5. `ProductImage` → 6. `ProductId` → 7. `Product` → 8. `Products`

비속어 검사를 처음부터 설계하지 않고 `ProductName`을 먼저 만든 뒤 나중에 붙인다. 붙이는 시점에 생기는 불편함(생성자 변경, 기존 테스트 수정 등)을 직접 겪고, 그 불편함이 인터페이스 분리의 이유가 되는지 확인한다 *[결정]*. 이때 느낀 점은 6번 섹션(AI 도구 활용 기록/소감)에 남긴다.

설계 결정:
- 도메인 클래스는 `shopping.domain` 패키지에 만든다 *[결정]*.
- 비속어 검사는 2단계에서 `ProductName`에 연결한다. 연결 방식(인터페이스 주입 등)은 그때 겪은 불편함을 보고 확정한다.
- 값 객체는 생성 시점에 검증하고, 잘못된 값이면 `IllegalArgumentException`을 던진다 *[결정]*. HTTP 오류 응답으로의 변환은 API 단계(13번)에서 한다.
- 가격 타입은 `long`으로 확정했다 (문서에 가격 범위 규정 없음).

## 3-2. 저장 방침

**결정 (사용자)**: HTTP API를 제공하는 것이 목적이다. 데이터를 영속적으로(서버를 껐다 켜도 남게) 저장하는 것은 목적으로 두지 않는다. DB는 쓰지 않는다.

**근거 (과제 문서)**
- 문서에는 DB, JPA, MySQL, Flyway에 대한 요구가 없다. "스프링 프레임워크를 사용하여 웹 애플리케이션으로 구현한다", "HTTP 요청과 응답은 JSON 형식"이라고만 되어 있다.
- 따라서 메모리에 보관해도 문서 요구를 어기지 않는다.

**방침**
- 상품은 `Products`(메모리)에 보관한다. 서버를 다시 시작하면 데이터가 사라지는 것은 의도된 동작이다 *[결정]*.
- 회원, 토큰, 위시 리스트도 같은 방식으로 메모리에 보관한다 *[결정]*.
- 상품 ID는 서버가 1부터 순서대로 부여한다 (서비스 계층에서 부여, 아래 "상품 서비스" 참고) *[결정]*. 서버 재시작 시 다시 1부터 시작한다.
- 저장소 인터페이스는 지금 만들지 않는다. 필요해지면(예: DB를 나중에 붙이는 경우) 그때 추출한다 *[결정]*.

**동시 요청 (보완 필요)**
- 스프링은 요청을 여러 스레드에서 동시에 처리한다. 현재 `Products`는 일반 `ArrayList`를 쓰므로 동시에 접근하면 중복 검사가 뚫리거나 오류가 날 수 있다.
- API에서 쓰기 전에 `Products`를 스레드 안전하게 만든다 (TODO 9번에 항목 추가).

**의존성 현황 (사실)**
- `build.gradle.kts`에는 이 방침에서 쓰지 않는 DB 관련 항목(JPA, Flyway, MySQL, H2)이 들어 있다. 앱을 실행하면 내장 H2, Flyway, JPA가 자동으로 시작되는 것을 로그로 확인했다 (`HikariPool`, `Flyway`, `EntityManagerFactory`).
- 이 항목들은 정리하지 않고 그대로 둔다 *[결정, 사용자]*. 제거했을 때 전체 테스트 220개와 앱 시작에는 문제가 없었으나 되돌렸다.

## 3-3. 회원 도메인 설계

과제 문서에는 "회원은 이메일과 비밀번호로 가입할 수 있다", "위시 리스트에 등록된 상품 목록을 조회/추가/삭제할 수 있다"만 있다 **[문서]**. 이메일과 비밀번호의 형식 규칙, 위시 리스트의 중복 규칙은 문서에 없고 아래는 모두 사용자가 정한 규칙이다.

회원(`Member`)은 이메일, 비밀번호, 위시 리스트를 가진다. 이메일, 비밀번호, 위시 리스트는 각자의 규칙을 스스로 지키는 도메인(`Email`, `Password`, `WishList`)으로 분리한다 *[결정]*.

| 도메인 | 구성 | 규칙 (모두 사용자 결정) |
|---|---|---|
| `Email` | 문자열 | `@`를 포함해야 하고, 도메인은 `gmail.com`만 허용 |
| `Password` | 문자열 | 영문, 숫자, 특수문자를 각각 1개 이상 포함하고 8자 이상 |
| `WishList` | `Product` 목록 | 상품 목록 조회, 추가, 삭제 가능. 같은 ID의 상품을 둘 이상 가질 수 없음 |
| `Member` | `Email` + `Password` + `WishList` | 세 값이 모두 있어야 생성 가능 |

**`gmail.com`만 허용하는 이유**
- 이 규칙은 과제 문서에 없고 사용자가 정한 것이다. 문서의 이메일 규칙은 "이메일로 가입한다"뿐이다.
- 사용자가 요청에서 이유를 따로 말하지 않았다. 아래는 규칙을 적용했을 때 확인되는 사실이며, 실제 이유는 사용자가 정해서 이 자리에 적는다: **(이유 미기재 — 사용자 확인 필요)**
  - 허용 도메인이 하나라서 검증 규칙이 단순하고 테스트로 모두 확인할 수 있다.
  - 다른 메일 서비스(`naver.com`, `kakao.com` 등)와 회사 메일 주소는 가입할 수 없다.

**이메일 세부 규칙 (문서에 없어 정한 것)** *[결정]*
- `@`는 정확히 하나여야 한다.
- `@` 앞(로컬 파트)은 비어 있으면 안 된다.
- 공백 문자(앞뒤 공백 포함)가 있으면 안 된다. 자동으로 다듬지 않는다.
- `@` 뒤는 정확히 `gmail.com`이어야 한다. 도메인은 대소문자를 구분하지 않는다 (`GMAIL.COM` 허용). 하위 도메인(`sub.gmail.com`), `gmail.com.kr`, `gmail.co`는 거부한다.
- 로컬 파트의 대소문자는 입력 그대로 보관하고, 같은 이메일 판정(`equals`)도 입력 그대로 비교한다. 대소문자를 무시하는 중복 판정은 하지 않는다 (미결정, 회원 가입 단계에서 필요하면 다시 정한다).
- `null`, 빈 값, 공백만 있는 값은 거부한다.

**비밀번호 세부 규칙 (문서에 없어 정한 것)** *[결정]*
- 8자 이상. 최대 길이는 두지 않는다.
- 영문(A-Z, a-z) 1개 이상, 숫자(0-9) 1개 이상, 특수문자 1개 이상을 포함한다. 대문자 필수 같은 추가 조건은 없다.
- 특수문자는 ASCII 기호 `` ! " # $ % & ' ( ) * + , - . / : ; < = > ? @ [ \ ] ^ _ ` { | } ~ ``를 뜻한다.
- 영문, 숫자, 위 특수문자 외의 문자는 거부한다. 공백, 한글, 악센트 문자(é)가 들어가면 거부한다.
- `null`과 빈 값은 거부한다.
- `toString()`은 비밀번호를 노출하지 않는다 (`****`).
- 비밀번호를 해시해서 보관하지 않는다. 회원 API 단계에서 보류하기로 했다 ("14. 회원 API"의 한계 참고).

**위시 리스트 세부 규칙** *[결정]*
- 저장된 상품(ID가 있는 `Product`)만 담는다. ID가 없는 상품은 거부한다.
- 같은 ID의 상품은 두 번 담을 수 없다 (예외, 기존 목록 유지).
- 삭제는 상품 ID로 한다. 담겨 있지 않은 상품을 삭제하면 `NoSuchElementException`.
- 조회한 목록은 복사본이라 직접 수정할 수 없다. 담은 순서대로 조회된다.
- `null`은 거부한다 (`NullPointerException`).
- 과제 API 명세는 삭제를 `/api/wishes/{wishId}`로 한다. 도메인은 상품 ID로 삭제하므로 `wishId`를 상품 ID와 같다고 보기로 했다 ("16. 위시 리스트 API" 참고).
- 이 단계에서는 인증, 회원 ID, 회원 중복 가입 방지, 토큰은 다루지 않는다.

**회원 세부 규칙** *[결정]*
- 이메일과 비밀번호로 생성하고, 위시 리스트는 비어 있는 상태로 시작한다.
- 이메일, 비밀번호가 `null`이면 `NullPointerException`.
- 회원도 ID로 관리한다 *[결정, 사용자 요청]*: `MemberId`(1 이상의 `long`, 값이 같으면 같은 ID)를 두고 `Member`가 ID를 가진다. 자세한 내용은 아래 "12. 회원 도메인"과 "14. 회원 API" 참고.

구현 순서 (TDD): `Email` → `Password` → `WishList` → `Member`

## 4. 기능 목록 (TODO)

각 항목은 TDD 사이클(테스트 먼저)로 진행하고, 완료 후 항목 단위로 커밋한다.
커밋 메시지 예: `feat: 상품 이름 유효성 검사 추가`, `test: ...`, `docs: ...`

### 0. 사전 준비
- [ ] `java -version`이 21인지 확인 (OpenJDK 21.0.10)
- [ ] `./gradlew clean test`가 현재 상태에서 통과하는지 확인 (BUILD SUCCESSFUL, 아직 테스트가 없어 `test NO-SOURCE`)
- [ ] 4 spaces 들여쓰기 등 코드 스타일 설정 (`.editorconfig` 추가)
- [ ] 테스트에서 JUnit 5와 AssertJ를 사용할 수 있는지 확인 (임시 테스트로 통과 확인 후 삭제)
- [ ] `application.properties` 확인: 현재 `spring.application.name=spring-shopping` 한 줄뿐이며 H2/Flyway 별도 설정 없음

### 1. 상품 이름 도메인 `ProductName` (값 객체, 단위 테스트) — 비속어 검사 제외
위치: `src/main/java/shopping/domain/ProductName.java`, 테스트: `src/test/java/shopping/domain/ProductNameTest.java`
- [x] 공백 포함 15자 이하만 허용 (16자 이상이면 예외)
- [x] 빈 값/공백만 있는 이름은 허용하지 않음 *[결정]* (null도 예외)
- [x] 허용 특수문자 `( ) [ ] + - & / _` 는 통과
- [x] 그 외 특수문자는 예외
- 결정: 상품 이름은 **영문(A-Z, a-z), 숫자, 공백, 허용 특수문자 `( ) [ ] + - & / _`만** 허용한다. 문서에 허용 문자 범위가 없어 정했다. 이유: 비속어 검사에 쓰는 PurgoMalum이 영문 비속어만 걸러내는 것을 확인했다 (`shit`, `fuck`은 true, `시발`, `씨발`, `ㅅㅂ`, `병신`, `좆`은 false). 검사할 수 없는 한글 등을 입력받지 않도록 문자 범위를 영문으로 제한한다.
- 결정: 숫자는 허용한다. (요청에서는 "영문과 허용 특수문자"만 언급했고, `Galaxy S24` 같은 이름을 위해 숫자를 포함했다.)
- [x] 한글, 한자, 일본어, 악센트 문자, 전각 문자, 이모지 등 영문이 아닌 글자는 허용하지 않음 *[결정]*: 허용 목록(영문·숫자·공백·허용 특수문자) 밖의 문자는 모두 예외다.
- 허용 문자가 모두 ASCII이므로 글자 수는 `String.length()`로 센다. (BMP 밖 문자 고려는 필요 없어 제거했다.)

### 2. 비속어 검사기를 `ProductName`에 연결
- [x] 비속어 포함 시 예외가 나는 테스트 작성 (실패 확인)
- [x] 비속어 검사 방법 결정 및 연결 (인터페이스 정의 + 테스트용 대체 구현(Fake) 포함 여부는 이때 확정)
- [x] 기존 `ProductName` 테스트 수정 사항과 불편했던 점을 기록

**진행 기록 (겪은 불편함)**
1. `new ProductName("shit")`으로 1인자 생성자 테스트를 먼저 썼더니 실패(Red)했지만, 통과시킬 방법이 바로 정해지지 않았다. 후보는 세 가지였다.
   - 도메인 안에 비속어 목록을 하드코딩 → 문서가 요구한 "PurgoMalum API 호출"과 맞지 않는다.
   - 도메인에서 직접 HTTP 호출 → 단위 테스트가 네트워크에 의존하고, 도메인이 외부 기술을 알게 된다.
   - 검사 책임을 밖에서 주입 → 채택.
2. 생성자를 `ProductName(String, ProfanityChecker)`로 바꾸자 기존 테스트 8곳이 컴파일 에러로 깨졌다.
3. 모든 테스트가 검사기를 넘겨야 해서 `NO_PROFANITY` 상수를 만들어 8곳을 고쳤다. 이름 규칙과 무관한 테스트에도 비속어 개념이 따라온다.
4. 비속어 검사는 가장 마지막에 둔다. 비싼 검사(나중에 외부 호출)를 앞의 값싼 검사가 통과한 뒤에만 하기 위함이다.

**결정**
- 인터페이스 `ProfanityChecker`(`boolean containsProfanity(String text)`)를 `shopping.domain`에 둔다.
- 테스트용 Fake는 별도 클래스 없이 람다로 만든다 (`@FunctionalInterface`).
- 결과: 테스트 50개 통과 (null 검사기 테스트 추가, 한글 등 허용 범위를 영문으로 좁힌 뒤).

**남은 문제 (아직 해결하지 않음)**
- ~~`null` 검사기를 넘기면 `NullPointerException`이 난다. 테스트도 없다.~~ → 해결: 생성자 첫 줄에서 `validateNotNull`로 즉시 실패시키고(값 검증보다 먼저), 테스트 추가.
- 현재 비속어 테스트는 "검사기가 true를 주면 예외를 던진다"만 증명한다. 실제 비속어 판정은 3번(PurgoMalum 구현체)이 끝나야 검증된다.

### 3. 비속어 검사 구현 (PurgoMalum)
- [x] PurgoMalum API 연동 구현체 (`ProfanityChecker` 구현, `GET https://www.purgomalum.com/service/containsprofanity?text=...`): `shopping.infrastructure.PurgoMalumProfanityChecker`
- [x] 정상 응답 처리: 200 + `true`는 비속어 있음, 200 + `false`는 없음
- [x] 오류 응답 처리 (아래 표): `shopping.domain.ProfanityCheckException`의 `Reason`(`BAD_REQUEST`, `SERVICE_ERROR`, `CONNECTION_ERROR`, `INVALID_RESPONSE`)으로 구분. HTTP 상태/메시지로의 변환은 API 단계에서 한다.
- [x] 연동 구현체 테스트 (외부 호출은 `MockRestServiceServer`와 로컬 가짜 서버로 검증, 실제 PurgoMalum 호출은 테스트에 넣지 않는다): 총 29개
- [x] 요청 시 이름을 쿼리 값으로 인코딩 (`&`, `+`, `/`, 공백, 괄호가 포함된 이름도 안전하게 전달됨을 테스트로 확인)
- [x] 타임아웃 적용: `PurgoMalumRestClientFactory` (연결 2초, 읽기 3초 *[결정]*). 로컬 가짜 서버가 느리게 응답하는 경우로 검증.
- [x] 응답 본문은 정확히 `true`/`false`(소문자)만 인정. 그 외(`True`, 빈 본문, HTML, 204/302)는 `INVALID_RESPONSE`.
- 참고: 실제 PurgoMalum을 한 번 호출해 `shit`는 true, 특수문자 포함 이름은 false가 나오는 것을 확인했다 (임시 테스트로 확인 후 삭제).
- 아직 안 한 것: `ProductName` 생성 지점에 이 구현체를 Spring 빈으로 연결하는 일은 상품 API 단계에서 한다.

**오류 처리 방침 *[결정]***

PurgoMalum 호출이 실패했을 때 오류 종류에 맞는 메시지를 돌려준다. 비속어 여부를 알 수 없으면 상품을 등록/수정하지 않는다(실패 시 통과시키지 않음).

| 상황 | 구현체가 던지는 예외 | 우리 API 응답 | 응답 메시지 |
|---|---|---|---|
| PurgoMalum이 4xx 응답 (우리 요청이 잘못됨) | `ProfanityCheckException` (요청 오류) | 500 | 비속어 검사 요청에 문제가 있어 처리할 수 없습니다. |
| PurgoMalum이 5xx 응답 | `ProfanityCheckException` (서비스 오류) | 503 | 비속어 검사 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해 주세요. |
| 타임아웃, 연결 실패 | `ProfanityCheckException` (연결 오류) | 503 | 비속어 검사 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요. |
| 200인데 본문이 `true`/`false`가 아님 | `ProfanityCheckException` (응답 형식 오류) | 502 | 비속어 검사 서비스에서 올바르지 않은 응답을 받았습니다. |

- 4xx는 사용자 입력이 아니라 우리 요청의 문제다. 상품 이름은 호출 전에 이미 검증되므로 클라이언트 잘못(400)으로 돌려보내지 않고 500으로 둔다.
- 응답 메시지에는 PurgoMalum 주소, 상태 코드 등 내부 정보를 넣지 않는다. 상세 원인은 예외 메시지와 로그에만 남긴다.
- 이 오류는 상품 이름이 규칙을 어긴 것(400)과 구분한다. 비속어 포함은 `IllegalArgumentException`(400), 검사 자체의 실패는 `ProfanityCheckException`(500/502/503)이다.
- 4xx/5xx 각각에 대해 오류 종류별 테스트를 작성한다. 4xx는 404/405를, 5xx는 500/503을 가짜 서버로 재현한다.
- HTTP 응답으로의 변환은 API 단계(상품 API의 공통 예외 처리)에서 한다.
- 타임아웃 시간은 구현 시 정한다 (현재 미정).

### 4. 상품 가격 도메인 `ProductPrice` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/ProductPrice.java`
- [x] 음수는 허용하지 않음 (예외)
- [x] 0은 허용 *[결정]*
- [x] 양수는 허용
- 결정: 가격 타입은 `long`이다. 문서에 가격 범위 규정이 없어 정수 중 넓은 타입을 택했다. 상한은 두지 않는다 (`Long.MAX_VALUE`까지 허용). 소수(원 미만 단위)는 다루지 않는다.
- 위반 시 `IllegalArgumentException`을 던진다.
- 결과: 테스트 7개 추가 (`ProductPriceTest`).

### 5. 상품 이미지 도메인 `ProductImage` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/ProductImage.java`
- [x] 빈 값/공백만 있는 값(`null` 포함)은 허용하지 않음 *[결정]*
- [x] `https`만 허용한다. `http`는 허용하지 않음 *[결정]* (대소문자 무시: `HTTPS://`는 허용, `HTTP://`는 거부)
- [x] `https`가 아닌 스킴은 모두 거부: `ftp`, `file`, `javascript`, `data`, 스킴 없음(`//example.com/a.png`, `example.com/a.png`)
- [x] 형식이 올바르지 않은 URL은 거부: 호스트 없음(`https://`, `https:///a.png`), 공백 포함(앞뒤 공백 포함), `<`, `>` 같은 허용되지 않는 문자
- [x] 정상 URL 허용: 경로, 포트, 쿼리 문자열(`?size=100&type=thumb`)이 있는 `https` URL
- 결정: 파일 확장자(`.png` 등)나 실제 이미지인지는 검사하지 않는다. 문서에 규칙이 없고, 확인하려면 외부 요청이 필요하기 때문이다.
- 결정: 길이 제한은 두지 않는다 (문서에 규칙 없음).
- 결정: 입력을 자동으로 다듬지 않는다 (`trim` 없음). 앞뒤 공백이 있으면 거부한다.
- 구현: `java.net.URI`로 파싱해 스킴과 호스트를 검사한다. 위반 시 `IllegalArgumentException`.
- 결과: 테스트 22개 추가 (`ProductImageTest`).

### 6. 상품 ID 도메인 `ProductId` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/ProductId.java`
- [x] 1 이상의 값으로 생성할 수 있다
- [x] 0 이하의 값은 허용하지 않음 (예외) *[결정]*
- [x] 값이 같으면 같은 ID이다 (동등성)
- 결정: ID는 `long`이다. 1부터 시작한다고 보고 0 이하를 거부한다 (문서에 ID 규칙 없음).
- 결정: Java `record`로 만들어 `equals`/`hashCode`를 값 기준으로 얻는다. 다른 값 객체와 달리 동등성이 필요해서다 (위시 리스트, 조회 등에서 비교).
- 결과: 테스트 7개 추가 (`ProductIdTest`).

### 7. 상품 도메인 `Product` (단위 테스트)
위치: `src/main/java/shopping/domain/Product.java`
`ProductId`, `ProductName`, `ProductPrice`, `ProductImage`를 가진다.
- [x] 이름, 가격, 이미지로 상품을 생성할 수 있다 (ID는 저장 전에는 없음)
- [x] ID와 함께 상품을 생성할 수 있다
- [x] 이름, 가격, 이미지는 각 값 객체의 규칙을 그대로 따른다 (잘못된 값이면 생성 불가)
- [x] 상품 수정 시에도 같은 규칙이 적용된다
- 결정: `Product`는 원시 값이 아니라 값 객체(`ProductName`, `ProductPrice`, `ProductImage`)를 받는다. 값 객체는 만들어지는 순간 규칙을 검증하므로, 규칙을 어긴 값은 `Product`에 전달되기 전에 이미 `IllegalArgumentException`으로 막힌다. 그래서 `Product`는 규칙을 다시 검사하지 않고 `null`만 막는다 (`NullPointerException`).
- 결정: 수정은 `update(name, price, image)`로 세 값을 한 번에 바꾼다. 부분 수정은 만들지 않는다 (API 명세의 `PUT`이 전체 수정이기 때문이 아니라, 문서에 부분 수정 요구가 없기 때문이다).
- 결정: 수정이 실패하면(`null` 포함) 기존 값은 그대로 유지된다. 세 값을 모두 검사한 뒤에 대입한다.
- 결정: `Product`는 `ProductId`를 가진다. 생성자는 두 가지다.
  - `Product(name, price, image)`: 아직 저장 전인 새 상품. ID가 없다.
  - `Product(id, name, price, image)`: 저장된 상품. ID는 `null`일 수 없다.
- 결정: `id()`는 `Optional<ProductId>`를 돌려준다. 저장 전 상품은 비어 있다. `null`을 반환하지 않기 위해서다.
- 결정: ID는 한 번 정해지면 바뀌지 않는다 (`final`). `update`는 ID를 건드리지 않는다.
- 영속 저장은 하지 않는다 ("3-2. 저장 방침"). 서비스가 ID를 부여하고 ID가 있는 `Product`를 만들어 `Products`에 넣는다.
- 결과: 테스트 12개 (`ProductTest`, ID 관련 4개 포함).

### 8. 상품 목록 도메인 `Products` (단위 테스트)
위치: `src/main/java/shopping/domain/Products.java`
상품들을 관리하며, 같은 ID나 같은 이름의 상품이 들어오는 것을 막는다 *[결정]*.
- [x] 추가: 상품을 추가하면 추가한 순서대로 조회된다
- [x] 같은 ID의 상품은 추가할 수 없다 (예외, 기존 목록 유지)
- [x] 같은 이름의 상품은 추가할 수 없다 (예외, 기존 목록 유지)
- [x] ID가 없는 상품(저장 전)은 추가할 수 없다
- [x] `null`은 추가할 수 없다 (`NullPointerException`)
- [x] ID로 조회한다 (없으면 `Optional.empty`)
- [x] 수정: 다른 상품과 같은 이름으로 수정할 수 없다 (예외, 기존 값 유지). 자기 자신의 이름으로 수정하는 것은 허용한다.
- [x] 없는 상품을 수정/삭제하면 `NoSuchElementException`
- [x] 삭제: 삭제한 상품의 ID와 이름은 다시 사용할 수 있다
- [x] 조회한 목록(`all()`)은 복사본이라 직접 수정할 수 없다
- 결정: "같은 이름"은 `ProductName` 값이 완전히 같은 경우다. 대소문자는 구분한다 (`Item`과 `item`은 다른 이름). 대소문자를 무시하려면 `ProductName.equals`를 바꾸면 된다. **사용자 확인 필요.**
- 결정: `Products`는 저장된 상품(ID가 있는 상품)만 담는다. 새 상품은 서비스가 ID를 부여한 뒤 추가한다.
- 결정: 중복은 `IllegalArgumentException`, 없는 상품은 `NoSuchElementException`으로 구분한다. HTTP 상태로의 변환(예: 중복 400, 없음 404)은 API 단계에서 한다.
- 결정: 이 클래스는 메모리상의 규칙만 담당한다. 동시 요청에서의 중복 방지(스레드 안전성)는 아직 없다 (미해결, 상품 API 단계에서 다룬다). DB를 쓰지 않으므로 이름 대소문자 구분은 `ProductName.equals`로 결정된다.
- 보조 변경: `ProductName`에 값 기준 `equals`/`hashCode`를 추가했다 (테스트 1개).
- 결과: `ProductsTest` 14개 추가.

### 9. 이메일 도메인 `Email` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/Email.java`
- [x] `@`가 정확히 하나여야 한다
- [x] `@` 앞(로컬 파트)이 비어 있으면 거부한다
- [x] `@` 뒤는 `gmail.com`만 허용한다 (대소문자 무시). `naver.com`, `gmail.co`, `gmail.com.kr`, `sub.gmail.com`은 거부한다
- [x] 공백 문자가 있으면 거부한다 (앞뒤 공백 포함)
- [x] `null`, 빈 값, 공백만 있는 값은 거부한다
- [x] 값이 같으면 같은 이메일이다
- 결과: `EmailTest` 32개 추가.

### 10. 비밀번호 도메인 `Password` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/Password.java`
- [x] 8자 이상만 허용한다
- [x] 영문이 없으면 거부한다
- [x] 숫자가 없으면 거부한다
- [x] 특수문자가 없으면 거부한다
- [x] 허용 문자(영문, 숫자, ASCII 특수문자) 외의 문자(공백, 한글, 악센트 문자)가 있으면 거부한다
- [x] `null`, 빈 값은 거부한다
- [x] `toString()`이 비밀번호를 노출하지 않는다
- [x] 값이 같으면 같은 비밀번호다
- 결과: `PasswordTest` 30개 추가. `toString` 테스트는 기본 `Object.toString`으로도 통과하지만, 이후 `record` 등으로 바꿔 값이 노출되는 실수를 막는 용도다.

### 11. 위시 리스트 도메인 `WishList` (단위 테스트)
위치: `src/main/java/shopping/domain/WishList.java`
- [x] 처음에는 비어 있다
- [x] 상품을 추가하면 담은 순서대로 조회된다
- [x] 같은 ID의 상품은 추가할 수 없다 (예외, 기존 목록 유지)
- [x] ID가 없는 상품은 추가할 수 없다
- [x] `null`은 추가할 수 없다
- [x] 상품 ID로 삭제한다. 없는 상품을 삭제하면 `NoSuchElementException`
- [x] 삭제한 상품은 다시 추가할 수 있다
- [x] 조회한 목록은 직접 수정할 수 없다
- 결과: `WishListTest` 11개 추가.

### 12. 회원 도메인 `Member` (단위 테스트)
위치: `src/main/java/shopping/domain/Member.java`
- [x] 이메일과 비밀번호로 회원을 생성하면 위시 리스트는 비어 있다
- [x] 이메일, 비밀번호가 `null`이면 생성할 수 없다
- [x] 회원의 위시 리스트에 상품을 추가/삭제하면 회원의 위시 리스트에 반영된다
- [x] 회원마다 위시 리스트가 따로다 (한 회원의 위시 리스트가 다른 회원에 영향을 주지 않는다)
- [x] 회원 ID `MemberId` (값 객체): 1 이상의 `long`, 0 이하는 `IllegalArgumentException`, 값이 같으면 같은 ID (`record`). `MemberIdTest` 7개.
- [x] 회원이 ID를 가진다 (사용자 요청, `Product`와 같은 방식):
  - `Member(email, password)`: 아직 가입 전인 회원. ID가 없다.
  - `Member(id, email, password)`: 가입된 회원. ID는 `null`일 수 없다.
  - `id()`는 `Optional<MemberId>`. 한 번 정해지면 바뀌지 않는다 (`final`).
- 결과: `MemberTest` 8개 (ID 관련 3개 포함).

### 13. 상품 API
- [x] `Products`를 스레드 안전하게 만든다 (동시 추가/수정에서도 같은 ID·이름 중복이 생기지 않도록). 테스트: 여러 스레드가 같은 이름을 동시에 추가해도 하나만 들어간다
  - 방식 *[결정]*: `Products`의 모든 공개 메서드를 `synchronized`로 만들어 같은 락을 쓴다. 중복 검사와 추가가 한 덩어리로 실행되어야 하므로 락을 잘게 나누지 않았다.
  - 테스트 (`ProductsConcurrencyTest`, 4개): 같은 이름 동시 추가, 같은 ID 동시 추가, 서로 다른 상품 400개 동시 추가, 서로 다른 상품을 같은 이름으로 동시 수정. 8개 스레드를 동시에 출발시키는 시도를 300번 반복한다.
  - Red 확인: 동기화 전에는 4개 모두 3번 연속 실패했다 (중복 허용 또는 동시 수정 오류). 동기화 후에는 5번 반복해도 통과하고, 전체 테스트를 3번 반복 실행해도 224개 모두 통과했다.
  - 한계 (미해결):
    - `findById`가 돌려주는 `Product`는 수정 가능한 객체다. 이 객체를 `Products` 밖에서 직접 `update`하면 락을 거치지 않아 이름 중복 검사를 우회한다. 서비스는 반드시 `Products.update(...)`로 수정한다.
    - ID 부여와 `Products.add`는 서로 다른 호출이라 서비스에서 한 덩어리로 묶었다 (아래 "상품 서비스" 참고).
    - `WishList`와 `Member`는 아직 스레드 안전하지 않다. 같은 회원의 위시 리스트에 동시에 요청이 오는 경우는 위시 리스트 API 단계에서 다룬다.
- [x] 상품 서비스: ID 부여, `Products`를 이용한 생성/조회/목록/수정/삭제
  - 위치: `src/main/java/shopping/service/ProductService.java`, 테스트 `ProductServiceTest`(13개), `ProductServiceConcurrencyTest`(2개)
  - 메서드: `create(name, price, imageUrl)`, `get(id)`, `list()`, `update(id, name, price, imageUrl)`, `delete(id)`. HTTP와 무관하게 원시 값(문자열, 숫자)을 받아 도메인 객체로 만든다.
  - 결정: ID는 1부터 순서대로 부여하고, **추가에 성공했을 때만** 다음 ID로 넘어간다. 규칙 위반, 비속어, 이름 중복으로 실패하면 ID를 소비하지 않아 번호가 건너뛰지 않는다. (처음 계획은 `AtomicLong`이었으나, 실패한 요청이 ID를 소비하는 것을 피하려고 `synchronized` 메서드 안의 `long` 카운터로 바꿨다.)
  - 결정: `create`, `update`, `delete`는 `synchronized`로 한 덩어리로 실행한다. 이미 `Products`도 자체 락을 쓰므로 락이 두 겹이지만, 락을 잡는 순서가 항상 서비스 → `Products`라 교착 상태는 생기지 않는다.
  - 오류: 규칙 위반과 이름/ID 중복은 `IllegalArgumentException`, 없는 상품은 `NoSuchElementException`, 비속어 검사 실패는 `ProfanityCheckException`이 그대로 올라온다. HTTP 응답으로의 변환은 컨트롤러/공통 예외 처리 단계에서 한다.
  - 동시성 테스트: 서로 다른 이름 8개를 동시에 만들면 ID가 1~8로 겹침 없이 부여된다 (200번 반복). 확인용으로 `create`의 `synchronized`를 잠시 빼봤더니 이 테스트가 실패했고, 다시 넣어 통과시켰다. 같은 이름을 동시에 만드는 테스트는 `synchronized`가 없어도 통과한다. 중복 방지는 `Products`의 락이 이미 맡고 있기 때문이다.
  - 아직 스프링 빈으로 등록하지 않았다. `ProductService`는 `Products`와 비속어 검사기가 필요한데 그 빈이 아직 없어서, 다음 항목(비속어 검사기를 빈으로 등록)에서 함께 등록한다. 지금 `@Service`를 붙이면 앱이 시작되지 않는다.
  - 한계: `get`/`list`가 돌려주는 `Product`는 수정 가능한 객체다. 컨트롤러는 이 객체를 직접 고치지 않고 `update`만 쓴다.
- [x] 비속어 검사기(`PurgoMalumProfanityChecker`)를 빈으로 등록하고 `ProductName` 생성에 연결
  - 위치: `src/main/java/shopping/config/AppConfig.java` (`@Configuration`). 등록한 빈: `RestClient`(연결 2초/읽기 3초 타임아웃), `ProfanityChecker`(PurgoMalum 구현체), `Products`(하나만 만들어 공유), `ProductService`.
  - 테스트 `BeanWiringTest`(4개, `@SpringBootTest`): 실제 비속어 검사기가 PurgoMalum 구현체로 등록됨, `Products`가 하나뿐임, 서비스가 검사기 빈으로 이름을 검사함(테스트용 가짜 검사기를 `@Primary`로 넣어 네트워크 없이 확인), 서비스가 만든 상품이 조회됨. 빈이 없을 때 `NoSuchBeanDefinitionException`으로 실패하는 것을 먼저 확인했다.
  - 연결: `ProductService`가 이름을 받을 때마다 `new ProductName(name, profanityChecker)`로 검사기를 넘긴다.
- [x] 요청/응답 JSON 형식 정하기 *[결정]*: 요청 `{"name": "...", "price": 1000, "imageUrl": "https://..."}`, 응답 `{"id": 1, "name": "...", "price": 1000, "imageUrl": "https://..."}`, 오류 `{"message": "..."}`. 문서에 필드명 규정이 없어 정했다.
  - `price`는 정수만 받는다. `1.5`, 문자열(`"abc"`), `long` 범위를 넘는 수는 400이다. 처음에는 `Long`으로 받았더니 `1.5`가 `1`로 잘려 통과해서(JSON 라이브러리의 기본 변환), `BigDecimal`로 받아 정수인지 직접 확인하도록 바꿨다.
  - 필수 값이 빠지면(`name`, `price`, `imageUrl` 없음 또는 `null`) 400이다.
- [x] 상품 생성 `POST /api/products`: 성공 시 `201`과 `Location: /api/products/{id}`, 생성된 상품 본문
- [x] 상품 조회 `GET /api/products/{productId}`: `200`, 없으면 `404`, ID가 0 이하이거나 숫자가 아니면 `400`
- [x] 상품 목록 조회 `GET /api/products`: `200`, 생성한 순서대로, 없으면 `[]`
- [x] 상품 수정 `PUT /api/products/{productId}`: `200`과 수정된 상품. 유효성 검사 포함(생성과 같은 규칙), 없으면 `404`, 실패하면 기존 값 유지, 자기 자신의 이름으로는 수정 가능
- [x] 상품 삭제 `DELETE /api/products/{productId}`: `204`, 없으면 `404`. 삭제한 ID는 다시 쓰지 않는다 (다음 상품은 다음 번호).
- [x] 잘못된 값 전달 시 오류 응답 (공통 예외 처리): `ApiExceptionHandler`(`@RestControllerAdvice`)

  | 던져지는 예외 | 응답 |
  |---|---|
  | `IllegalArgumentException` (규칙 위반, 이름 중복, 필수 값 없음) | 400 + 예외 메시지 |
  | `NoSuchElementException` (없는 상품) | 404 + 예외 메시지 |
  | JSON을 읽을 수 없음 (빈 본문, 깨진 JSON, 배열, 숫자 자리에 문자열) | 400 "요청 내용을 읽을 수 없습니다. 입력한 내용을 확인해 주세요." |
  | 경로 값이 숫자가 아님 (`/api/products/abc`) | 400 "주소에 입력한 번호가 올바르지 않습니다." |
  | `ProfanityCheckException` `BAD_REQUEST` | 500 "비속어 검사 요청에 문제가 있어 처리할 수 없습니다." |
  | `ProfanityCheckException` `SERVICE_ERROR` | 503 "비속어 검사 서비스를 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해 주세요." |
  | `ProfanityCheckException` `CONNECTION_ERROR` | 503 "비속어 검사 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요." |
  | `ProfanityCheckException` `INVALID_RESPONSE` | 502 "비속어 검사 서비스에서 올바르지 않은 응답을 받았습니다." |

  - 결정: 이름 중복도 `400`으로 응답한다 (`409`로 구분하지 않았다). 구분하려면 중복 전용 예외가 필요하다. **사용자 확인 필요.**
  - 비속어 검사 실패 응답에는 PurgoMalum 주소나 상태 코드 같은 내부 정보를 넣지 않는다 (테스트로 확인).
  - 처리하지 않은 예외(예: 지원하지 않는 Content-Type)는 스프링 기본 응답을 따른다.
- 코드: `shopping.controller` 패키지의 `ProductController`, `ProductRequest`, `ProductResponse`, `ErrorResponse`, `ApiExceptionHandler`
- 테스트: `ProductControllerTest`(MockMvc, 36개), `ProfanityCheckErrorTest`(4개). 컨트롤러 테스트는 요청마다 새 서비스를 만들어 서로 영향을 주지 않는다.
- 실제 서버 확인: `./gradlew bootRun`으로 앱을 띄우고 `curl`로 생성(실제 PurgoMalum 호출 포함), 특수문자 이름(`A&B+C/D (1)`), 비속어(`shit`), 중복 이름, `price` 1.5, 한글 이름, 조회, 목록, 수정, 삭제, 삭제 후 조회, 삭제 후 새 상품의 ID(3)를 확인했다. 모두 위 표와 같게 응답했다.

### 14. 회원 API
- [x] 회원 보관 (ID, 이메일, 비밀번호, 메모리): `shopping.domain.Members` *[결정, 사용자 요청: 회원도 ID로 관리]*
  - 내부는 `LinkedHashMap<MemberId, Member>`다. ID를 키로 써서 같은 ID의 회원은 하나만 담긴다는 것을 구조로 표현하고, 담은 순서도 유지한다. 이메일 중복은 `Products`의 이름 중복처럼 값들을 훑어 따로 검사한다.
  - `add`: ID가 없는 회원(`Member(email, password)`)은 거부, 같은 ID는 거부, 같은 이메일은 ID가 달라도 거부 (모두 `IllegalArgumentException`, 기존 회원 유지). `null`은 `NullPointerException`.
  - `findById(MemberId)`, `findByEmail(Email)`: 없으면 `Optional.empty()`, `null`은 `NullPointerException`.
  - `Products`처럼 `synchronized`로 중복 검사와 추가를 한 덩어리로 실행한다. 같은 이메일(ID는 다름) 또는 같은 ID(이메일은 다름)로 8개 스레드가 동시에 가입하는 시도를 200번 반복해도 한 명만 들어간다.
  - 테스트 `MembersTest` 10개.
- [x] 토큰 발급 저장소: `shopping.service.TokenStore` *[결정]*
  - 토큰은 `UUID.randomUUID()` 문자열이고 메모리(`ConcurrentHashMap`)에 `토큰 → 회원 ID`로 보관한다 (처음에는 이메일이었으나 회원을 ID로 관리하면서 바꿨다). 같은 회원에게 여러 번 발급하면 매번 다른 토큰이고 모두 유효하다 (만료와 폐기는 만들지 않았다).
  - `find(token)`은 모르는 토큰, `null`, 빈 값에 비어 있는 결과를 돌려준다. 인증 단계(15번)에서 이 ID로 회원을 찾는다.
  - 테스트 `TokenStoreTest` 6개.
- [x] 회원 ID 부여 *[결정]*: `MemberService.register`가 1부터 순서대로 부여하고(`synchronized`), **가입에 성공했을 때만** 다음 번호로 넘어간다. 이메일 중복이나 규칙 위반으로 실패하면 ID를 소비하지 않는다 (상품 ID와 같은 방식). 서버를 재시작하면 다시 1부터 시작한다. 가입/로그인 응답에는 회원 ID를 넣지 않는다 (토큰만 돌려준다).
- [x] 회원 가입 `POST /api/members/register` → 토큰 반환
  - 요청 `{"email": "...", "password": "..."}`, 성공 시 `201`과 `{"token": "..."}`. 응답에 비밀번호는 없다 (테스트로 확인).
  - 이메일 규칙(gmail.com 등)과 비밀번호 규칙을 어기면 `400`, 필수 값이 없거나 JSON이 깨졌으면 `400`.
  - 서비스 `shopping.service.MemberService.register`: 규칙 검증 → `Members`에 추가 → 토큰 발급. 실패하면 아무것도 남기지 않는다.
- [x] 이메일 중복 가입 방지: 이미 가입된 이메일은 `400` "이미 가입된 이메일입니다."
- [x] 로그인 `POST /api/members/login` → 이메일/비밀번호 일치 시 토큰 반환
  - 성공 시 `200`과 `{"token": "..."}`. 로그인할 때마다 새 토큰을 발급한다. 비밀번호는 대소문자를 구분한다.
  - 비밀번호 비교는 `MessageDigest.isEqual`(상수 시간 비교)로 한다.
  - 필수 값(`email`, `password`)이 없으면 `400`.
- [x] 일치하지 않으면 오류 응답 (401) *[결정]*: `ApiExceptionHandler`가 `LoginFailedException`을 `401` `{"message": "이메일 또는 비밀번호가 올바르지 않습니다."}`로 바꾼다.
  - *[결정]* 가입하지 않은 이메일, 비밀번호 틀림, 형식이 틀린 이메일/비밀번호(예: `user`, `short`)를 **모두 같은 401과 같은 메시지**로 응답한다. 어느 이메일이 가입되어 있는지 응답으로 알 수 없게 하기 위함이다.
  - 빈 문자열(`""`)은 형식이 틀린 값이라 401이고, 필드가 아예 없거나 `null`이면 400이다.
- 코드: `Members`, `TokenStore`, `MemberService`, `LoginFailedException`, `MemberController`, `MemberRequest`, `TokenResponse`, 그리고 `AppConfig`에 `Members`/`TokenStore`/`MemberService` 빈 등록. 테스트: `MemberServiceTest`(27개), `MemberControllerTest`(MockMvc, 27개), `BeanWiringTest`에 2개 추가.
- 실제 서버 확인: `bootRun` 후 `curl`로 가입(201), 같은 이메일 재가입(400), `naver.com` 가입(400), 특수문자 없는 비밀번호(400), 필수 값 없음(400), 로그인 성공(200), 비밀번호 틀림(401), 없는 이메일(401), 로그인 필수 값 없음(400)을 확인했다. 서버 로그에는 비밀번호 문자열이 나타나지 않았다.
- 전체 테스트 352개 통과 (3번 반복).
- 한계 (미해결):
  - 비밀번호를 해시하지 않고 `Member`가 입력값을 그대로 가진다 (메모리에만 있고 `toString`과 응답, 로그에는 나오지 않는다). 해시하려면 `Member`가 해시된 비밀번호를 가지도록 도메인을 바꿔야 한다.
  - 같은 이메일의 대소문자 구분: `User@gmail.com`과 `user@gmail.com`은 다른 이메일로 가입된다.
  - 로그인 시도 횟수 제한이 없다 (무차별 대입 방어 없음).
  - 토큰은 만료되지 않고 서버를 재시작하면 모두 사라진다.

### 15. 인증
- [x] 토큰 발급 및 검증 로직: 발급과 토큰 → 회원 ID 조회는 "14. 회원 API"의 `TokenStore`, 헤더 검증과 회원 식별은 `shopping.service.AuthService`
  - `Authorization: Bearer {token}` 헤더를 읽는다. `Bearer`는 대소문자를 구분하지 않고(`bearer`, `BEARER` 허용), 형식은 `Bearer` + 공백 하나 + 공백 없는 토큰이다 *[결정]*.
  - 다음은 모두 같은 실패로 처리한다: 헤더 없음, 빈 값, `Bearer`만 있음, 다른 방식(`Basic ...`), 토큰만 보냄, 모르는 토큰, 토큰은 있지만 그 ID의 회원이 없음. 실패 메시지는 "로그인이 필요합니다. 먼저 로그인해 주세요."로 이유와 상관없이 같다.
  - 테스트 `AuthServiceTest` 17개.
- [x] 요청 헤더에서 토큰을 읽어 회원을 식별 (ArgumentResolver) *[결정]*
  - 인터셉터 대신 ArgumentResolver를 택했다. 컨트롤러 메서드에 `@LoginMember Member member`를 쓰면 식별된 회원이 주입되고, 그 파라미터가 없는 API(상품, 회원 가입/로그인)는 인증을 요구하지 않는다. 인증이 필요한 API가 메서드 선언에서 바로 보인다.
  - `LoginMember`(애너테이션), `LoginMemberArgumentResolver`, 등록은 `shopping.config.WebConfig`.
  - 회원 파라미터를 `@RequestBody`보다 앞에 두어 인증이 본문 검사보다 먼저 처리된다 (본문이 깨졌어도 토큰이 없으면 401, 테스트로 확인).
- [x] 토큰이 없거나 유효하지 않으면 401: `UnauthorizedException` → `ApiExceptionHandler`가 `401` `{"message": "로그인이 필요합니다. 먼저 로그인해 주세요."}`
- 인증이 필요한 API: 위시 리스트 3개. 상품 API와 회원 API는 토큰 없이 사용한다 (문서에 상품 API 인증 요구가 없다).
- 한계: 토큰이 만료되지 않고 폐기(로그아웃) 수단이 없다. 서버를 재시작하면 모든 토큰이 사라져 다시 로그인해야 한다.

### 16. 위시 리스트 API (인증 필요)
- [x] 위시 보관 (회원, 상품, 메모리): 회원마다 `Member`의 `WishList`가 있고(도메인 "11. 위시 리스트"), 서비스는 `shopping.service.WishService`
  - `WishList`를 스레드 안전하게 만들었다 (`synchronized`). 같은 회원의 같은 상품을 8개 스레드가 동시에 추가하는 시도를 300번 반복해도 하나만 담기고, 서로 다른 상품 400개를 동시에 담아도 모두 담긴다. 동기화 전에는 두 테스트 모두 3번 연속 실패했다 (`WishListConcurrencyTest` 2개).
  - 담는 것은 상품 정보 복사본이 아니라 같은 `Product` 객체다. 그래서 상품을 수정하면 위시 리스트에도 바로 반영된다 (테스트로 확인).
  - 담은 뒤 카탈로그에서 상품이 삭제되면 위시 리스트 조회에서 보이지 않게 거른다 *[결정]*. 목록에서 실제로 지우지는 않아서, 삭제는 그대로 가능하다 (204). 삭제한 상품은 다시 담을 수 없다 (404).
  - 테스트 `WishServiceTest` 11개.
- [x] `wishId` 결정 *[결정]*: 위시 항목의 `wishId`는 **담은 상품의 ID와 같다.** 도메인(`WishList`)이 상품 ID로 항목을 구분하고 별도의 위시 번호를 두지 않기 때문이다. `DELETE /api/wishes/{wishId}`의 `wishId`에 상품 ID를 넣는다. 응답에는 `wishId`와 `product.id`가 둘 다 있고 값이 같다.
- [x] 위시 추가 `POST /api/wishes`: 요청 `{"productId": 1}`, 성공 시 `201`과 `{"wishId": 1, "product": {"id", "name", "price", "imageUrl"}}`. `Location` 헤더는 없다 (위시 단건 조회 API가 없다).
  - `productId`가 없거나 `null`, 0 이하, 정수가 아님(`"abc"`, `1.5`), JSON이 깨졌으면 `400`. 상품 가격과 같은 이유로 `BigDecimal`로 받아 정수인지 확인한다.
- [x] 존재하지 않는 상품 추가 시 오류 응답: `404` "존재하지 않는 상품입니다. (상품 번호: N)" *[결정]* (요청 형식은 맞지만 가리키는 상품이 없어서 404로 했다). 이미 담은 상품을 다시 담으면 `400`.
- [x] 위시 조회 `GET /api/wishes` (본인 것만): `200`, 담은 순서대로, 없으면 `[]`. 다른 회원이 담은 상품은 보이지 않는다.
- [x] 위시 삭제 `DELETE /api/wishes/{wishId}` (본인 것만): `204`. 내 목록에 없으면 `404` (다른 회원이 담은 항목이어도 같은 `404`라서 다른 회원의 위시가 있는지 알 수 없다). `wishId`가 0 이하거나 숫자가 아니면 `400`.
- 코드: `shopping.controller`의 `WishController`, `WishRequest`, `WishResponse`, `LoginMember`, `LoginMemberArgumentResolver`, `shopping.config`의 `WebConfig`와 `AppConfig`의 `AuthService`/`WishService` 빈. 테스트: `WishControllerTest`(MockMvc, 30개), 실제 앱 설정으로 요청이 끝까지 동작하는지 보는 `ApiIntegrationTest`(4개: 토큰 없으면 401, 상품 API는 공개, 가입→상품 등록→위시 추가/조회/삭제, 로그인 토큰 사용), `BeanWiringTest`(6개).
- 실제 서버 확인: `bootRun` 후 `curl`로 토큰 없음(401), 잘못된 토큰(401), 추가(201), 중복 추가(400), 없는 상품(404), `productId` 없음(400), 목록, 다른 회원 목록(`[]`), 다른 회원의 위시 삭제 시도(404), 본인 삭제(204), 카탈로그에서 상품 삭제 후 목록(`[]`), 상품 API 공개를 확인했다.
- 한계: 위시 리스트와 회원, 토큰은 서버를 재시작하면 사라진다. 상품을 삭제하면 위시 리스트에는 숨겨질 뿐 메모리에 남아 있다.

### 17. 마무리
- [x] 프로그래밍 요구 사항 점검 (depth 2 이하, 메서드 15줄 이하, else/switch/3항 연산자 없음)
  - 운영 코드(`src/main/java`)를 검색과 간단한 스크립트로 점검했다: `else`, `switch`, 3항 연산자 없음, 15줄을 넘는 메서드 없음, 들여쓰기 깊이 3 이상인 제어문 없음. 깊이는 들여쓰기 위치로 본 근사 점검이라 눈으로 보는 리뷰를 대체하지는 않는다.
  - 테스트 코드(`src/test/java`)에서 동시성 테스트 3곳(`MembersTest`, `ProductsConcurrencyTest`, `WishListConcurrencyTest`)에 3항 연산자(`? 1 : 0`)가 있어 `if`로 바꿨다. 이후 테스트 코드에도 없음.
  - 아직 점검하지 않은 것: Google Java Style Guide의 세부 규칙(임포트 순서 등)과 100자를 넘는 줄은 도구로 점검하지 않았다. 테스트 메서드는 15줄 규칙을 적용하지 않았다 (일부 테스트 메서드가 15줄을 넘는다).
- [x] `./gradlew clean test` 전체 통과: 432개 테스트, 실패 0, 3번 반복 실행해도 같은 결과 (동시성 테스트 포함). (이후 `null` 검사 통일, `Map` 변경, 회원 ID 추가를 거쳐 다시 확인한 값이다.)
- [ ] AI 도구 활용 기록 작성 (아래 섹션)
- [ ] 과제 진행 소감 작성

## 5. TDD 진행 방식

1. 기능 목록에서 항목 하나를 고른다.
2. 실패하는 테스트를 먼저 작성하고 실패하는 것을 확인한다.
3. 테스트를 통과시키는 최소한의 코드를 작성한다.
4. 리팩터링한다 (요구 사항 규칙 점검).
5. 항목 단위로 커밋한다.

테스트 계층:
- 단위 테스트: 상품 이름 규칙 등 도메인 로직 (JUnit + AssertJ)
- 통합 테스트: API 동작 (`spring-boot-starter-webmvc-test`, DB 없이 메모리 저장)

## 6. AI 도구 활용 기록

Claude Code를 활용해 과제를 진행했다. AI가 작성한 코드를 그대로 사용하기보다, 내가 구현 방향과 규칙을 정하고 AI의 도움을 받아 구현과 검증을 진행했다.

먼저 과제 문서를 함께 읽고 README에 기능 목록을 작성했다. 문서에 명시된 요구 사항은 [문서], 직접 정한 내용은 **[결정]**으로 구분해 AI가 추측한 내용과 실제 요구 사항이 섞이지 않도록 했다. 처음 제안받은 JPA와 Flyway도 문서에 DB 요구가 없다는 것을 확인한 뒤 해당 구현까지는 제외하고, 메모리 저장 방식을 선택했다. 정해지지 않은 규칙은 AI에게 선택지와 이유를 설명하게 한 뒤 직접 결정했다.

구현은 도메인부터 시작해 서비스, API, 인증 순서로 진행했다. 테스트를 먼저 작성하고 실패를 확인한 뒤 통과하는 코드를 구현했으며, 기능 단위로 커밋했다. 생성된 코드도 직접 읽으며 메서드 이름, 객체의 책임, 오류 메시지를 수정했다. 특히 객체의 값을 꺼내 외부에서 비교하던 코드를 product.hasName(name)처럼 객체가 직접 판단하는 방식으로 바꾸며 객체의 책임을 배웠다.

검증 과정에서는 상품 가격의 소수점이 잘려 통과하는 문제와 동시 요청 시 중복이 발생하는 문제를 발견해 수정했다. 동시성 제어 코드를 제거했을 때 테스트가 실패하는지도 확인했다.

이번 과제를 통해 AI에 명확한 기준을 전달하고, 결과를 직접 이해하고 검증하는 과정의 중요성을 배웠다. 다만 DB 연결까지 구현하지 못한 점은 아쉽다. 이후에는 데이터가 서버 재시작 후에도 유지되도록 DB를 연동해 보고 싶다.

## 7. 과제 진행 소감

AI를 활용하면서 동시성 이슈에 대해 깊이 생각해 볼 수 있었다. 서버는 여러 스레드에서 요청을 동시에 처리하기 때문에, 중복 검사와 데이터 추가가 따로 실행되면 같은 이름이나 이메일이 중복으로 저장될 수 있다. 이를 동시성 테스트의 실패로 직접 확인했다. synchronized를 적용하면 테스트가 통과하고, 확인을 위해 일부러 제거하면 다시 실패하는 과정을 보면서 테스트가 실제로 문제를 잡아낸다는 점도 확인할 수 있었다.

AI가 코드를 빠르게 작성해 주지만, 함수 이름이 의도한 역할을 잘 드러내는지는 함께 검토할 필요가 있었다. 예를 들어 validateLocalPart는 이름만으로 무엇을 검사하는지 알기 어려웠다. 여러 후보를 비교한 뒤 validateUsernameNotEmpty처럼 검사하는 규칙이 드러나는 이름으로 바꿨다. 객체의 값을 꺼내 외부에서 비교하던 코드도 hasName, matchesPassword, addWish 같은 메서드로 옮겨, 객체가 직접 판단하고 처리하도록 수정했다.

TDD로 개발하니 리팩토링 과정에서 기존 요구 사항을 여전히 만족하는지 확인하기 편리했다. List를 Map으로 바꾸거나, null 검사 방식을 통일하거나, 객체가 직접 처리하는 구조로 변경할 때도 기존 테스트를 통해 검증한 동작이 유지되는지 확인할 수 있었다. 회원을 ID로 관리하도록 요구 사항을 변경했을 때도 테스트를 먼저 수정하고 구현을 맞춰 나갈 수 있어 부담이 줄었다.

정해지지 않은 내용을 AI가 임의로 추측해 채우지 않도록 한 것도 도움이 됐다. 문서에 명시된 요구 사항과 직접 내린 결정을 구분해 README에 기록했고, 나중에 “이 규칙은 왜 이렇게 정했지?”라는 의문이 들 때 근거를 바로 찾을 수 있었다.

아쉬운 점은 DB 연동까지 구현하지 못했다는 것이다. 이후에는 서버를 재시작해도 데이터가 유지되도록 DB를 연동해 보고 싶다.
