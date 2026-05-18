create table public.verification_tokens
(
    id          bigserial
        constraint verification_tokens_pk
            primary key,
    token       text        not null,
    expiry_date timestamptz not null,
    user_id     bigint      not null
        constraint verification_tokens_users_id_fk
            references public.users
            on delete cascade
);

