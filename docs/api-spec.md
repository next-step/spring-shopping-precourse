# API 명세

API 명세는 아래와 같다. 클라이언트에 따라 이러한 API가 모두 필요할 수도 있고 필요하지 않을 수도 있다. 또는 누락된 API가 있을 수도 있다.

## 회원 API

| URL | 메서드 | 기능 | 설명 |
|---|---|---|---|
| `/api/members/register` | POST | 회원 가입 | 새 회원을 등록하고 토큰을 받는다. |
| `/api/members/login` | POST | 로그인 | 회원을 인증하고 토큰을 받는다. |

## 상품 API

| URL | 메서드 | 기능 | 설명 |
|---|---|---|---|
| `/api/products` | POST | 상품 생성 | 새 상품을 등록한다. |
| `/api/products/{productId}` | GET | 상품 조회 | 특정 상품의 정보를 조회한다. |
| `/api/products/{productId}` | PUT | 상품 수정 | 기존 상품의 정보를 수정한다. |
| `/api/products/{productId}` | DELETE | 상품 삭제 | 특정 상품을 삭제한다. |
| `/api/products` | GET | 상품 목록 조회 | 모든 상품의 목록을 조회한다. |

## 위시 리스트 API

| URL | 메서드 | 기능 | 설명 |
|---|---|---|---|
| `/api/wishes` | POST | 위시 리스트 상품 추가 | 회원의 위시 리스트에 상품을 추가한다. |
| `/api/wishes/{wishId}` | DELETE | 위시 리스트 상품 삭제 | 회원의 위시 리스트에서 상품을 삭제한다. |
| `/api/wishes` | GET | 위시 리스트 상품 조회 | 회원의 위시 리스트에 있는 상품을 조회한다. |
