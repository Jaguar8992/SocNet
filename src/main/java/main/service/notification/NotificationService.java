package main.service.notification;

import lombok.RequiredArgsConstructor;
import main.api.response.PageCommonResponseList;
import main.api.response.notification.NotificationResponse;
import main.api.response.notification.NotificationUserData;
import main.model.entity.Notification;
import main.model.entity.User;
import main.model.entity.enums.NotificationType;
import main.model.repository.*;
import main.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageRepository messageRepository;
    private final PostCommentRepository postCommentRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    public ResponseEntity<?> getNotifications(Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        List<NotificationResponse> notificationResponses = new ArrayList<>();
        User user = userService.getCurrentUser();
        List<Notification> notificationList = notificationRepository.findAllByIdUser(user.getId(), getTypesForUser(user), pageable);

        for (int i = 0; i < notificationList.size(); i++) {
            User userFromEntityId = getUserForResponse(notificationList.get(i).getType(), notificationList, i);

            notificationResponses.add(
                    new NotificationResponse(notificationList.get(i),
                            new NotificationUserData(
                                    userFromEntityId.getPhoto() == null ? "" : userFromEntityId.getPhoto(),
                                    userFromEntityId.getFirstName(),
                                    userFromEntityId.getLastName())));
        }

        return new ResponseEntity<>(new PageCommonResponseList<>(
                "string",
                notificationList.size(),
                offset,
                itemPerPage,
                notificationResponses), HttpStatus.OK);
    }

    private User getUserForResponse(NotificationType type, List<Notification> notificationList, Integer index) {
        User userFromEntityId = null;
        try {
            switch (type) {
                case FRIEND_REQUEST:
                case FRIEND_BIRTHDAY: {
                    userFromEntityId = userRepository.findUserById(notificationList.get(index).getEntityId()).get();
                    break;
                }
                case POST: {
                    userFromEntityId = postRepository.findById(notificationList.get(index).getEntityId()).get().getAuthor();
                    break;
                }
                case MESSAGE: {
                    userFromEntityId = messageRepository.getById(notificationList.get(index).getEntityId()).getAuthor();
                    break;
                }
                case POST_COMMENT:
                case COMMENT_COMMENT: {
                    userFromEntityId = postCommentRepository.getById(notificationList.get(index).getEntityId()).getAuthor();
                    break;
                }
            }

            if (userFromEntityId == null) {
                throw new Exception("userFromEntityId == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userFromEntityId;
    }

    private List<NotificationType> getTypesForUser(User user) {
        List<NotificationType> notificationTypes = new ArrayList<>();

        NotificationType[] types = NotificationType.values();

        for (NotificationType type : types) {

            Optional<Byte> setting = notificationSettingRepository.getNotificationSetting(user, type);

            if (setting.isEmpty()) {
                continue;
            }
            if (setting.get() == 1) {
                notificationTypes.add(type);
            }
        }
        return notificationTypes;
    }

    public ResponseEntity<?> reedNotifications(Boolean booleans, Integer id) {
        Pageable pageable = PageRequest.of(0, 20);
        List<NotificationResponse> notificationResponses = new ArrayList<>();

        List<Notification> notificationList;
        if (booleans) {
            User user = userService.getCurrentUser();
            notificationList = notificationRepository.findAllByIdUser(user.getId(), getTypesForUser(user), pageable);
            notificationRepository.deleteAllByUserId(userService.getCurrentUser().getId());

            for (int i = 0; i < notificationList.size(); i++) {
                notificationList = notificationRepository.findAllByIdUser(userService.getCurrentUser().getId(), getTypesForUser(user), pageable);
                notificationRepository.deleteAllByUserId(userService.getCurrentUser().getId());
            }
        } else {
            notificationList = notificationRepository.findById(id, pageable);

            notificationRepository.deleteByUserId(id);
        }
        return getResponseEntity(notificationResponses, notificationList);
    }

    private ResponseEntity<?> getResponseEntity(List<NotificationResponse> notificationResponses, List<Notification> notificationList) {
        //Не понятно зачем при прочитывании на фронт возвращать что было прочинено. Ведь бы удаляем
        for (int i = 0; i < notificationList.size(); i++) {
            User userFromEntityId = getUserForResponse(notificationList.get(i).getType(), notificationList, i);

            notificationResponses.add(
                    new NotificationResponse(notificationList.get(i),
                            new NotificationUserData(
                                    userFromEntityId.getPhoto(),
                                    userFromEntityId.getFirstName(),
                                    userFromEntityId.getLastName())));
        }

        return new ResponseEntity<>(new PageCommonResponseList<>(
                "string",
                notificationList.size(),
                0,
                20,
                notificationResponses), HttpStatus.OK);
    }

}
