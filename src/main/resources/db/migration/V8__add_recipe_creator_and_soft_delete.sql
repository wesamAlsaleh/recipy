alter table public.recipes
    add created_by bigint not null
        constraint recipes_user_id_fk
            references public.users
            on delete cascade;

create index "idx_recipes_deleted"
    on public.recipes (deleted);

alter table public.recipes
drop constraint recipes_categories_id_fk;

alter table public.recipes
    add constraint recipes_categories_id_fk
        foreign key (category_id) references public.categories
            on delete cascade;

alter table public.categories
    add updated_at timestamptz default current_timestamp not null;

alter table public.categories
    add deleted boolean default false not null;

