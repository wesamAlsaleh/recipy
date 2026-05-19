create table public.user_sessions
(
    id          bigserial                             not null
        constraint user_sessions_pk
            primary key,
    token       text                                  not null,
    user_id     bigint                                not null
        constraint user_sessions_users_id_fk
            references public.users
            on delete cascade,
    expiry_date timestamptz                           not null,
    revoked     boolean     default false             not null,
    created_at  timestamptz default current_timestamp not null
);

