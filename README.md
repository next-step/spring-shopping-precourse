# spring-shopping-precourse

## 프로젝트 소개

상품 관리, 회원 인증, 위시 리스트를 제공하는 HTTP API를 구현한다.
요청과 응답은 JSON 형식을 사용한다.

## 개발 환경

- Java 21
- Spring Boot
- Gradle
- MySQL 8.4 (애플리케이션 DB)
- H2 (저장소·Service·API 통합 테스트용 DB)

## 구현할 기능

### 상품

- [x] 상품 등록
- [x] 상품 단건 조회
- [x] 상품 목록 조회
- [x] 상품 수정
- [x] 상품 삭제

### 유효성 검사 및 예외 처리

- [x] 상품명 길이 검사: 공백 포함 최대 15자
- [x] 상품명 특수문자 검사: `(`, `)`, `[`, `]`, `+`, `-`, `&`, `/`, `_`만 허용
- [x] PurgoMalum API를 통한 비속어 검사
- [x] 상품 등록과 수정 모두에 검증 적용
- [x] 잘못된 입력에 오류 응답
- [x] 존재하지 않는 상품에 오류 응답
- [x] 가격 누락·음수 및 이미지 URL 누락·형식 검사
- [x] 비속어 검사 API 장애 시 등록·수정 중단 및 503 응답
- [x] 상품명 앞뒤 공백 제거 후 검증·저장

### 회원 인증

- [x] 이메일과 비밀번호로 회원가입
- [x] 이메일과 비밀번호로 로그인
- [x] 회원가입·로그인 성공 시 토큰 발급
- [x] 이메일 정규화·중복 확인 및 BCrypt 비밀번호 검증
- [x] 회원 요청 검증 및 중복 409·로그인 실패 401 응답
- [x] 토큰을 통한 회원 인증 및 만료 검사

### 위시 리스트

- [x] 인증된 회원의 위시 리스트 조회
- [x] 위시 리스트에 상품 추가
- [x] 위시 리스트에서 상품 삭제
- [x] 다른 회원의 위시 항목 삭제 방지
- [x] 동일 회원·상품 조합 중복 방지
- [x] 상품 삭제 시 모든 회원의 해당 위시 항목 삭제

## 구현 전략

다음 역할 분리를 기준으로 구현을 진행한다.

- Controller: HTTP 요청과 응답 처리
- Service: 기능의 처리 흐름과 비즈니스 규칙 수행
- Repository: 데이터 저장과 조회
- DTO: 요청·응답 데이터 표현

상품 관리 → 입력 검증 → 회원 인증 → 위시 리스트 순서로 구현한다.
각 기능을 구현하고 테스트한 뒤, 위 기능 목록 단위로 커밋한다.
커밋 메시지는 AngularJS 커밋 메시지 규칙을 참고한다.

## 직접 결정한 정책

과제에 구체적으로 명시되지 않은 사항은 아래 정책을 기준으로 구현한다.
상품·회원·토큰 인증·위시 리스트의 현재 동작을 기록한다.

