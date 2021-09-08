package main.service.friends;

import lombok.AllArgsConstructor;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.api.response.error.ErrorResponse;
import main.model.entity.Friendship;
import main.model.entity.User;
import main.model.entity.enums.FriendshipStatus;
import main.model.entity.enums.NotificationType;
import main.model.repository.FriendshipRepository;
import main.model.repository.UserRepository;
import main.service.NotificationApi;
import main.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SetFriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final Logger log = Logger.getLogger(SetFriendshipService.class.getName());
    private final NotificationApi notificationApi;

    public ResponseEntity<?> createResponse(Integer id, FriendshipStatus status) {

        Optional<User> friend = userRepository.findById(id);
        Friendship friendship;
        User user;
        User dstUser;

        //checking if the user is authorized
        try {
            user = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        //checking if the friend is get
        if (friend.isEmpty()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        dstUser = friend.get();

        if (user.equals(dstUser)) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        //Создать или получить friendship
        Optional<Friendship> friendshipOptional = friendshipRepository.findFriendshipForUser(user, dstUser);

        if (friendshipOptional.isEmpty() && status == FriendshipStatus.UNBLOCK) {
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        try {
            //If there is no friend request, create it
            if (friendshipOptional.isEmpty()) {
                friendship = createFriendShip(user, dstUser);
                friendship.setStatus(status);
            } else {
                friendship = friendshipOptional.get();
                //if there is a friend request, we accept it, set the Friend status
                if (friendship.getStatus() == FriendshipStatus.REQUEST && status == FriendshipStatus.REQUEST
                        && !friendship.getSrcUser().equals(user)) {
                    friendship.setStatus(FriendshipStatus.FRIEND);
                    //Set block status when users a Friends
                } else if (status == FriendshipStatus.BLOCKED) {
                    if (friendshipOptional.get().getStatus() != FriendshipStatus.BLOCKED) {
                        friendshipRepository.delete(friendship);
                        friendship.setStatus(FriendshipStatus.BLOCKED);
                    }
                    //unblock User
                } else if (friendship.getStatus() == FriendshipStatus.BLOCKED && status == FriendshipStatus.UNBLOCK && !friendship.getSrcUser().equals(user)) {
                    friendshipRepository.delete(friendship);
                    return ResponseEntity.ok(new DTOSuccessfully(
                            null,
                            Instant.now().getEpochSecond(),
                            new DTOMessage()));
                }
                //check blocked, declined and already friends
                else if (friendship.getStatus() != FriendshipStatus.BLOCKED && friendship.getStatus() != FriendshipStatus.DECLINED
                        && friendship.getStatus() != FriendshipStatus.FRIEND) {
                    friendship.setStatus(status);
                } else {
                    return ResponseEntity.status(400).body(new ErrorResponse(
                            DTOError.BAD_REQUEST.get(),
                            DTOErrorDescription.BAD_REQUEST.get()));
                }
            }

            friendshipRepository.save(friendship);

            //EntityId сущность относительно которой создано оповещение (сообщения, добавление в друзья и т.д.)
            if (friendship.getStatus() == FriendshipStatus.REQUEST) {
                notificationApi.createNotification(NotificationType.FRIEND_REQUEST, friendship.getDstUser(), friendship.getSrcUser().getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Successfully");

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                Instant.now().getEpochSecond(),
                new DTOMessage()));
    }

    private Friendship createFriendShip(User src, User dst) {
        Friendship friendship = new Friendship();
        friendship.setSrcUser(src);
        friendship.setDstUser(dst);
        friendship.setTime(LocalDateTime.now());

        return friendship;
    }

}
