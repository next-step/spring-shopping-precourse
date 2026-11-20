CREATE TABLE wishes (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_wishes_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_wishes_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE CASCADE,
    CONSTRAINT uk_wishes_member_product UNIQUE (member_id, product_id)
)