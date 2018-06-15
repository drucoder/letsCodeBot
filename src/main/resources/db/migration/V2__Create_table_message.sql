create table telegram_message (
    id bigint not null primary key,
    text varchar(4096) not null default '',
    done boolean not null,
    chat_id bigint not null references telegram_chat,
    user_id_from bigint not null references telegram_user,
    answer_for_id bigint references telegram_message
);