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
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer>
{
    @Query("FROM NotificationSetting setting WHERE setting.type = :type AND setting.userId = :userId")
    Optional<NotificationSetting> findByTypeAndPersonId(@Param("type") NotificationType type, @Param("userId") Integer userId);

    List<NotificationSetting> findByUserId(Integer userId);

    @Query("SELECT DISTINCT isEnable FROM NotificationSetting WHERE user_id = :user_id AND type = :setting")
    Byte getNotificationSetting(@Param("user_id") Integer userId, @Param("setting") NotificationType setting);
}
