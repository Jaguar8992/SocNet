package main.model.repository;

import main.model.entity.Notification;
import main.model.entity.enums.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {
    Optional<Notification> findByType(NotificationType type);

    @Query(value = "SELECT n FROM Notification n WHERE n.user.id = :id AND n.entityId = :entityId AND n.type = :typeNotification ")
    List<Notification> findByUserAndEntityId(@Param("id") Integer id,
                                             @Param("entityId") Integer entityId,
                                             @Param("typeNotification") NotificationType typeNotification);

    @Query(value = "SELECT n FROM Notification n WHERE n.user.id = :id AND n.type IN (:types)")
    List<Notification> findAllByIdUser(@Param("id") Integer id, @Param("types") List<NotificationType> types, Pageable pageable);

    @Query(value = "SELECT n FROM Notification n WHERE n.id = :id")
    List<Notification> findById(@Param("id") Integer id, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Notification n WHERE n.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Integer id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Notification n WHERE n.id = :id")
    void deleteByUserId(@Param("id") Integer id);

    @Query(value = "" +
            "SELECT n " +
            "FROM Notification n " +
            "WHERE n.sentEmailTime IS NULL " +
            "AND n.sentTime < :timeDelay ")
    List<Notification> getNotificationsWithDelay(@Param("timeDelay") LocalDateTime timeDelay);
}
