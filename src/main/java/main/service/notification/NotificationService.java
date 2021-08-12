package main.service.notification;

import lombok.RequiredArgsConstructor;
import main.api.response.notification.NotificationResponse;
import main.api.response.notification.NotificationResponseList;
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
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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


    public ResponseEntity<?> getNotifications(Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        List<NotificationResponse> notificationResponses = new ArrayList<>();
        List<Notification> notificationList = notificationRepository.findAllByIdUser(userService.getCurrentUser().getId(), pageable);

        for (int i = 0; i < notificationList.size(); i++) {
            User userFromEntityId = getUserForResponse(notificationList.get(i).getType(), notificationList, i);

            notificationResponses.add(
                    new NotificationResponse(notificationList.get(i),
                            new NotificationUserData(
                                    userFromEntityId.getPhoto(),
                                    userFromEntityId.getFirstName(),
                                    userFromEntityId.getLastName())));
        }

        return new ResponseEntity<>(new NotificationResponseList(
                "string",
                Instant.now().getEpochSecond(),
                notificationList.size(),
                offset,
                itemPerPage,
                notificationResponses), HttpStatus.OK);
    }

    private User getUserForResponse(NotificationType type, List<Notification> notificationList, Integer index) {
        User userFromEntityId = null;
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
        try {
            if (userFromEntityId == null) {
                throw new Exception("userFromEntityId == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userFromEntityId;
    }


    public ResponseEntity<?> reedNotifications(Boolean booleans, Integer id, Principal principal) {
        Pageable pageable = PageRequest.of(0, 20);
        List<NotificationResponse> notificationResponses = new ArrayList<>();

        if (booleans) {
            List<Notification> notificationList = notificationRepository.findAllByIdUser(userService.getCurrentUser().getId(), pageable);
            notificationRepository.deleteAllByUserId(userService.getCurrentUser().getId());

            for (int i = 0; i < notificationList.size(); i++) {
                User userFromEntityId = getUserForResponse(notificationList.get(i).getType(), notificationList, i);

                notificationResponses.add(
                        new NotificationResponse(notificationList.get(i),
                                new NotificationUserData(
                                        userFromEntityId.getPhoto(),
                                        userFromEntityId.getFirstName(),
                                        userFromEntityId.getLastName())));
            }

            return new ResponseEntity<>(new NotificationResponseList(
                    "string",
                    Instant.now().getEpochSecond(),
                    notificationList.size(),
                    0,
                    20,
                    notificationResponses), HttpStatus.OK);
        } else {
            List<Notification> notificationList = notificationRepository.findById(id, pageable);
            notificationRepository.deleteAllByUserId(userService.getCurrentUser().getId());

            for (int i = 0; i < notificationList.size(); i++) {
                User userFromEntityId = getUserForResponse(notificationList.get(i).getType(), notificationList, i);

                notificationResponses.add(
                        new NotificationResponse(notificationList.get(i),
                                new NotificationUserData(
                                        userFromEntityId.getPhoto(),
                                        userFromEntityId.getFirstName(),
                                        userFromEntityId.getLastName())));
            }

            return new ResponseEntity<>(new NotificationResponseList(
                    "string",
                    Instant.now().getEpochSecond(),
                    notificationList.size(),
                    0,
                    20,
                    notificationResponses), HttpStatus.OK);
        }
    }
}
