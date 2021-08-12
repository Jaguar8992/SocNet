package main.controller;

import main.service.PostService;
import main.service.profiles.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/users")
public class ApiProfileController {

    private final PostService postService;
    private final ProfileService profileService;

    public ApiProfileController(PostService postService, ProfileService profileService) {
        this.postService = postService;
        this.profileService = profileService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(
            @RequestParam(name = "first_name", defaultValue = "") String firstName,
            @RequestParam(name = "last_name", defaultValue = "") String lastName,
            @RequestParam(name = "age_from", defaultValue = "0") int ageFrom,
            @RequestParam(name = "age_to", defaultValue = "150") int ageTo,
            @RequestParam(name = "town_id", defaultValue = "0") int townId,
            @RequestParam(name = "country_id", defaultValue = "0") int countryId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int itemPerPage) {
        return profileService.createUsersSearchResponse(firstName, lastName, ageFrom, ageTo, townId, countryId, offset,
                itemPerPage);
    }


    @GetMapping("/{id}/wall")
    public ResponseEntity<?> getWall (@PathVariable Integer id,
                                      @RequestParam(defaultValue = "0") Integer offset,
                                      @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return postService.createWallPostResponse(id, offset, itemPerPage);
    }

    //GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id)
    {
        return profileService.getUserById(id);
    }

    //GET CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return profileService.getCurrentUser();
    }

    @DeleteMapping("/me")
    public ResponseEntity <?> deleteProfile (HttpServletRequest request, HttpServletResponse response){
        return profileService.deleteUser(request, response);
    }
}

