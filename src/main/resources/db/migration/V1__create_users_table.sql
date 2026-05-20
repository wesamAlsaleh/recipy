create table public.users
(
    id                bigserial                             not null
        constraint users_pk
            primary key,
    username          varchar(255)                          not null,
    email             varchar(255)                          not null
        constraint users_pk_2
            unique,
    password          varchar(255)                          not null,
    role              varchar(20)                           not null,
    status            boolean     default true              not null,
    profile_image_url varchar(2048),
    email_verified    boolean     default false             not null,
    deleted           boolean     default false             not null,
    created_at        timestamptz default current_timestamp not null,
    updated_at        timestamptz default current_timestamp not null
);

