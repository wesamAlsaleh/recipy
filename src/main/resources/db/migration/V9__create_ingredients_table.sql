create table public.ingredients
(
    id        bigserial                             not null
        constraint ingredients_pk
            primary key,
    name      varchar(255)                          not null,
    quantity  varchar(255)                          not null,
    note      text,
    recipe_id bigint                                not null
        constraint ingredients_recipes_id_fk
            references public.recipes
            on delete cascade
);
