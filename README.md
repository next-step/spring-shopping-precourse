# spring-shopping-precourse

쇼핑 - 프리코스

## 구현 기능 목록

### 1. 상품
- [ ] 상품 생성 (POST /api/products)
- [ ] 상품 조회 (GET /api/products/{productId})
- [ ] 상품 목록 조회 (GET /api/products)
- [ ] 상품 수정 (PUT /api/products/{productId})
- [ ] 상품 삭제 (DELETE /api/products/{productId})
- 상품은 이름, 가격, 이미지 URL이 포함된다. (이미지는 URL 문자열로 관리)

### 2. 유효성 검사 및 예외 처리
- [ ] 상품 이름은 공백 포함 최대 15자 (+ 빈 이름 불가)
- [ ] 허용 특수문자: ( ) [ ] + - & / _ (그 외 특수문자 불가)
- [ ] 비속어 포함 불가 (PurgoMalum API로 검사)
- [ ] 잘못된 값이면 적절한 오류 응답 반환

### 3. 회원 로그인
- [ ] 회원 가입 (POST /api/members/register): 이메일/비밀번호, 토큰 발급
- [ ] 로그인 (POST /api/members/login): 검증 후 토큰 발급

### 4. 위시리스트 (인증된 회원만)
- [ ] 위시리스트 상품 조회 (GET /api/wishes)
- [ ] 위시리스트 상품 추가 (POST /api/wishes)
- [ ] 위시리스트 상품 삭제 (DELETE /api/wishes/{wishId})

## 구현 전략
- 계층 분리: Controller → Service → Repository, 검증 로직은 도메인 객체에 둔다
- 패키지는 기능별로 나눈다: product, member, wish, auth, common
- 저장소는 메모리(Map)로 별도 클래스로 분리해 구현한다
- 상품 이름 검증은 별도 클래스(ProductName)로 분리하여 단위 테스트한다
- 비속어 검사는 외부 API 호출 부분을 분리하여 테스트 가능하게 만든다 (PurgoMalum 영어 비속어만 걸러낸다)
- 토큰은 JWT로 발급한다. 이메일을 subject에 담고, 유효 시간은 1시간이다 (비밀키 application.properties)
- 위시리스트 API는 Authorization: Bearer 헤더의 토큰으로 회원을 확인한다
- 코드 규칙: 들여쓰기 4칸/깊이 2 이하, 메서드 15줄 이하, else·switch·삼항 연산자 금지
- 기능 단위로 커밋하며, 커밋 메시지는 AngularJS Commit Convention을 따른다
- 테스트: JUnit + AssertJ로 테스트한다
- 상품 가격(0원 이상), 이미지 URL(http/https로 시작), 빈 이름 금지 규칙을 추가한다
- 오류 응답: 잘못된 값은 400, 없는 상품은 404, 비속어 검사 서비스 장애는 503, 중복 가입은 409, 로그인 실패는 401

## AI 도구 활용 기록
### 활용 방식
- Claude에게 단계별 로드맵, 기능별 코드 초안, 커밋 메시지, 오류 원인 분석을 도움받았다.
- 코드는 단계별로 받아 직접 입력하고, 컴파일과 테스트로 동작을 확인한 뒤 커밋했다. 
- 요구사항에 적힌 JUnit, AssertJ 문서(Baeldung)와 PurgoMalum 사이트를 AI의 코드와 대조해 확인했다.

### 코드를 어떻게 수정했는가
- 예외 클래스 두 개가 한 파일에 들어가 컴파일 오류(public 클래스 이름과 파일 이름 불일치)가 났고, 파일을 분리해 해결했다.
- 프로젝트 템플릿에 JPA/Flyway가 있었지만, Flyway 때문에 테이블이 자동 생성되지 않아 시간 내 구현을 위해 메모리 저장소로 바꿨다.

### 학습한 것
- Controller, Service, Repository로 계층을 나누는 이유와, 메서드를 작게 나누면 테스트가 쉬워진다는 점
- 외부 API 호출을 인터페이스로 분리해 가짜 구현으로 테스트하는 방법
- 예외를 HTTP 상태 코드로 변환하는 @RestControllerAdvice
- 기능 단위 커밋과 커밋 메시지 컨벤션, fork/branch/PR 제출 흐름