| 항목 | 정책 | 선택 이유 |
| --- | --- | --- |
| 데이터 저장 | 애플리케이션은 MySQL과 JPA 사용. Service 테스트는 H2 사용 | MySQL에 데이터를 유지하고 테스트를 개발 DB와 분리 |
| 상품명 | 등록·수정 DTO 생성 시 `strip()`으로 앞뒤 공백 제거 후 검증·저장. 내부 공백은 유지하고 최대 15자 검사. 빈 값·공백만 있는 값 거절 | 상품명을 일관되게 관리하고 정규화된 값으로 비속어 검사 및 저장 |
| 가격 | 필수 입력, 정수이며 0원 이상 허용 | 무료 상품을 허용하고 음수 가격 방지 |
| 이미지 URL | 필수 입력, `http`·`https` 주소만 허용 | 이미지 주소의 기본 형식 검사 |
| 이메일 | 필수 입력, 이메일 형식 검사. 앞뒤 공백 제거·소문자 변환 후 중복 검사 | 같은 이메일의 중복 가입 방지 |
| 비밀번호 | 8~64자이면서 UTF-8 기준 최대 72바이트. 앞뒤 공백을 제거하지 않음 | BCrypt 입력 제한을 지키고 입력한 값을 그대로 취급 |
| 비밀번호 저장 | 원문 대신 BCrypt 해시 저장 | 원문 비밀번호가 저장되지 않도록 처리 |
| 토큰 발급·저장 | SecureRandom 32바이트를 URL-safe Base64로 인코딩. DB에는 SHA-256 해시·회원 연결·24시간 만료 시각 저장 | 무작위 토큰 발급 및 원문 토큰의 DB 저장 방지 |
| 토큰 검증·전달 | `Authorization: Bearer {token}` 헤더로 전달하고 DB 해시와 만료 시각으로 인증. 만료 시각부터 사용 불가 | 위시 리스트 접근 제어 및 만료 토큰 거절 |
| 상품 API 인증 | 인증 없이 사용 가능 | PDF가 인증을 명시한 위시 리스트부터 인증 적용 |
| 위시 리스트 중복 | 같은 회원의 같은 상품 중복 추가는 409. DB 유일성 제약도 적용 | 중복 항목 방지. 다른 회원은 같은 상품 추가 가능 |
| 위시 삭제 권한 | 토큰의 회원 ID로 소유권 확인. 타인의 항목은 403, 없는 항목은 404 | 회원별 데이터 접근 제한 |
| 상품 삭제 | 해당 상품을 참조하는 모든 회원의 위시 항목을 같은 트랜잭션에서 삭제 | 존재하지 않는 상품이 위시 리스트에 남는 것 방지 |
| PurgoMalum API 장애 | 상품 등록·수정을 중단하고 재시도 가능한 오류 반환 | 비속어 검증을 거치지 않은 상품이 저장되는 것 방지 |

로그인에도 회원가입과 동일한 이메일 정규화 규칙을 적용한다.
이메일은 정규화 후 최대 254자로 제한하고 DB의 유일성 제약으로도 중복을 방지한다.
사전 중복 검사를 통과한 뒤 DB에서 발생한 이메일 중복도 409로 변환한다.
존재하지 않는 이메일과 잘못된 비밀번호는 동일한 로그인 실패 메시지를 반환한다.
없는 회원도 더미 해시로 비밀번호 비교를 수행해 로그인 처리 시간 차이를 줄인다.
회원가입과 토큰 저장은 같은 트랜잭션으로 처리하므로 토큰 저장 실패 시 회원가입도 롤백된다.
위시 API는 인터셉터에서 요청 본문 검증 전에 토큰을 인증하고, 확인한 회원 ID를 요청 속성으로 전달한다.
회원 ID를 요청 본문이나 쿼리 매개변수로 지정해 다른 회원의 위시를 조작할 수 없다.
Bearer 인증 방식 이름은 대소문자를 구분하지 않으며 상품·회원가입·로그인 API에는 토큰을 요구하지 않는다.
위시 추가와 상품 삭제는 동일한 상품 행에 쓰기 잠금을 사용해 동시 요청을 조정한다.
허용 특수문자와 비속어 검사 규칙은 상품 등록·수정 모두에 적용한다.
PurgoMalum 연결 및 응답 대기 시간은 각각 3초로 설정한다.
정확한 `true`·`false` 이외의 응답도 서비스 장애로 처리한다.

### 오류 응답 정책

현재 구현된 오류 응답:

| 상황 | HTTP 상태 코드 | 오류 코드 |
| --- | --- | --- |
| DTO 검증 실패·잘못된 JSON·값의 타입 오류 | `400 Bad Request` | `INVALID_REQUEST` |
| 상품명 비속어 감지 | `400 Bad Request` | `PROFANITY_DETECTED` |
| 중복 이메일 | `409 Conflict` | `DUPLICATE_EMAIL` |
| 이메일 또는 비밀번호 불일치 | `401 Unauthorized` | `INVALID_CREDENTIALS` |
| 토큰 누락·잘못된 Bearer 형식·미등록·만료 | `401 Unauthorized` | `INVALID_TOKEN` |
| 다른 회원의 위시 삭제 시도 | `403 Forbidden` | `WISH_ACCESS_DENIED` |
| 위시 항목 없음 | `404 Not Found` | `WISH_NOT_FOUND` |
| 같은 회원의 위시 상품 중복 | `409 Conflict` | `DUPLICATE_WISH` |
| 상품 없음 | `404 Not Found` | `PRODUCT_NOT_FOUND` |
| 외부 비속어 검사 서비스 이용 불가 | `503 Service Unavailable` | `PROFANITY_SERVICE_UNAVAILABLE` |

