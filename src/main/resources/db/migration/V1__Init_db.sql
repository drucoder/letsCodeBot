
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

create table telegram_message (
    id bigint not null primary key,
    text varchar(4096) not null default '',
    done boolean not null,
    chat_id bigint not null references telegram_chat,
    user_id_from bigint not null references telegram_user,
    answer_for_id bigint references telegram_message
);


