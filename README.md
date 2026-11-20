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
- [ ] 상품 이름은 공백 포함 최대 15자
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
- 상품 이름 검증은 별도 클래스로 분리하여 단위 테스트한다
- 비속어 검사는 외부 API 호출 부분을 분리하여 테스트 가능하게 만든다
- 토큰은 JWT로 발급한다. 이메일을 subject에 담고, 요청 시 Authorization: Bearer 헤더로 전달받아 검증한다
- 코드 규칙: 들여쓰기 4칸/깊이 2 이하, 메서드 15줄 이하, else·switch·삼항 연산자 금지
- 기능 단위로 커밋하며, 커밋 메시지는 AngularJS Commit Convention을 따른다
- 테스트: JUnit + AssertJ로 테스트한다

## AI 도구 활용 기록
- (활용 방식 / 코드를 어떻게 수정했는지 / 무엇을 학습했는지)