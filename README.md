# spring-shopping-precourse

온라인 쇼핑몰을 위한 간단한 HTTP API를 스프링 프레임워크로 구현한다. 요청과 응답은 JSON 형식이다.

> 이 문서는 `쇼핑 - 프리코스` 과제 문서에 적힌 내용(**[문서]**)과, 문서에 없어서 직접 정한 내용(**[결정]**)을 구분해서 적는다.
> 개발은 TDD(테스트 작성 → 실패 확인 → 구현 → 리팩터링)로 진행한다.

---

## 1. 프로젝트 현황 (저장소 확인 결과)

- Gradle(Kotlin DSL), Spring Boot 4.1.2, Java toolchain 21
- 의존성(처음 상태): JPA, Flyway(+MySQL), Thymeleaf, Validation, WebMVC, H2(runtime), MySQL(runtime). DB 관련 항목은 "13. 상품 API"에서 제거했다.
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
| 비밀번호 저장 | 평문 저장 금지, 해시해서 저장 | 보안 기본 원칙 (해시 방식은 구현 시 확정) |
| 인증 대상 | 위시 리스트 API만 인증 필요 | 문서가 "인증된 사용자를 대상으로" 위시 리스트를 명시함. 상품 API 인증은 문서에 없음 |
| 이메일 중복 | 이미 가입된 이메일로 가입 시 400 응답 | 문서에 규칙 없음 |
| 이메일/비밀번호 검증 | 이메일 형식 검증, 비밀번호 빈 값 금지 | 문서에 규칙 없음 |
| 가격 | 0 이상의 정수 | 문서에 규칙 없음 |
| 이미지 URL | 빈 값/공백만 있는 값 금지, `https`만 허용(`http` 불가) | 문서에 규칙 없음. 값 객체 `ProductImage`로 검증 |
| 위시 중복 추가 | 같은 상품 중복 추가 시 400 응답 | 문서에 규칙 없음 |
| 위시 삭제 권한 | 본인 위시만 삭제 가능, 타인 것이면 403 | 문서에 규칙 없음 |
| 오류 응답 형식 | 상태 코드 + `{"message": "..."}` | 문서에 "적절한 오류 응답"이라고만 함 |
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
- 비밀번호 해시 방식과 필요한 의존성
- ~~상품 이름이 허용하는 문자 범위~~ → 영문, 숫자, 공백, 허용 특수문자로 결정 (4번 `ProductName` 참고)

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
- 상품 ID는 서버가 1부터 순서대로 부여한다 (`AtomicLong`, 서비스 계층에서 부여) *[결정]*. 서버 재시작 시 다시 1부터 시작한다.
- 저장소 인터페이스는 지금 만들지 않는다. 필요해지면(예: DB를 나중에 붙이는 경우) 그때 추출한다 *[결정]*.

**동시 요청 (보완 필요)**
- 스프링은 요청을 여러 스레드에서 동시에 처리한다. 현재 `Products`는 일반 `ArrayList`를 쓰므로 동시에 접근하면 중복 검사가 뚫리거나 오류가 날 수 있다.
- API에서 쓰기 전에 `Products`를 스레드 안전하게 만든다 (TODO 9번에 항목 추가).

**의존성 현황 (사실)**
- 처음에는 `build.gradle.kts`에 이 방침에서 쓰지 않는 DB 관련 항목이 들어 있었고, 앱을 실행하면 내장 H2, Flyway, JPA가 자동으로 시작됐다.
- 상품 API 단계 첫 항목에서 이 항목들을 제거했다 (제거 목록과 확인 결과는 "13. 상품 API" 참고).

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
- 비밀번호를 해시해서 보관할지는 아직 정하지 않았다 (회원 가입/로그인 단계에서 결정).

