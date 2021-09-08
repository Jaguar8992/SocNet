package main.controller;

import main.service.posts.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds")
public class ApiFeedsController {

    private final PostService postService;

    public ApiFeedsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    public ResponseEntity<?> getFeeds(@RequestParam(defaultValue = "", required = false) String name,
                                      @RequestParam(defaultValue = "0") Integer offset,
                                      @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return postService.createFeedsResponse("%" + name + "%", offset, itemPerPage);
    }

}
