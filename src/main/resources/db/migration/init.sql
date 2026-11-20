create table if not exists product
(
    id        bigint auto_increment primary key,
    name      varchar(15)   not null,
    price     bigint        not null,
    image_url varchar(2048) not null
);
