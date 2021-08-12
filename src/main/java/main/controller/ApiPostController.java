package main.controller;

import main.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    public ResponseEntity<?> searchPosts(@RequestParam(defaultValue = "") String text,
                                         @RequestParam(defaultValue = "0", name = "date_from") Long dateFrom,
                                         @RequestParam(defaultValue = "0", name = "date_to") Long dateTo,
                                         @RequestParam(defaultValue = "0") Integer offset,
                                         @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return postService.createPostResponse("%" + text + "%", dateFrom, dateTo, offset, itemPerPage);
    }

}
