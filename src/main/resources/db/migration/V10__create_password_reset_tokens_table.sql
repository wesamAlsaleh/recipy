create table public.password_reset_tokens
(
    id            bigserial                             not null
        constraint password_reset_tokens_pk
            primary key,
    token         varchar(255)                          not null,
    user_id       bigint                                not null
        constraint password_reset_tokens_users_id_fk
            references public.users
            on delete cascade,
    used          boolean     default false             not null,
    expired_at    timestamptz                           not null,
    created_at    timestamptz default current_timestamp not null
);

