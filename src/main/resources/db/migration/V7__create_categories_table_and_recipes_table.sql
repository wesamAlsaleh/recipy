create table public.categories
(
    id         bigserial                             not null
        constraint categories_pk
            primary key,
    name       varchar(255)                          not null,
    created_at timestamptz default current_timestamp not null
);

create table public.recipes
(
    id           bigserial                             not null
        constraint recipes_pk
            primary key,
    name         varchar(255)                          not null,
    description  text,
    instructions text                                  not null,
    cooking_time integer                               not null,
    difficulty   varchar(10)                           not null,
    image_url    varchar(2048),
    deleted      boolean     default false             not null,
    category_id  bigint                                not null
        constraint recipes_categories_id_fk
            references public.categories (id),
    created_at   timestamptz default current_timestamp not null,
    updated_at   timestamptz   default current_timestamp not null
);

