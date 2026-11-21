-- 회원 테이블: 이메일과 비밀번호를 저장한다.
CREATE TABLE member
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);
