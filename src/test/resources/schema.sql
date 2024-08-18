DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS user_tokens;

CREATE TABLE IF NOT EXISTS users
(
    user_id    char(36)  not null,
    email      varchar(1000) unique,
    password   varchar(60),
    created_at timestamp not null default now(),
    updated_at timestamp,
    deleted_at timestamp,
    version    long      not null default 0,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS user_tokens
(
    user_id    char(36)  not null,
    token      varchar(100),
    created_at timestamp not null default now(),
    updated_at timestamp,
    deleted_at timestamp,
    version    long      not null default 0,
    primary key (user_id)
);

CREATE TABLE IF NOT EXISTS products
(
    product_id         char(36)  not null,
    name               varchar(15),
    price              bigint,
    image_download_url text,
    created_at         timestamp not null default now(),
    updated_at         timestamp,
    deleted_at         timestamp,
    version            long      not null default 0,
    primary key (product_id)
);

CREATE TABLE IF NOT EXISTS wishlists
(
    wishlist_id char(36)  not null,
    user_id     char(36)  not null,
    product_id  char(36)  not null,
    created_at  timestamp not null default now(),
    updated_at  timestamp,
    deleted_at  timestamp,
    version     long      not null default 0,
    primary key (wishlist_id)
);