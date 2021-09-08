package main.service;

import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOSuccessfully;
import main.api.dto.likes.DTOHasLike;
import main.api.dto.likes.DTOLikes;
import main.api.response.error.ErrorResponse;
import main.model.entity.Post;
import main.model.entity.PostComment;
import main.model.entity.PostLike;
import main.model.entity.User;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostLikeRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final Logger log = Logger.getLogger(LikeService.class.getName());

    @Autowired
    public LikeService(PostRepository postRepository, PostCommentRepository postCommentRepository, PostLikeRepository postLikeRepository, UserService userService, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.postLikeRepository = postLikeRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getLiked(int userId, int itemId, String type) {

        try {
            userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (!checkIsCorrect(itemId, type)) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        Optional<PostLike> like;
        DTOHasLike hasLike = new DTOHasLike();
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        switch (type) {
            case ("Post"):
                like = postLikeRepository.findByPostForUser(userId, itemId);
                break;
            case ("Comment"):
                like = postLikeRepository.findByCommentForUser(userId, itemId);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        hasLike.setLikes(like.isPresent());

        return ResponseEntity.ok(new DTOSuccessfully("string", timestamp, hasLike));
    }

    public ResponseEntity<?> getLikes(int itemId, String type) {
        try {
            userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (!checkIsCorrect(itemId, type)) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }


        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();
        DTOLikes likes = getDTOLikes(itemId, type);

        return ResponseEntity.ok(new DTOSuccessfully("string", timestamp, likes));
    }

    public ResponseEntity<?> putLike(int itemId, String type) {

        User currentUser;

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (!createLike(currentUser, itemId, type)) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        } else {
            long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();
            DTOLikes likes = getDTOLikes(itemId, type);

            return ResponseEntity.ok(new DTOSuccessfully("string", timestamp, likes));
        }
    }

    public ResponseEntity<?> deleteLike(int itemId, String type) {
        User currentUser;

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (!deleteLike(currentUser, itemId, type)) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();
        DTOLikes likes = getDTOLikes(itemId, type);

        return ResponseEntity.ok(new DTOSuccessfully("string", timestamp, likes));
    }


    private boolean checkIsCorrect(int itemId, String type) {
        switch (type) {
            case ("Post"):
                if (postRepository.getPostById(itemId).isPresent()) {
                    return true;
                }
                break;
            case ("Comment"):
                if (postCommentRepository.findById(itemId).isPresent()) {
                    return true;
                }
                break;
        }
        return false;
    }

    private DTOLikes getDTOLikes(int itemId, String type) {
        DTOLikes likes = new DTOLikes();
        List<Integer> users;

        switch (type) {
            case ("Post"):
                users = postLikeRepository.findUsersByPost(itemId);
                break;
            case ("Comment"):
                users = postLikeRepository.findUsersByComment(itemId);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        likes.setLikes(users.size());
        likes.setUsers(users);

        return likes;
    }

    private boolean createLike(User user, int itemId, String type) {
        PostLike like = new PostLike();

        like.setTime(LocalDateTime.now());
        like.setUser(user);

        switch (type) {
            case ("Post"):
                Optional<Post> postOptional = postRepository.getPostById(itemId);
                Post post;
                if (postOptional.isPresent()) {
                    post = postOptional.get();
                } else return false;

                if (postLikeRepository.findByPostForUser(user.getId(), post.getId()).isEmpty()) {
                    like.setPost(post);
                    postLikeRepository.save(like);
                }
                break;
            case ("Comment"):
                Optional<PostComment> postCommentOptional = postCommentRepository.findById(itemId);
                PostComment comment;
                if (postCommentOptional.isPresent()) {
                    comment = postCommentOptional.get();
                } else return false;

                if (postLikeRepository.findByCommentForUser(user.getId(), itemId).isEmpty()) {
                    like.setPostComment(comment);
                    postLikeRepository.save(like);
                }
                break;
        }
        return true;
    }

    private boolean deleteLike(User user, int itemId, String type) {
        Optional<PostLike> optional;
        switch (type) {
            case ("Post"):
                optional = postLikeRepository.findByPostForUser(user.getId(), itemId);
                break;
            case ("Comment"):
                optional = postLikeRepository.findByCommentForUser(user.getId(), itemId);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        if (optional.isEmpty()) {
            return false;
        }

        postLikeRepository.delete(optional.get());

        return true;
    }
}
