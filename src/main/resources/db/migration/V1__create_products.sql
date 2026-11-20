CREATE TABLE products (
    id        BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(15)   NOT NULL,
    price     INT           NOT NULL,
    image_url VARCHAR(1000) NOT NULL
);

