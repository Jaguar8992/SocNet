package main.service;

import main.mailSender.MailSender;
import main.model.entity.Notification;
import main.model.entity.NotificationSetting;
import main.model.entity.User;
import main.model.entity.enums.NotificationType;
import main.model.repository.NotificationRepository;
import main.model.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationApi {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    @Value("${scheduler.notificationMinDelay:15}")
    private int notificationMinDelay;

    public NotificationApi(NotificationRepository notificationRepository,
                           NotificationSettingRepository notificationSettingRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
    }

    /**
     * {@link NotificationType notificationType} Какое уведомление я должен создать
     * <br>{@link User user} Какому пользователю прийдет уведомление.
     * <br>{@link Notification entityId} - id сущности (комментарий, friend, запись или сообщение)
     */
    public void createNotification(NotificationType notificationType, User user, Integer entityId) {
        try {
            if (user == null) {
                throw new Exception("User not found!");
            }

            List<Notification> findRow = notificationRepository.findByUserAndEntityId(user.getId(), entityId, notificationType);

            Optional<Byte> optionalSetting = notificationSettingRepository.getNotificationSetting(user, notificationType);

            boolean enableNotification = true;
            if (optionalSetting.isEmpty()) {

                enableNotification = false;
                addNotificationSettings(notificationType, user, (byte) 0);

            } else if (optionalSetting.get() != 1) {

                enableNotification = false;

            }

            if (findRow.isEmpty() && enableNotification) {

                Notification notification = new Notification();
                notification.setType(notificationType);
                notification.setSentTime(LocalDateTime.now());
                notification.setUser(user);
                notification.setEntityId(entityId);
                notification.setEmail(user.getEmail());
                notification.setPhone(user.getPhone());
                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToMail(Notification notification) {
        NotificationType notificationType = notification.getType();
        User user = notification.getUser();
        String mail = user.getEmail();

        if (mail == null || mail.equals("")) {
            return;
        }

        Optional<Byte> optionalSetting = notificationSettingRepository.getNotificationSetting(user, notificationType);

        if (optionalSetting.isEmpty()) {

            addNotificationSettings(notificationType, user, (byte) 0);

        } else if (optionalSetting.get() == 1) {
            {
                switch (notificationType) {
                    case FRIEND_REQUEST:
                        MailSender.sendMessage(mail, "New friend request",
                                "You have a new friend request");
                        break;
                    case MESSAGE:
                        MailSender.sendMessage(mail, "New message",
                                "You have new message");
                        break;
                    case POST:
                        MailSender.sendMessage(mail, "Post published",
                                "Post published");
                        break;
                    case POST_COMMENT:
                        MailSender.sendMessage(mail, "New comment",
                                "Your post was commented");
                        break;
                    case COMMENT_COMMENT:
                        MailSender.sendMessage(mail, "New comment",
                                "Your comment was answered by");
                        break;
                    case FRIEND_BIRTHDAY:
                        MailSender.sendMessage(mail, "Friend's birthday",
                                "Your friend is celebrating his birthday");
                        break;
                }
            }
        }
    }

    private void addNotificationSettings(NotificationType notificationType, User user, byte value) {
        NotificationSetting setting = new NotificationSetting();
        setting.setIsEnable(value);
        setting.setUser(user);
        setting.setType(notificationType);
        notificationSettingRepository.save(setting);
    }

    public void sendNotificationEmail() {
        List<Notification> notifications = notificationRepository.getNotificationsWithDelay(
                LocalDateTime.now().minusMinutes(notificationMinDelay));

        notifications.stream()
                .filter(notification -> notification.getUser().getEmail() != null)
                .forEach(notification -> {
                    sendToMail(notification);
                    notification.setSentEmailTime(LocalDateTime.now());
                    notificationRepository.save(notification);
                });
    }
}