`INVALID_TOKEN` 응답에는 `WWW-Authenticate: Bearer` 헤더를 포함한다.
ID 경로 변수가 숫자가 아니면 `400 INVALID_REQUEST`를 반환한다.

오류 응답은 오류를 구분하는 `code`와 설명을 제공하는 `message`로 통일한다.

```json
{
  "code": "INVALID_REQUEST",
  "message": "상품 이름은 최대 15자입니다."
}
```

## API 명세

과제에서 제시한 기본 API 목록을 모두 구현했다. 위시 리스트 API에는 Bearer 토큰이 필요하다.

| 구분 | 메서드 | 경로 | 기능 |
| --- | --- | --- | --- |
| 회원 | POST | `/api/members/register` | 회원가입 및 토큰 발급 |
| 회원 | POST | `/api/members/login` | 로그인 및 토큰 발급 |
| 상품 | POST | `/api/products` | 상품 등록 |
| 상품 | GET | `/api/products/{productId}` | 상품 단건 조회 |
| 상품 | GET | `/api/products` | 상품 목록 조회 |
| 상품 | PUT | `/api/products/{productId}` | 상품 수정 |
| 상품 | DELETE | `/api/products/{productId}` | 상품 삭제 |
| 위시 리스트 | POST | `/api/wishes` | 상품 추가 |
| 위시 리스트 | GET | `/api/wishes` | 상품 목록 조회 |
| 위시 리스트 | DELETE | `/api/wishes/{wishId}` | 상품 삭제 |

상품 정보에는 이름, 가격, 이미지 URL이 포함된다.
이미지는 파일 업로드 없이 URL로 관리한다.
위시 리스트 API는 인증된 회원을 대상으로 한다.
`productId`는 상품 ID이며, `wishId`는 위시 리스트 항목 ID이다.

### 상품 API 요청·응답

상품 API는 현재 인증 없이 사용할 수 있다.

| 메서드 | 경로 | 성공 응답 |
| --- | --- | --- |
| POST | `/api/products` | 201, 상품 JSON 및 `Location: /api/products/{id}` |
| GET | `/api/products/{productId}` | 200, 상품 JSON |
| GET | `/api/products` | 200, 상품 JSON 배열. 상품이 없으면 `[]` |
| PUT | `/api/products/{productId}` | 200, 수정된 상품 JSON |
| DELETE | `/api/products/{productId}` | 204, 응답 본문 없음 |

등록·수정 요청 본문은 다음 필드를 모두 전달한다.

```json
{
  "name": "keyboard",
  "price": 1000,
  "imageUrl": "https://example.com/a.jpg"
}
```

등록·단건 조회·수정 성공 응답 예시:

```json
{
  "id": 4,
  "name": "keyboard",
  "price": 1000,
  "imageUrl": "https://example.com/a.jpg"
}
```

ID는 DB가 부여하므로 예시와 다를 수 있다. 목록 조회는 같은 구조의 객체들을 배열로 반환하며,
현재 조회 순서는 보장하지 않는다. 이미지 URL은 형식만 검사하고 이미지 존재 여부는 확인하지 않는다.

PurgoMalum은 `GET https://www.purgomalum.com/service/containsprofanity`에
상품명을 `text` 쿼리 매개변수로 전달한다. 비속어 감지 또는 검사 장애 시 저장·수정하지 않는다.
판정 범위는 외부 서비스의 비속어 목록에 따른다.

### 회원가입·로그인 API

| 메서드 | 경로 | 성공 응답 |
| --- | --- | --- |
| POST | `/api/members/register` | 201, 인증 토큰 JSON |
| POST | `/api/members/login` | 200, 새 인증 토큰 JSON |

두 API 모두 다음 요청 본문을 사용한다. 이메일은 앞뒤 공백 제거·소문자 변환 후 검증하며,
비밀번호는 입력값을 그대로 해시 생성·비교에 사용한다.

