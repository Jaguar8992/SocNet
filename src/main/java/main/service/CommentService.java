package main.service;

import lombok.AllArgsConstructor;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.request.PostCommentRequest;
import main.api.response.CommonResponseList;
import main.api.response.PageCommonResponseList;
import main.api.response.error.ErrorResponse;
import main.api.response.post.CommentResponse;
import main.api.response.postcomments.PostCommentDeleteResponse;
import main.api.response.postcomments.PostCommentsResponse;
import main.model.entity.Post;
import main.model.entity.PostComment;
import main.model.entity.PostLike;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostLikeRepository;
import main.model.repository.PostRepository;
import org.apache.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostLikeRepository postLikeRepository;
    private final Logger log = Logger.getLogger(CommentService.class.getName());

    public ResponseEntity<?> postComments(PostCommentRequest postCommentRequest, Integer postId) {
        Post post;
        try {
            post = postRepository
                    .getPostById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("post "
                            + postId + " not found"));
        } catch (EntityNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        PostComment postCommentRepositoryById = null;
        if (postCommentRequest.getParentId() != null) {
            postCommentRepositoryById = postCommentRepository.getById(postCommentRequest.getParentId());
        }

        List<PostCommentsResponse> commentsResponse = new ArrayList<>(2);

        PostComment postComment = new PostComment();
        postComment.setCommentText(postCommentRequest.getCommentText());
        postComment.setPost(post);
        postComment.setIsDeleted(false);
        postComment.setTime(LocalDateTime.now());
        postComment.setAuthor(userService.getCurrentUser());
        postComment.setParent(postCommentRepositoryById);
        postComment.setIsBlocked(userService.getCurrentUser().getIsBlocked());

        commentsResponse.add(new PostCommentsResponse(postComment));
        postCommentRepository.save(postComment);

        return ResponseEntity.ok(new CommonResponseList<>(
                "string",
                commentsResponse
        ));
    }

    public ResponseEntity<?> getComments(int postId, Integer offset, Integer itemPerPage) {
        if (getPost(postId)) return ResponseEntity.status(400).body(new ErrorResponse(
                DTOError.BAD_REQUEST.get(),
                DTOErrorDescription.BAD_REQUEST.get()));

        Pageable pageable = PageRequest.of(offset, itemPerPage);
        List<PostComment> postCommentList = postCommentRepository.searchCommentsByPostId(postId, pageable);
        List<CommentResponse> commentResponseList = new ArrayList<>();

        for (int i = 0; i < postCommentList.size(); i++) {
            Optional<PostLike> commentMyLike = postLikeRepository
                    .findMyLikeInComment(
                            userService.getCurrentUser().getId(),
                            postCommentList.get(i).getId());

            if (userService.getCurrentUser().getId() == postCommentList.get(i).getAuthor().getId() && (postCommentList.get(i).getIsDeleted() || !postCommentList.get(i).getIsDeleted())) {
                commentResponseList.add(new CommentResponse(postCommentList.get(i), commentMyLike.orElse(null)));
            }

            if (userService.getCurrentUser().getId() != postCommentList.get(i).getAuthor().getId() && !postCommentList.get(i).getIsDeleted()) {
                commentResponseList.add(new CommentResponse(postCommentList.get(i), commentMyLike.orElse(null)));
            }
        }

        return ResponseEntity.ok(new PageCommonResponseList<>(
                "string",
                commentResponseList.size(),
                offset,
                itemPerPage,
                commentResponseList));
    }

    public ResponseEntity<?> editComment(PostCommentRequest postCommentRequest, Integer postId, Integer commentId) {
        if (getPost(postId)) return ResponseEntity.status(400).body(new ErrorResponse(
                DTOError.BAD_REQUEST.get(),
                DTOErrorDescription.BAD_REQUEST.get()));

        PostComment postComment = postCommentRepository.getPostCommentById(postId, commentId);
        if (postComment == null) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        } else {
            if (postComment.getAuthor().getId() != userService.getCurrentUser().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                        DTOError.BAD_REQUEST.get(),
                        DTOErrorDescription.BAD_REQUEST.get()));
            }
            postComment.setCommentText(postCommentRequest.getCommentText());
            postCommentRepository.save(postComment);

            return ResponseEntity.ok(new CommonResponseList<>(
                    "string",
                    new PostCommentsResponse(postComment)));
        }
    }

    public ResponseEntity<?> deleteComment(Integer postId, Integer commentId) {
        if (getPost(postId)) return ResponseEntity.status(400).body(new ErrorResponse(
                DTOError.BAD_REQUEST.get(),
                DTOErrorDescription.BAD_REQUEST.get()));

        PostComment postComment = postCommentRepository.getPostCommentById(postId, commentId);
        if (postComment == null) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        if (postComment.getAuthor().getId() != userService.getCurrentUser().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        postComment.setIsDeleted(true);
        postCommentRepository.save(postComment);
        return ResponseEntity.ok(new CommonResponseList<>(
                "string",
                new PostCommentDeleteResponse(commentId, true)));
    }

    public ResponseEntity<?> recoverComment(Integer postId, Integer commentId) {
        if (getPost(postId)) return ResponseEntity.status(400).body(new ErrorResponse(
                DTOError.BAD_REQUEST.get(),
                DTOErrorDescription.BAD_REQUEST.get()));

        PostComment postComment = postCommentRepository.getPostCommentById(postId, commentId);

        if (postComment == null) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        if (postComment.getAuthor().getId() != userService.getCurrentUser().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        postComment.setIsDeleted(false);
        postCommentRepository.save(postComment);

        return ResponseEntity.ok(new CommonResponseList<>(
                "string",
                new PostCommentsResponse(postComment)));
    }

    private boolean getPost(int postId) {
        try {
            Post post = postRepository
                    .getPostById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("post "
                            + postId + " not found"));
        } catch (EntityNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return true;
        }
        return false;
    }

}