**위시 리스트 세부 규칙** *[결정]*
- 저장된 상품(ID가 있는 `Product`)만 담는다. ID가 없는 상품은 거부한다.
- 같은 ID의 상품은 두 번 담을 수 없다 (예외, 기존 목록 유지).
- 삭제는 상품 ID로 한다. 담겨 있지 않은 상품을 삭제하면 `NoSuchElementException`.
- 조회한 목록은 복사본이라 직접 수정할 수 없다. 담은 순서대로 조회된다.
- `null`은 거부한다 (`NullPointerException`).
- 과제 API 명세는 삭제를 `/api/wishes/{wishId}`로 한다. 도메인은 상품 ID로 삭제하므로, `wishId`를 상품 ID로 볼지는 위시 리스트 API 단계에서 정한다 (미결정).
- 이 단계에서는 인증, 회원 ID, 회원 중복 가입 방지, 토큰은 다루지 않는다.

**회원 세부 규칙** *[결정]*
- 이메일과 비밀번호로 생성하고, 위시 리스트는 비어 있는 상태로 시작한다.
- 이메일, 비밀번호가 `null`이면 `NullPointerException`.
- 회원 ID는 아직 두지 않는다 (회원 API 단계에서 필요하면 추가한다).

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
- [ ] 공백 포함 15자 이하만 허용 (16자 이상이면 예외)
- [ ] 빈 값/공백만 있는 이름은 허용하지 않음 *[결정]* (null도 예외)
- [ ] 허용 특수문자 `( ) [ ] + - & / _` 는 통과
- [ ] 그 외 특수문자는 예외
- 결정: 상품 이름은 **영문(A-Z, a-z), 숫자, 공백, 허용 특수문자 `( ) [ ] + - & / _`만** 허용한다. 문서에 허용 문자 범위가 없어 정했다. 이유: 비속어 검사에 쓰는 PurgoMalum이 영문 비속어만 걸러내는 것을 확인했다 (`shit`, `fuck`은 true, `시발`, `씨발`, `ㅅㅂ`, `병신`, `좆`은 false). 검사할 수 없는 한글 등을 입력받지 않도록 문자 범위를 영문으로 제한한다.
- 결정: 숫자는 허용한다. (요청에서는 "영문과 허용 특수문자"만 언급했고, `Galaxy S24` 같은 이름을 위해 숫자를 포함했다.)
- [ ] 한글, 한자, 일본어, 악센트 문자, 전각 문자, 이모지 등 영문이 아닌 글자는 허용하지 않음 *[결정]*: 허용 목록(영문·숫자·공백·허용 특수문자) 밖의 문자는 모두 예외다.
- 허용 문자가 모두 ASCII이므로 글자 수는 `String.length()`로 센다. (BMP 밖 문자 고려는 필요 없어 제거했다.)

### 2. 비속어 검사기를 `ProductName`에 연결
- [ ] 비속어 포함 시 예외가 나는 테스트 작성 (실패 확인)
- [ ] 비속어 검사 방법 결정 및 연결 (인터페이스 정의 + 테스트용 대체 구현(Fake) 포함 여부는 이때 확정)
- [ ] 기존 `ProductName` 테스트 수정 사항과 불편했던 점을 기록

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
- ~~`null` 검사기를 넘기면 `NullPointerException`이 난다. 테스트도 없다.~~ → 해결: 생성자 첫 줄에서 `Objects.requireNonNull`로 즉시 실패시키고(값 검증보다 먼저), 테스트 추가.
- 현재 비속어 테스트는 "검사기가 true를 주면 예외를 던진다"만 증명한다. 실제 비속어 판정은 3번(PurgoMalum 구현체)이 끝나야 검증된다.