```json
{
  "email": "member@example.com",
  "password": "password123!"
}
```

성공 응답 예시:

```json
{
  "token": "발급된_무작위_토큰",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

`expiresIn`은 발급 시점부터의 유효 시간(초)이다. 로그인할 때마다 새 토큰을 발급한다.
응답에는 비밀번호·비밀번호 해시를 포함하지 않으며 `Cache-Control: no-store`를 설정한다.
발급된 토큰은 위시 리스트 API의 `Authorization` 헤더에 전달한다.

PowerShell 요청 예시(서버 실행 후 별도 터미널에서 실행):

```powershell
$body = @{ email = 'member@example.com'; password = 'password123!' } | ConvertTo-Json
$bytes = [System.Text.Encoding]::UTF8.GetBytes($body)
$signup = Invoke-RestMethod -Uri 'http://localhost:8080/api/members/register' `
    -Method Post -ContentType 'application/json; charset=utf-8' -Body $bytes
$signup.token
$login = Invoke-RestMethod -Uri 'http://localhost:8080/api/members/login' `
    -Method Post -ContentType 'application/json; charset=utf-8' -Body $bytes
$login.token
```

이미 가입한 이메일로 회원가입을 반복하면 409를 반환한다. 위 코드는 실제 DB에 회원·토큰을 저장한다.

### 위시 리스트 API

모든 요청에 `Authorization: Bearer {token}` 헤더가 필요하다.
토큰으로 확인한 회원만 대상으로 처리하며, 회원 ID는 클라이언트에서 받지 않는다.

| 메서드 | 경로 | 성공 응답 |
| --- | --- | --- |
| POST | `/api/wishes` | 201, 위시 JSON 및 `Location: /api/wishes/{wishId}` |
| GET | `/api/wishes` | 200, 자신의 위시 JSON 배열. 없으면 `[]` |
| DELETE | `/api/wishes/{wishId}` | 204, 응답 본문 없음 |

추가 요청은 양수인 상품 ID를 전달한다.

```json
{
  "productId": 4
}
```

추가 성공 응답 예시:

```json
{
  "id": 1,
  "product": {
    "id": 4,
    "name": "keyboard",
    "price": 1000,
    "imageUrl": "https://example.com/a.jpg"
  }
}
```

최상위 `id`는 위시 ID이며 `product.id`와 다르다. 삭제할 때는 최상위 `id`를 사용한다.
목록은 위시 ID 오름차순이며, 상품이 수정되면 목록에도 최신 상품 정보가 반영된다.
목록 응답에는 회원 정보나 비밀번호·토큰을 포함하지 않는다.

앞서 로그인한 PowerShell 세션에서 실행하는 예시:

```powershell
$headers = @{ Authorization = "Bearer $($login.token)" }
$body = @{ productId = 4 } | ConvertTo-Json # 실제 존재하는 상품 ID로 변경
$wish = Invoke-RestMethod -Uri 'http://localhost:8080/api/wishes' `
    -Method Post -Headers $headers -ContentType 'application/json; charset=utf-8' -Body $body
$wish | ConvertTo-Json -Depth 5
Invoke-RestMethod -Uri 'http://localhost:8080/api/wishes' -Headers $headers |
    ConvertTo-Json -Depth 5
Invoke-RestMethod -Uri "http://localhost:8080/api/wishes/$($wish.id)" `
    -Method Delete -Headers $headers
