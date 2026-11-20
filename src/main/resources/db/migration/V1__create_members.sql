CREATE TABLE members (
    id BIGINT NOT NULL AUTO_INCREMENT primary key,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL ,
    name VARCHAR(39) NOT NULL ,
    CONSTRAINT uk_members_email UNIQUE (email)
)