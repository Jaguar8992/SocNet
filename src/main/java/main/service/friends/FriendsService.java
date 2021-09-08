package main.service.friends;

import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.api.response.PageCommonResponseList;
import main.api.response.user.UserResponse;
import main.model.entity.User;
import main.model.repository.FriendshipRepository;
import main.model.repository.UserRepository;
import main.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendsService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final FriendshipRepository friendshipRepository;
    private final Logger logger = Logger.getLogger(FriendsService.class.getName());

    @Autowired
    public FriendsService(UserRepository userRepository, UserService userService, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
    }

    public List<User> getAllMyFriends(User user) {
        return userRepository.getAllMyFriends(user);
    }

    public PageCommonResponseList<?> getFriends(String name, Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        List<User> userPage;
        if (!name.equals("")) {
            userPage = userRepository.getFriendsByName(userService.getCurrentUser(), name, pageable);
        } else {
            userPage = getAllMyFriends(userService.getCurrentUser());
        }

        List<UserResponse> friendsData = new ArrayList<>();

        for (int i = 0; i < userPage.size(); i++) {
            friendsData.add(new UserResponse(userPage.get(i)));
        }

        logger.info(FriendsService.class.getName() + " getFriends() " + " Successfully");

        return new PageCommonResponseList<>(
                "string",
                userPage.size(),
                offset,
                itemPerPage,
                friendsData);
    }

    public PageCommonResponseList<?> getRecommendations(Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        Page<User> friendsRecommendations = userRepository.getFriendsRecommendations(userService.getCurrentUser(), pageable);
        List<User> content = friendsRecommendations.getContent();

        ArrayList<UserResponse> friends = new ArrayList<>();

        content.forEach(user -> friends.add(new UserResponse(user)));

        logger.info(FriendsService.class.getName() + " getRecommendations() " + " Successfully");

        return new PageCommonResponseList<>(
                "string",
                content.size(),
                offset,
                itemPerPage,
                friends);
    }

    public PageCommonResponseList<?> getRequests(String name, Integer offset, Integer itemPerPage) {
        Pageable pageable = PageRequest.of(offset, itemPerPage);
        //TODO: add check for null user
        Page<User> userPage;

        if (!name.equals("")) {
            userPage = userRepository.getFriendRequestsByName(userService.getCurrentUser(), name, pageable);
        } else {
            userPage = userRepository.getFriendRequestsAll(userService.getCurrentUser(), pageable);
        }

        List<UserResponse> friendsData = new ArrayList<>();

        for (int i = 0; i < userPage.getContent().size(); i++) {
            friendsData.add(new UserResponse(userPage.getContent().get(i)));
        }

        logger.info(FriendsService.class.getName() + " getRequests() " + " Successfully");

        return new PageCommonResponseList<>(
                "string",
                userPage.getContent().size(),
                offset,
                itemPerPage,
                friendsData);
    }

    public ResponseEntity<?> deleteFriend(Integer id) {

        friendshipRepository.deleteFriend(userService.getCurrentUser().getId(), id);

        return new ResponseEntity<>(new DTOSuccessfully(
                "string", Instant.now().getEpochSecond(), new DTOMessage()),
                HttpStatus.OK);
    }

}
