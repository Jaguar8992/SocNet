package main.model.repository;

import main.model.entity.NotificationSetting;
import main.model.entity.User;
import main.model.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer> {
    @Query("FROM NotificationSetting setting WHERE setting.type = :type AND setting.user = :user")
    Optional<NotificationSetting> findByTypeAndPersonId(@Param("type") NotificationType type, @Param("user") User user);

    List<NotificationSetting> findByUser(User user);

    @Query("SELECT DISTINCT isEnable FROM NotificationSetting WHERE user = :user AND type = :setting")
    Optional<Byte> getNotificationSetting(@Param("user") User user, @Param("setting") NotificationType setting);
}
