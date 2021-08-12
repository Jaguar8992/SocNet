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
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {
    Optional<Notification> findByType(NotificationType type);

    @Query(value = "SELECT n FROM Notification n WHERE n.user.id = :id")
    List<Notification> findAllByIdUser(@Param("id") Integer id, Pageable pageable);

    @Query(value = "SELECT n FROM Notification n WHERE n.id = :id")
    List<Notification> findById(@Param("id") Integer id, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Notification n WHERE n.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Integer id);
}
