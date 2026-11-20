CREATE TABLE wish (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      member_id BIGINT NOT NULL,
                      product_id BIGINT NOT NULL,
                      FOREIGN KEY (member_id) REFERENCES member (id),
                      FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE,
                      UNIQUE (member_id, product_id)
);