### 3. 비속어 검사 구현 (PurgoMalum)
- [ ] PurgoMalum API 연동 구현체 (`ProfanityChecker` 구현, `GET https://www.purgomalum.com/service/containsprofanity?text=...`): `shopping.infrastructure.PurgoMalumProfanityChecker`
- [ ] 정상 응답 처리: 200 + `true`는 비속어 있음, 200 + `false`는 없음
- [ ] 오류 응답 처리 (아래 표): `shopping.domain.ProfanityCheckException`의 `Reason`(`BAD_REQUEST`, `SERVICE_ERROR`, `CONNECTION_ERROR`, `INVALID_RESPONSE`)으로 구분. HTTP 상태/메시지로의 변환은 API 단계에서 한다.
- [ ] 연동 구현체 테스트 (외부 호출은 `MockRestServiceServer`와 로컬 가짜 서버로 검증, 실제 PurgoMalum 호출은 테스트에 넣지 않는다): 총 29개
- [ ] 요청 시 이름을 쿼리 값으로 인코딩 (`&`, `+`, `/`, 공백, 괄호가 포함된 이름도 안전하게 전달됨을 테스트로 확인)
- [ ] 타임아웃 적용: `PurgoMalumRestClientFactory` (연결 2초, 읽기 3초 *[결정]*). 로컬 가짜 서버가 느리게 응답하는 경우로 검증.
- [ ] 응답 본문은 정확히 `true`/`false`(소문자)만 인정. 그 외(`True`, 빈 본문, HTML, 204/302)는 `INVALID_RESPONSE`.
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
- [ ] 음수는 허용하지 않음 (예외)
- [ ] 0은 허용 *[결정]*
- [ ] 양수는 허용
- 결정: 가격 타입은 `long`이다. 문서에 가격 범위 규정이 없어 정수 중 넓은 타입을 택했다. 상한은 두지 않는다 (`Long.MAX_VALUE`까지 허용). 소수(원 미만 단위)는 다루지 않는다.
- 위반 시 `IllegalArgumentException`을 던진다.
- 결과: 테스트 7개 추가 (`ProductPriceTest`).

### 5. 상품 이미지 도메인 `ProductImage` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/ProductImage.java`
- [ ] 빈 값/공백만 있는 값(`null` 포함)은 허용하지 않음 *[결정]*
- [ ] `https`만 허용한다. `http`는 허용하지 않음 *[결정]* (대소문자 무시: `HTTPS://`는 허용, `HTTP://`는 거부)
- [ ] `https`가 아닌 스킴은 모두 거부: `ftp`, `file`, `javascript`, `data`, 스킴 없음(`//example.com/a.png`, `example.com/a.png`)
- [ ] 형식이 올바르지 않은 URL은 거부: 호스트 없음(`https://`, `https:///a.png`), 공백 포함(앞뒤 공백 포함), `<`, `>` 같은 허용되지 않는 문자
- [ ] 정상 URL 허용: 경로, 포트, 쿼리 문자열(`?size=100&type=thumb`)이 있는 `https` URL
- 결정: 파일 확장자(`.png` 등)나 실제 이미지인지는 검사하지 않는다. 문서에 규칙이 없고, 확인하려면 외부 요청이 필요하기 때문이다.
- 결정: 길이 제한은 두지 않는다 (문서에 규칙 없음).
- 결정: 입력을 자동으로 다듬지 않는다 (`trim` 없음). 앞뒤 공백이 있으면 거부한다.
- 구현: `java.net.URI`로 파싱해 스킴과 호스트를 검사한다. 위반 시 `IllegalArgumentException`.
- 결과: 테스트 22개 추가 (`ProductImageTest`).

### 6. 상품 ID 도메인 `ProductId` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/ProductId.java`
- [ ] 1 이상의 값으로 생성할 수 있다
- [ ] 0 이하의 값은 허용하지 않음 (예외) *[결정]*
- [ ] 값이 같으면 같은 ID이다 (동등성)
- 결정: ID는 `long`이다. 1부터 시작한다고 보고 0 이하를 거부한다 (문서에 ID 규칙 없음).
- 결정: Java `record`로 만들어 `equals`/`hashCode`를 값 기준으로 얻는다. 다른 값 객체와 달리 동등성이 필요해서다 (위시 리스트, 조회 등에서 비교).
- 결과: 테스트 7개 추가 (`ProductIdTest`).