```

## 테스트 계획

- JUnit과 AssertJ 사용
- 각 기능의 정상 동작과 예외 상황 검증
- 상품명 15자·16자 경계값 검증
- 허용·금지 특수문자와 비속어 검증
- 외부 API 응답을 대체하여 비속어 검사 성공·실패·장애 상황 검증
- 회원가입과 로그인 성공·실패 검증
- 잘못된 토큰과 인증 정보 누락 검증
- 회원별 위시 리스트 접근 제한 검증

현재 자동 테스트는 상품 Service의 저장·조회·수정·삭제와 실패 시 데이터 유지,
Controller의 정상 응답·입력 검증·오류 JSON, PurgoMalumClient의 응답 변환·장애 처리를 검증한다.
등록·수정 요청에서 앞뒤 공백 제거, 내부 공백 유지, 제거 후 15자 허용·16자 거절도 검증한다.
Client 테스트는 가짜 HTTP 응답을 사용하므로 외부 서비스에 접속하지 않는다.
타임아웃 예외 처리는 검증하지만 실제 대기 시간이 3초인지를 측정하지는 않는다.
회원 관련 테스트는 이메일 저장·정규화·중복, 비밀번호 해시·공백 유지·경계값,
로그인 성공·실패, DB 중복 오류 변환, 토큰 해시 저장·유효 시간·고유성을 검증한다.
실제 Spring 구성을 사용하는 MockMvc 통합 테스트로 회원가입·로그인 요청부터 DB·토큰까지 연결하고,
토큰 발급 실패 시 가입 롤백도 검증한다. 회원 API는 실제 MySQL에서도 회원가입·로그인과
서로 다른 토큰 발급을 수동 확인했다.

토큰 테스트는 정상·누락·미등록·만료 직전·만료 시각·만료 이후 및 Bearer 헤더 형식을 검증한다.
위시 테스트는 추가·목록·삭제, 빈 목록, 회원별 분리, 중복, 존재하지 않는 상품·위시,
소유권 위반, 잘못된 입력, 인증 우선 처리와 상품 삭제 연동을 검증한다.
Spring 전체 구성의 MockMvc 테스트에서 회원가입 토큰으로 위시 API에 접근하는 흐름도 확인한다.
별도 트랜잭션의 동시 추가·상품 삭제 경합은 H2에서 검증했다.
실제 MySQL에서도 위시 추가 201, 회원별 목록 분리, 중복 추가 409,
타인 항목 삭제 403, 토큰 없는 조회 401을 수동 확인했다.
본인 위시 삭제는 204와 빈 본문을 반환하고 원래 상품은 유지된다.
상품 삭제 시 모든 회원의 해당 위시가 함께 제거되며, 삭제한 상품 조회는 404를 반환한다.

실제 앱에서는 정상 상품 등록, 비속어 등록·수정 차단, 수정 거절 후 기존 데이터 유지,
서비스 이용 불가 시 503 응답을 수동 확인했다.

## 프로그래밍 규칙

- Google Java Style Guide를 기반으로 작성하되, 들여쓰기는 공백 4칸 사용
- 제어문 중첩 깊이는 최대 2단계
- 메서드는 15줄 이하로 작성하고 한 가지 역할만 수행
- `else`, `switch`, 삼항 연산자 사용 금지

## 실행 및 테스트

Java 21 환경에서 프로젝트 루트의 터미널을 사용한다.
MySQL 서버를 실행하고 `shopping` DB와 해당 DB에 접근할 수 있는
`shopping_user` 계정을 미리 준비한다. 접속 주소는 `localhost:3306`이다.
실행 설정은 `src/main/resources/application.properties`에 있다.
개발 중 테이블 변경은 `ddl-auto=update`를 사용하고 Flyway는 비활성화한다.

실행:

```powershell
$env:DB_PASSWORD = 'shopping_user의_실제_비밀번호'
.\gradlew.bat bootRun
```

비밀번호 예시는 실제 계정 비밀번호로 교체한다. 비밀번호를 설정 파일이나 Git에 저장하지 않는다.
환경 변수는 현재 PowerShell 창에서만 유지되므로 새 창에서는 다시 설정한다.
서버는 `http://localhost:8080`에서 실행되며 `Ctrl+C`로 종료한다.

테스트(개발용 MySQL 및 외부 API 접속 없이 실행):

```powershell
.\gradlew.bat clean test
```

## AI 활용 기록

### 과제 준비 및 문서 정리

- 활용한 작업: 과제 요구사항 요약, 개발 환경 오류 확인, README 초안 작성, 세부 정책 및 오류 응답 형식 제안 정리
- 제안받은 내용을 검토하고 수정한 부분: 직접 검토한 뒤 작성 예정
- 학습한 내용: 직접 학습한 내용을 작성 예정

### 상품 API 구현 및 검증

