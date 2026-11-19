# spring-shopping-precourse

# 구현 전략
- [ ] Java를 사용한다
- [ ] 외부 API 실패 시, 500 에러를 반환한


# 기능 목록
## 상품
- [x] 상품을 조회한다
  - [x] GET /api/products/{productId}
  - [x] GET /api/products
  - [x] 상품에는 이름, 가격, 이미지 URL이 포함된다
- [x] 상품을 등록한다
  - [x] POST /api/products
  - [x] 유효성 검사를 한다
    - [x] 이름 유효성 검사 
      - [x] 상품 이름은 공백을 포함하여 15자까지 입력 가능
      - [x] 상품 이름에는 아래 특수 문자만 사용할 수 있다
        - [x] 가능 목록: (),[],+,-,&,/,_
      - [x] PurgoMalum API를 호출하여 상품 이름에 비속어가 들어갔는지 확인한다
    - [x] 가격 유효성 검사
      - [x] 가격은 0을 초과해야한다
      - [x] 가격은 숫자만 입력할 수 있다
- [x] 상품을 수정한다
  - [x] PUT /api/products/{productId}
- [x] 상품을 삭제한다
  - [x] DELETE /api/products/{productId}

## 회원 가입 & 로그인
- [ ] 회원가입
  - [ ] POST /api/members/register
  - [ ] 이메일과 비밀번호로 가입할 수 있다
  - [ ] 유효성 검사
    - [ ] 이메일 유효성 검사
    - [ ] 비밀번호 유효성 검사
      - [ ] 영어와 숫자를 포함하여 8글자 이상
  - [ ] 이미 가입된 이메일로 다시 가입 불가
- [ ] 로그인
  - [ ] POST /api/members/login
  - [ ] 이메일과 비밀번호를 검사한다
  - [ ] 일치하면 인증 토큰을 발급한다

## 위시 리스트
- [ ] 위시 리스트의 소유자만이 위시 리스트를 조회 및 등록, 수정이 가능하다
- [ ] 위시 리스트를 조회한다
  - [ ] GET /api/wishes 
- [ ] 위시 리스트에 상품을 추가한다
  - [ ] POST /api/wishes 
- [ ] 위시 리스트에서 상품을 삭제할 수 있다
  - [ ] DELETE /api/wishes/{wishId} 