
create table telegram_user (
    id bigint not null primary key,
    first_name varchar default '',
    last_name varchar default '',
    user_name varchar default '',
    is_bot boolean default false
);

create table telegram_chat (
    id bigint not null primary key,
    type varchar not null,
    title varchar default '',
    first_name varchar default '',
    last_name varchar default '',
    user_name varchar default ''
);



