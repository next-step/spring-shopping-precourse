CREATE TABLE wish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_wish_member FOREIGN KEY (member_id)
        REFERENCES member (id) ON DELETE CASCADE,
    CONSTRAINT fk_wish_product FOREIGN KEY (product_id)
        REFERENCES product (id) ON DELETE CASCADE,
    CONSTRAINT uk_wish_member_product UNIQUE (member_id, product_id)
)