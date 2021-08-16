package main.service;

import main.mailSender.MailSender;
import main.model.entity.Notification;
import main.model.entity.NotificationSetting;
import main.model.entity.User;
import main.model.entity.enums.NotificationType;
import main.model.repository.NotificationRepository;
import main.model.repository.NotificationSettingRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationApi
{

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Autowired
    public NotificationApi(NotificationRepository notificationRepository, UserService userService, UserRepository userRepository, NotificationSettingRepository notificationSettingRepository)
    {
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
    }

    /**
     * What kind of notification should I create {@link NotificationType notificationType}
     * <br>Requires the id of the User to whom this notification applies {@link User user}.
     * <br>{@link Notification entityId} - id of the entity about which the notification was sent (comment, friend, post or message)
     */
    public void createNotification(NotificationType notificationType, User user, Integer entityId)
    {
        try {
            if (user != null) {
                Notification notification = new Notification();
                notification.setType(notificationType);
                notification.setSentTime(LocalDateTime.now());
                notification.setUser(user);
                notification.setEntityId(entityId);
                notification.setEmail(user.getEmail());
                notification.setPhone(user.getPhone());
                notificationRepository.save(notification);

                sendToMail(notificationType, user.getEmail(), user);

            } else {
                throw new Exception("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToMail(NotificationType notificationType, String mail, User user)
    {
        Optional <Byte> optionalSetting = notificationSettingRepository.getNotificationSetting(user.getId(), notificationType);

        if (optionalSetting.isEmpty()) {

            NotificationSetting setting = new NotificationSetting();
            setting.setIsEnable((byte) 0);
            setting.setUserId(user.getId());
            setting.setType(notificationType);
            notificationSettingRepository.save(setting);

        } else if (optionalSetting.get() == 1) {
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
