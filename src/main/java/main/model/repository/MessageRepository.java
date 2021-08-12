package main.model.repository;

import main.model.entity.Dialog;
import main.model.entity.Message;
import main.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    Page<Message> findAllByDialogAndMessageTextContaining(Dialog dialog, String query, Pageable pageable);

    @Query ("SELECT count(*) FROM Message WHERE recipient = :user AND  readStatus = 'SENT'")
    Long getCountOfUnreadMessage (@Param("user") User user);
}
