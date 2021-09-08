package main.controller;


import lombok.AllArgsConstructor;
import main.api.request.PostRequest;
import main.api.request.ProfileChangingRequest;
import main.model.entity.enums.FriendshipStatus;
import main.service.UserService;
import main.service.files.FileService;
import main.service.friends.SetFriendshipService;
import main.service.posts.PostService;
import main.service.profiles.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiProfileController {

    private final PostService postService;
    private final ProfileService profileService;
    private final FileService fileService;
    private final UserService userService;
    private final SetFriendshipService setFriendshipService;


    @GetMapping("/search")
    public ResponseEntity<?> searchUser(
            @RequestParam(name = "first_name", defaultValue = "") String firstName,
            @RequestParam(name = "last_name", defaultValue = "") String lastName,
            @RequestParam(name = "age_from", defaultValue = "0") int ageFrom,
            @RequestParam(name = "age_to", defaultValue = "150") int ageTo,
            @RequestParam(name = "city", defaultValue = "") String city,
            @RequestParam(name = "country", defaultValue = "") String country,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int itemPerPage) {
        return profileService.createUsersSearchResponse(firstName, lastName, ageFrom, ageTo, city, country, offset,
                itemPerPage);
    }


    @GetMapping("/{id}/wall")
    public ResponseEntity<?> getWall(@PathVariable Integer id,
                                     @RequestParam(defaultValue = "0") Integer offset,
                                     @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return postService.createWallPostResponse(id, offset, itemPerPage);
    }

    @PostMapping("/{id}/wall")
    public ResponseEntity<?> addPostOnWall(@PathVariable Integer id,
                                           @RequestParam(name = "publish_date", defaultValue = "0") Long publishDate,
                                           @RequestBody PostRequest postRequest) {
        return postService.addPostOnWall(id, publishDate, postRequest);
    }

    //GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        return profileService.getUserById(id);
    }

    //GET CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return profileService.getCurrentUser();
    }

    @PutMapping(value = "/me",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> changeUsersData(@RequestBody ProfileChangingRequest requestUser) {
        return profileService.updateUserInformation(requestUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteProfile(HttpServletRequest request, HttpServletResponse response) {
        return profileService.deleteUser(request, response);
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockUser(@PathVariable Integer id) {
        return setFriendshipService.createResponse(id, FriendshipStatus.BLOCKED);
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<?> unblockUser(@PathVariable Integer id) {
        return setFriendshipService.createResponse(id, FriendshipStatus.UNBLOCK);
    }
}