### 7. 상품 도메인 `Product` (단위 테스트)
위치: `src/main/java/shopping/domain/Product.java`
`ProductId`, `ProductName`, `ProductPrice`, `ProductImage`를 가진다.
- [ ] 이름, 가격, 이미지로 상품을 생성할 수 있다 (ID는 저장 전에는 없음)
- [ ] ID와 함께 상품을 생성할 수 있다
- [ ] 이름, 가격, 이미지는 각 값 객체의 규칙을 그대로 따른다 (잘못된 값이면 생성 불가)
- [ ] 상품 수정 시에도 같은 규칙이 적용된다
- 결정: `Product`는 원시 값이 아니라 값 객체(`ProductName`, `ProductPrice`, `ProductImage`)를 받는다. 값 객체는 만들어지는 순간 규칙을 검증하므로, 규칙을 어긴 값은 `Product`에 전달되기 전에 이미 `IllegalArgumentException`으로 막힌다. 그래서 `Product`는 규칙을 다시 검사하지 않고 `null`만 막는다 (`NullPointerException`).
- 결정: 수정은 `update(name, price, image)`로 세 값을 한 번에 바꾼다. 부분 수정은 만들지 않는다 (API 명세의 `PUT`이 전체 수정이기 때문이 아니라, 문서에 부분 수정 요구가 없기 때문이다).
- 결정: 수정이 실패하면(`null` 포함) 기존 값은 그대로 유지된다. 세 값을 모두 검사한 뒤에 대입한다.
- 결정: `Product`는 `ProductId`를 가진다. 생성자는 두 가지다.
  - `Product(name, price, image)`: 아직 저장 전인 새 상품. ID가 없다.
  - `Product(id, name, price, image)`: 저장된 상품. ID는 `null`일 수 없다.
- 결정: `id()`는 `Optional<ProductId>`를 돌려준다. 저장 전 상품은 비어 있다. `null`을 반환하지 않기 위해서다.
- 결정: ID는 한 번 정해지면 바뀌지 않는다 (`final`). `update`는 ID를 건드리지 않는다.
- 영속 저장은 하지 않는다 ("3-2. 저장 방침"). 서비스가 ID를 부여하고(`AtomicLong`) ID가 있는 `Product`를 만들어 `Products`에 넣는다.
- 결과: 테스트 12개 (`ProductTest`, ID 관련 4개 포함).

### 8. 상품 목록 도메인 `Products` (단위 테스트)
위치: `src/main/java/shopping/domain/Products.java`
상품들을 관리하며, 같은 ID나 같은 이름의 상품이 들어오는 것을 막는다 *[결정]*.
- [ ] 추가: 상품을 추가하면 추가한 순서대로 조회된다
- [ ] 같은 ID의 상품은 추가할 수 없다 (예외, 기존 목록 유지)
- [ ] 같은 이름의 상품은 추가할 수 없다 (예외, 기존 목록 유지)
- [ ] ID가 없는 상품(저장 전)은 추가할 수 없다
- [ ] `null`은 추가할 수 없다 (`NullPointerException`)
- [ ] ID로 조회한다 (없으면 `Optional.empty`)
- [ ] 수정: 다른 상품과 같은 이름으로 수정할 수 없다 (예외, 기존 값 유지). 자기 자신의 이름으로 수정하는 것은 허용한다.
- [ ] 없는 상품을 수정/삭제하면 `NoSuchElementException`
- [ ] 삭제: 삭제한 상품의 ID와 이름은 다시 사용할 수 있다
- [ ] 조회한 목록(`all()`)은 복사본이라 직접 수정할 수 없다
- 결정: "같은 이름"은 `ProductName` 값이 완전히 같은 경우다. 대소문자는 구분한다 (`Item`과 `item`은 다른 이름). 대소문자를 무시하려면 `ProductName.equals`를 바꾸면 된다. **사용자 확인 필요.**
- 결정: `Products`는 저장된 상품(ID가 있는 상품)만 담는다. 새 상품은 서비스가 ID를 부여한 뒤 추가한다.
- 결정: 중복은 `IllegalArgumentException`, 없는 상품은 `NoSuchElementException`으로 구분한다. HTTP 상태로의 변환(예: 중복 400, 없음 404)은 API 단계에서 한다.
- 결정: 이 클래스는 메모리상의 규칙만 담당한다. 동시 요청에서의 중복 방지(스레드 안전성)는 아직 없다 (미해결, 상품 API 단계에서 다룬다). DB를 쓰지 않으므로 이름 대소문자 구분은 `ProductName.equals`로 결정된다.
- 보조 변경: `ProductName`에 값 기준 `equals`/`hashCode`를 추가했다 (테스트 1개).
- 결과: `ProductsTest` 14개 추가.

