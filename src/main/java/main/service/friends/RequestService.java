package main.service.friends;

import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.model.entity.Friendship;
import main.model.entity.Notification;
import main.model.entity.User;
import main.model.entity.enums.FriendshipStatus;
import main.model.entity.enums.NotificationType;
import main.model.repository.FriendshipRepository;
import main.model.repository.NotificationRepository;
import main.model.repository.UserRepository;
import main.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class RequestService
{
    private final NotificationRepository notificationRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private Logger log = Logger.getLogger(RequestService.class.getName());

    public RequestService(NotificationRepository notificationRepository, FriendshipRepository friendshipRepository,
                          UserRepository userRepository, UserService userService)
    {
        this.notificationRepository = notificationRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public ResponseEntity createResponse(Integer dstUserId)
    {
        User currentUser;
        User dstUser;

        //Проверка авторизирвоан ли пользователь
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new Error(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Optional<User> friend = userRepository.findById(dstUserId);

        //Получен ли пользователь
        if (friend.isEmpty()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.status(400).body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        dstUser = friend.get();

        //Не является ли добавляемый user текущим
        if (dstUser == currentUser) {
            log.error("cannot add yourself");
            return ResponseEntity.status(400).body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        createFriendShip(currentUser, dstUser);

        createNotification(currentUser, dstUser);

        log.info("Successfully");

        return ResponseEntity.ok(new DTOSuccessfully(
            null,
            new Date().getTime() / 1000,
            new DTOMessage()));

    }

    private void createFriendShip(User current, User dst)
    {
        Friendship friendship = new Friendship();
        friendship.setStatus(FriendshipStatus.REQUEST);
        friendship.setTime(LocalDateTime.now());
        friendship.setDstUser(dst);
        friendship.setSrcUser(current);

        friendshipRepository.save(friendship);
    }

    //Todo сменить в будущем на NotificationApi
    private void createNotification(User current, User dst)
    {
        Notification notification;
        int friendshipId;

        Optional <Friendship> optionalFriendship = friendshipRepository.findFriendshipForUser(current, dst);
        if (optionalFriendship.isEmpty()){
            notification = new Notification();
            friendshipId = optionalFriendship.get().getId();

            notification.setType(NotificationType.FRIEND_REQUEST);
            notification.setUser(dst);
            notification.setEntityId(friendshipId);
            notification.setSentTime(LocalDateTime.now());
            //Todo send Email and sms
            notification.setEmail(dst.getEmail());
            notification.setPhone(dst.getPhone());

            notificationRepository.save(notification);
        }
        else log.error("request not found");
    }
}
