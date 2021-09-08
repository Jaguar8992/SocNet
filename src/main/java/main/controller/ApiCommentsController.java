package main.controller;

import lombok.AllArgsConstructor;
import main.api.dto.DTOError;
import main.api.request.PostCommentRequest;
import main.api.response.error.ErrorResponse;
import main.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/post")
public class ApiCommentsController {

    private final CommentService commentService;

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable int id,
                                         @RequestParam(defaultValue = "0") Integer offset,
                                         @RequestParam(defaultValue = "20") Integer itemPerPage,
                                         Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return commentService.getComments(id, offset, itemPerPage);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> postComment(@RequestBody PostCommentRequest postCommentRequest, Principal principal, @PathVariable Integer id) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return commentService.postComments(postCommentRequest, id);
    }

    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<?> editComment(@RequestBody PostCommentRequest postCommentRequest,
                                         @PathVariable Integer id,
                                         @PathVariable Integer commentId,
                                         Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return commentService.editComment(postCommentRequest, id, commentId);
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id, @PathVariable Integer commentId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return commentService.deleteComment(id, commentId);
    }

    @PutMapping("/{id}/comments/{commentId}/recover")
    public ResponseEntity<?> recoverComment(@PathVariable Integer id, @PathVariable Integer commentId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return commentService.recoverComment(id, commentId);
    }

}