### 9. 이메일 도메인 `Email` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/Email.java`
- [ ] `@`가 정확히 하나여야 한다
- [ ] `@` 앞(로컬 파트)이 비어 있으면 거부한다
- [ ] `@` 뒤는 `gmail.com`만 허용한다 (대소문자 무시). `naver.com`, `gmail.co`, `gmail.com.kr`, `sub.gmail.com`은 거부한다
- [ ] 공백 문자가 있으면 거부한다 (앞뒤 공백 포함)
- [ ] `null`, 빈 값, 공백만 있는 값은 거부한다
- [ ] 값이 같으면 같은 이메일이다
- 결과: `EmailTest` 32개 추가.

### 10. 비밀번호 도메인 `Password` (값 객체, 단위 테스트)
위치: `src/main/java/shopping/domain/Password.java`
- [ ] 8자 이상만 허용한다
- [ ] 영문이 없으면 거부한다
- [ ] 숫자가 없으면 거부한다
- [ ] 특수문자가 없으면 거부한다
- [ ] 허용 문자(영문, 숫자, ASCII 특수문자) 외의 문자(공백, 한글, 악센트 문자)가 있으면 거부한다
- [ ] `null`, 빈 값은 거부한다
- [ ] `toString()`이 비밀번호를 노출하지 않는다
- [ ] 값이 같으면 같은 비밀번호다
- 결과: `PasswordTest` 30개 추가. `toString` 테스트는 기본 `Object.toString`으로도 통과하지만, 이후 `record` 등으로 바꿔 값이 노출되는 실수를 막는 용도다.

### 11. 위시 리스트 도메인 `WishList` (단위 테스트)
위치: `src/main/java/shopping/domain/WishList.java`
- [ ] 처음에는 비어 있다
- [ ] 상품을 추가하면 담은 순서대로 조회된다
- [ ] 같은 ID의 상품은 추가할 수 없다 (예외, 기존 목록 유지)
- [ ] ID가 없는 상품은 추가할 수 없다
- [ ] `null`은 추가할 수 없다
- [ ] 상품 ID로 삭제한다. 없는 상품을 삭제하면 `NoSuchElementException`
- [ ] 삭제한 상품은 다시 추가할 수 있다
- [ ] 조회한 목록은 직접 수정할 수 없다
- 결과: `WishListTest` 11개 추가.

### 12. 회원 도메인 `Member` (단위 테스트)
위치: `src/main/java/shopping/domain/Member.java`
- [ ] 이메일과 비밀번호로 회원을 생성하면 위시 리스트는 비어 있다
- [ ] 이메일, 비밀번호가 `null`이면 생성할 수 없다
- [ ] 회원의 위시 리스트에 상품을 추가/삭제하면 회원의 위시 리스트에 반영된다
- [ ] 회원마다 위시 리스트가 따로다 (한 회원의 위시 리스트가 다른 회원에 영향을 주지 않는다)
- 결과: `MemberTest` 5개 추가. 전체 테스트 220개 통과.

