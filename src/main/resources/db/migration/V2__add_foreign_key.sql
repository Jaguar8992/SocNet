alter table town add constraint FK5lidm6cqbc7u4xhqpxm898qme foreign key (country_id)
    references country (id);

alter table user add constraint FK5lidm6cqbc7u4xhqpxm988qme foreign key (town_id)
    references town (id);

alter table friendship add constraint FK5lidm6cqbc7u4xhqpxm155qme foreign key (src_user_id)
    references user (id);

alter table friendship add constraint FK5lidm6cqbc7u4xhqpxm210qme foreign key (dst_user_id)
    references user (id);

alter table message add constraint FK5lidm6cqbc785xhqpxm210qme foreign key (author_id)
    references user (id);

alter table message add constraint FK5lidm6cqbc785xhqpxm310qme foreign key (recipient_id)
    references user (id);

alter table post add constraint FK5lidm6cqbc785xhqpxm157qme foreign key (author_id)
    references user (id);

alter table post2tag add constraint FK5lidm6cqbc785xhqpxm895qme foreign key (post_id)
    references post (id);

alter table post2tag add constraint FK5lidm6cqbc785xhqpxm778qme foreign key (tag_id)
    references tag (id);

alter table post_like add constraint FK5lidm9cqbc785xhqpxm778qme foreign key (post_id)
    references post (id);

alter table post_like add constraint FK5lidm9cqbc785xhqpxm278qme foreign key (user_id)
    references user (id);

alter table post_file add constraint FK5lidm9cqbc785xhqpxm520qme foreign key (post_id)
    references post (id);

alter table post_comment add constraint FK5lidm9cqbc7u4xhqpxm520qme foreign key (post_id)
    references post (id);

alter table post_comment add constraint FK5lidm9cqbc1u4xhqpxm520qme foreign key (parent_id)
    references post_comment (id);

alter table post_comment add constraint FK5lidm9cqbc1u5xhqpxm520qme foreign key (author_id)
    references user (id);

alter table report add constraint FK5lidm9cqbc7u4xhqpxm870qme foreign key (post_id)
    references post (id);

alter table report add constraint FK5lidm9cqbc3t5xhqpxm870qme foreign key (comment_id)
    references post_comment (id);

alter table report add constraint FK5lidm9cqbc3u5xhqpxm870qme foreign key (author_id)
    references user (id);

alter table notification add constraint FKjou6suf2w810t2u3l96uasw3r foreign key (user_id)
    references user (id);

alter table block_history add constraint FKpjoedhh4h917xf25el3odq20i foreign key (user_id)
    references user (id);

alter table block_history add constraint FK9q09ho9p8fmo6rcysnci8rocc foreign key (post_id)
    references post (id);

alter table block_history add constraint FKaawaqxjs3br8dw5v90w7uu514 foreign key (comment_id)
    references post_comment (id);

alter table message add constraint FKaawaqxjs3br8da5v90w7uu114 foreign key (dialog_id)
    references dialog (id);

alter table user2dialog add constraint FKaawaqxes3br8dw5v90w7uu314 foreign key (dialog_id)
    references dialog (id);

alter table user2dialog add constraint FKaawaqejs3br8dw5v90w7uu314 foreign key (user_id)
    references user (id);

alter table dialog add constraint FKaawaqxjs3bf8dw5v30w7uuf14 foreign key (owner_id)
    references user (id);