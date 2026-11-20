# spring-shopping-precourse

## 기능 요구사항
### 1. 상품
- 상품의 경우 저장하기 위해 내장된 h2 db를 사용한다.
- 상품의 DB는 상품id, 이름. 가격, 개수, 이미지 URL로 구성한다.
- 도메인은 회원, 상품, 위시리스트로 나눈다.
#### 상품 목록 조회
> GET `/api/products`
- [ ] Controller 추가
- [ ] product에 getProductsList 메소드 추가
#### 상품 조회
> GET `/api/products/{productId}`
- [ ] Controller 추가
- [ ] product에 getProducts 메소드 추가
#### 상품 추가
> POST `/api/products`
- [ ] Controller 추가
- [ ] product에 addProducts 메소드 추가
#### 상품 수정
> PUT `/api/products/{productId}`
- [ ] Controller 추가
- [ ] product에 editProducts 메소드 추가
#### 상품 삭제
> DELETE `/api/products/{productId}`
- [ ] Controller 추가
- [ ] product에 deleteProducts 메소드 추가

### 2. 유효성 검사 및 예외 처리
- [ ] 상품 이름 예외 처리에 PurgoMalum API 활용
- [ ] 공백 처리
- [ ] 최대 15자 입력 
- [ ] 가능한 특수 문자 : ( ), [ ], +, -, &, /, _

### 3. 회원 로그인
#### 회원가입
> POST `/api/members/register`
- [ ] Controller 추가
- [ ] user에 signup 메소드 추가
#### 로그인
> POST `/api/members/login`
- [ ] Controller 추가
- [ ] user에 login 메소드 추가

### 4. 위시 리스트
#### 위시리스트 조회
> GET `/api/wishes`
- [ ] Controller 추가
- [ ] wish에 getWhishList 메소드 추가
#### 위시리스트 추가
> POST `/api/wishes`
- [ ] Controller 추가
- [ ] wish에 addWishlist 메소드 추가
#### 위시리스트 삭제
> DELETE `/api/wishes/{wishId}`
- [ ] Controller 추가
- [ ] wish에 deleteWishlist 메소드 추가
