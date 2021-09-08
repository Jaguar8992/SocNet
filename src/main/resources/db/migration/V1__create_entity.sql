CREATE TABLE language
(
    id   integer      not null AUTO_INCREMENT,
    name varchar(255) not null,
    primary key (id)
);

CREATE TABLE country
(
    id   integer      not null AUTO_INCREMENT,
    name varchar(255) not null,
    primary key (id)
);

CREATE TABLE town
(
    id         integer      not null AUTO_INCREMENT,
    name       varchar(255) not null,
    country_id integer      not null,
    primary key (id)
);

CREATE TABLE user
(
    id                  integer                            not null AUTO_INCREMENT,
    type                enum ('USER', 'MODERATOR','ADMIN') not null,
    first_name          varchar(255)                       not null,
    last_name           varchar(255)                       not null,
    reg_date            datetime(6)                        not null,
    birth_date          date,
    email               varchar(255)                       not null,
    phone               varchar(30),
    password            varchar(255)                       not null,
    confirmation_code   varchar(255),
    is_approved         tinyint                            not null,
    photo               TEXT,
    about               TEXT,
    town_id             integer,
    messages_permission enum ('ALL','FRIENDS')             not null,
    last_online_time    datetime(6)                        not null,
    is_blocked          tinyint                            not null,
    is_online           tinyint                            not null,
    primary key (id)
);

CREATE TABLE file_info
(
    id                 integer      not null AUTO_INCREMENT,
    hash_file          varchar(255) not null,
    owner_id           integer      not null,
    file_name          varchar(255),
    relative_file_path varchar(255),
    raw_file_url       varchar(255),
    file_format        varchar(255),
    bytes              long         not null,
    file_type          varchar(255),
    created_at         datetime(6),
    primary key (id)
);

CREATE TABLE friendship
(
    id          integer                                                         not null AUTO_INCREMENT,
    src_user_id integer                                                         not null,
    dst_user_id integer                                                         not null,
    status      enum ('REQUEST', 'FRIEND', 'SUBSCRIBED', 'DECLINED', 'BLOCKED') not null,
    time        datetime(6)                                                     not null,
    primary key (id)
);

CREATE TABLE message
(
    id           integer              not null AUTO_INCREMENT,
    time         datetime(6)          not null,
    author_id    integer              not null,
    message_text TEXT,
    read_status  enum ('SENT','READ') not null,
    dialog_id    integer              not null,
    primary key (id)
);

CREATE TABLE dialog
(
    id           integer not null auto_increment,
    owner_id     integer not null,
    recipient_id integer not null,
    is_deleted   boolean not null,
    invite_url   text    not null,
    last_message integer,
    primary key (id)
);

CREATE TABLE post
(
    id         integer           not null AUTO_INCREMENT,
    time       datetime(6)       not null,
    author_id  integer           not null,
    title      varchar(255)      not null,
    post_text  TEXT              not null,
    is_blocked tinyint           not null,
    is_deleted tinyint DEFAULT 0 not null,
    primary key (id)
);

CREATE TABLE tag
(
    id   integer      not null AUTO_INCREMENT,
    name varchar(255) not null,
    primary key (id)
);

CREATE TABLE post2tag
(
    id      integer not null AUTO_INCREMENT,
    post_id integer not null,
    tag_id  integer not null,
    primary key (id)
);

CREATE TABLE post_like
(
    id         integer     not null AUTO_INCREMENT,
    time       datetime(6) not null,
    post_id    integer,
    comment_id integer,
    user_id    integer     not null,
    primary key (id)
);

CREATE TABLE post_file
(
    id      integer      not null AUTO_INCREMENT,
    post_id integer      not null,
    name    varchar(255) not null,
    path    TEXT         not null,
    primary key (id)
);

CREATE TABLE post_comment
(
    id           integer           not null AUTO_INCREMENT,
    time         datetime(6)       not null,
    post_id      integer           not null,
    parent_id    integer,
    author_id    integer           not null,
    comment_text TEXT              not null,
    is_blocked   tinyint           not null,
    is_deleted   tinyint DEFAULT 0 not null,
    primary key (id)
);

CREATE TABLE report
(
    id         integer     not null AUTO_INCREMENT,
    time       datetime(6) not null,
    post_id    integer     not null,
    comment_id integer,
    author_id  integer     not null,
    primary key (id)
);

CREATE TABLE notification
(
    id              integer                                                                                          not null AUTO_INCREMENT,
    type            enum ('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE', 'FRIEND_BIRTHDAY') not null,
    sent_time       datetime(6)                                                                                      not null,
    user_id         integer                                                                                          not null,
    entity_id       integer                                                                                          not null,
    email           varchar(255),
    sent_email_time datetime(6),
    phone           varchar(30),
    primary key (id)
);

CREATE TABLE block_history
(
    id         integer                  not null AUTO_INCREMENT,
    time       datetime(6)              not null,
    user_id    integer                  not null,
    post_id    integer,
    comment_id integer,
    action     enum ('BLOCK','UNBLOCK') not null,
    primary key (id)
);

CREATE TABLE token2user
(
    id      integer      not null AUTO_INCREMENT,
    token   varchar(255) not null,
    time    datetime(6)  not null,
    user_id integer      not null,
    primary key (id)
);

CREATE TABLE notification_setting
(
    id        integer                                                                                          not null AUTO_INCREMENT,
    type      enum ('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE', 'FRIEND_BIRTHDAY') not null,
    user_id   integer                                                                                          not null,
    is_enable tinyint                                                                                          not null,
    primary key (id)
);

CREATE TABLE logs
(
    USER_ID VARCHAR(20)   NOT NULL,
    DATED   DATETIME      NOT NULL,
    LOGGER  VARCHAR(50)   NOT NULL,
    LEVEL   VARCHAR(10)   NOT NULL,
    MESSAGE VARCHAR(1000) NOT NULL
);

CREATE TABLE user_print_status
(
    id        integer                   not null AUTO_INCREMENT,
    status    enum ('ACTIVE', 'PRINTS') not null,
    user_id   integer                   not null,
    dialog_id integer                   not null,
    time      datetime(6)               not null,
    primary key (id)
);

create table hibernate_sequence
(
    next_val bigint
);

insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