- AI로부터 엔티티·저장소·Service·DTO·Controller 구성과 테스트 코드의 설명 및 예시를 받았다.
- 일부 요청에서는 AI가 파일 수정과 테스트 코드 추가를 직접 수행했다.
- 테스트 실행·결과 확인과 기능별 커밋·Push는 직접 진행했다.
- 중괄호 위치, 경로 변수 오타, Mockito 설정 순서, JSONPath 대소문자 오류를 확인하고 수정했다.
- MySQL 로그인 실패를 계정 비밀번호와 실행 환경 변수 설정으로 해결했다.
- 실제 앱 기동 시 누락된 RestClient 자동 구성 의존성을 추가했다.
- 상품명 정규화는 AI가 등록·수정 DTO의 생성자와 Controller 테스트를 추가했다.
- PowerShell 요청으로 정상 등록·비속어 차단·수정 거절 후 데이터 유지를 확인했다.
- 실제로 이해한 내용과 AI 제안을 수정한 이유는 최종 제출 전에 직접 보완할 예정이다.

### 회원가입·로그인 구현

- 초기 회원 엔티티·저장소·BCrypt 설정·회원가입 Service와 테스트는 AI 설명 및 코드 추가를 활용했다.
- 4일 차 남은 작업은 AI에 직접 구현·테스트·작업별 커밋·Push까지 요청했다.
- AI가 로그인 검증·DB 중복 오류 처리, 요청 DTO·비밀번호 바이트 검증, 토큰 발급·저장,
  회원 API와 자동 테스트 및 문서 갱신을 수행했다.
- 구현 코드와 테스트를 직접 검토하고 이해한 내용·수정한 이유는 최종 제출 전에 보완할 예정이다.

구현 중 AI를 사용하면 활용 방식, 제안받은 코드를 수정한 내용, 학습한 내용을 추가한다.

## 과제 진행 소감

상품 CRUD부터 회원 인증과 위시 리스트까지 기능을 확장하면서, 하나의 요청이 Controller,
Service, Repository를 거쳐 처리되는 흐름을 살펴볼 수 있었다. 요청과 응답은 DTO로 표현하고,
비즈니스 규칙은 Service에서 처리하도록 역할을 나누는 이유도 코드와 테스트를 통해 배웠다.
정상 요청을 처리하는 것뿐 아니라 잘못된 입력, 중복 데이터, 존재하지 않는 데이터에 어떤 응답을
반환할지 정하는 일도 API 구현의 일부라는 점을 알게 되었다.

구현 과정에서는 코드 외의 실행 환경을 확인하는 데도 시간이 들었다. MySQL 로그인 실패는
계정 비밀번호와 환경 변수 설정을 확인해 해결했고, 실제 앱 실행 중에는 RestClient 구성에 필요한
의존성이 누락된 문제도 확인했다. 이런 과정을 통해 오류가 발생했을 때 코드뿐 아니라 설정과
실행 환경도 함께 살펴봐야 한다는 점을 배웠다.

테스트를 작성하고 결과를 확인하는 과정에서도 작은 차이가 중요했다. 경로 변수 오타,
Mockito 설정 순서, JSONPath의 대소문자 때문에 실패하는 경우를 확인하며 요청 경로와 응답 구조를
정확히 읽는 연습을 했다. 자동 테스트에 더해 실제 MySQL과 PowerShell 요청으로 동작을 확인하면서,
테스트 환경에서 확인한 결과와 실제 실행 결과를 함께 점검하는 경험을 했다.

회원 인증과 위시 리스트에서는 기능 사이의 관계를 고려할 부분이 많았다. 토큰으로 회원을 구분하고,
다른 회원의 위시 삭제를 막으며, 상품 삭제 시 연결된 위시도 정리해야 했다. 이 과정에서 개별 API의
성공 여부뿐 아니라 접근 권한과 데이터의 일관성도 확인해야 한다는 점을 배웠다.

AI의 설명과 코드 구현 도움을 받으며 과제를 진행했다. 기능이 동작하는 것을 확인하는 데서 더 나아가,
각 코드가 필요한 이유와 테스트가 확인하는 내용을 직접 설명할 수 있도록 복습할 필요가 있다.
특히 토큰 인증 흐름과 트랜잭션, 동시 요청 처리 부분을 다시 살펴보며 이해를 보완하고 싶다.
