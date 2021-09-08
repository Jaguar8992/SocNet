package main.model.repository;

import main.model.entity.Dialog;
import main.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Integer> {

    @Query(value = "SELECT DISTINCT d FROM Dialog d " +
            "LEFT JOIN Message m ON d.id = m.dialog.id " +
            "WHERE (d.owner.id = :ownerId OR d.recipient.id = :ownerId) " +
            "AND (LOWER(m.messageText) LIKE LOWER(CONCAT('%', :query, '%')) OR m.messageText IS NULL)")
    Page<Dialog> getAllDialog(@Param("ownerId") int ownerId,
                              @Param("query") String query,
                              Pageable pageable);

    @Query(value = "SELECT d FROM Dialog AS d " +
            "WHERE (d.owner = :user and d.recipient = :recipient) OR " +
            "(d.owner = :recipient and d.recipient = :user)")
    List<Dialog> getDialogFor2Users(@Param("user") User user,
                                    @Param("recipient") User recipient);

    Optional<Dialog> findDialogById(Integer dialogId);

    @Query(value = "SELECT COUNT(DISTINCT m.id) " +
            "FROM Dialog AS d INNER JOIN Message m ON d = m.dialog " +
            "WHERE d = :dialog and m.author <> :user and m.readStatus = 'SENT'")
    Long calculateUnreadMessage(@Param("user") User user,
                                @Param("dialog") Dialog dialog);

}
