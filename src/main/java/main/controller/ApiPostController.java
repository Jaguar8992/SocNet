package main.controller;

import main.api.dto.DTOError;
import main.api.request.PostRequest;
import main.api.response.error.ErrorResponse;
import main.service.PostCommentService;
import main.service.posts.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/post")
public class ApiPostController {
    private final PostService postService;
    private final PostCommentService postCommentService;

    public ApiPostController(PostService postService, PostCommentService postCommentService) {
        this.postService = postService;
        this.postCommentService = postCommentService;
    }

    @GetMapping("")
    public ResponseEntity<?> searchPosts(@RequestParam(defaultValue = "") String text,
                                         @RequestParam(defaultValue = "0", name = "date_from") Long dateFrom,
                                         @RequestParam(defaultValue = "0", name = "date_to") Long dateTo,
                                         @RequestParam(defaultValue = "0") Integer offset,
                                         @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return postService.createPostResponse("%" + text + "%", dateFrom, dateTo, offset, itemPerPage);
    }

    //GET POST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable int id) {
        return postService.getPostById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable int id,
                                      @RequestParam(name = "publish_date", defaultValue = "0") Long publishDate,
                                      @RequestBody PostRequest postRequest,
                                      Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return postService.editPost(id, publishDate, postRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable int id, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return postService.deletePost(id);
    }

    @PutMapping("/{id}/recover")
    public ResponseEntity<?> recoverPost(@PathVariable int id, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return postService.recoverPost(id);
    }
}
