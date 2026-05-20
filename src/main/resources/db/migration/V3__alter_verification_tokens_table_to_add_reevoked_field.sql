alter table public.verification_tokens
    add revoked boolean default false not null;