### 13. 상품 API
- [ ] 의존성 정리: 쓰지 않는 DB 관련 항목을 `build.gradle.kts`에서 제거했다 ("3-2" 참고)
  - 제거한 것: `kotlin("plugin.jpa")`, `spring-boot-h2console`, `spring-boot-starter-data-jpa`, `spring-boot-starter-flyway`, `flyway-mysql`, `h2`, `mysql-connector-j`, `spring-boot-starter-data-jpa-test`, `spring-boot-starter-flyway-test` (9줄)
  - 남긴 것: Thymeleaf, Validation, WebMVC, Kotlin(`kotlin-reflect`, `jackson-module-kotlin`, `kotlin-test-junit5`), JUnit 런처. Thymeleaf는 이 방침과 무관해 건드리지 않았다.
  - 확인: 제거 전 앱을 실행하면 내장 H2, Flyway, JPA가 자동으로 시작되는 것을 로그로 확인했다 (`HikariPool`, `Flyway`, `EntityManagerFactory`). 제거 후에는 해당 로그가 없고 `Started Application`까지 정상이다. `./gradlew clean test` 220개 통과.
  - 남겨둔 것: 빈 폴더 `src/main/resources/db/migration/`(`.gitkeep`)은 지우지 않았다. Flyway를 쓰지 않으므로 필요 없지만 이번 변경 범위에서 제외했다.
- [ ] `Products`를 스레드 안전하게 만든다 (동시 추가/수정에서도 같은 ID·이름 중복이 생기지 않도록). 테스트: 여러 스레드가 같은 이름을 동시에 추가해도 하나만 들어간다
- [ ] 상품 서비스: ID 부여(`AtomicLong`), `Products`를 이용한 생성/조회/목록/수정/삭제
- [ ] 비속어 검사기(`PurgoMalumProfanityChecker`)를 빈으로 등록하고 `ProductName` 생성에 연결
- [ ] 요청/응답 JSON 형식 정하기 (상품: 이름, 가격, 이미지 URL, ID). 문서에 필드명 규정이 없으므로 `name`, `price`, `imageUrl`, `id`로 정한다 *[결정]*
- [ ] 상품 생성 `POST /api/products`
- [ ] 상품 조회 `GET /api/products/{productId}` (없으면 404)
- [ ] 상품 목록 조회 `GET /api/products`
- [ ] 상품 수정 `PUT /api/products/{productId}` (유효성 검사 포함, 없으면 404)
- [ ] 상품 삭제 `DELETE /api/products/{productId}` (없으면 404)
- [ ] 잘못된 값 전달 시 오류 응답 (공통 예외 처리)

### 14. 회원 API
- [ ] 회원 보관 (이메일, 비밀번호, 메모리)
- [ ] 회원 가입 `POST /api/members/register` → 토큰 반환
- [ ] 이메일 중복 가입 방지
- [ ] 로그인 `POST /api/members/login` → 이메일/비밀번호 일치 시 토큰 반환
- [ ] 일치하지 않으면 오류 응답 (401) *[결정]*

### 15. 인증
- [ ] 토큰 발급 및 검증 로직
- [ ] 요청 헤더에서 토큰을 읽어 회원을 식별 (인터셉터 또는 ArgumentResolver)
- [ ] 토큰이 없거나 유효하지 않으면 401

### 16. 위시 리스트 API (인증 필요)
- [ ] 위시 보관 (회원, 상품, 메모리)
- [ ] 위시 추가 `POST /api/wishes`
- [ ] 존재하지 않는 상품 추가 시 오류 응답
- [ ] 위시 조회 `GET /api/wishes` (본인 것만)
- [ ] 위시 삭제 `DELETE /api/wishes/{wishId}` (본인 것만)

### 17. 마무리
- [ ] 프로그래밍 요구 사항 점검 (depth 2 이하, 메서드 15줄 이하, else/switch/3항 연산자 없음)
- [ ] `./gradlew clean test` 전체 통과
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

- (진행하면서 작성: 활용한 방식, 코드를 어떻게 수정했는지, 무엇을 학습했는지)

## 7. 과제 진행 소감

- (제출 전에 작성)
