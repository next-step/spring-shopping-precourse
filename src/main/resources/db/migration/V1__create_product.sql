CREATE TABLE product (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(15)  NOT NULL,
    price   INT          NOT NULL,
    count   INT          NOT NULL,
    url     VARCHAR(255) NOT NULL
);
