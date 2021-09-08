package main.service.posts;

import lombok.AllArgsConstructor;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.request.PostRequest;
import main.api.response.CommonResponseList;
import main.api.response.PageCommonResponseList;
import main.api.response.error.ErrorResponse;
import main.api.response.post.CommentResponse;
import main.api.response.post.PostResponse;
import main.api.response.post.WallPostResponse;
import main.api.response.postcomments.PostCommentDeleteResponse;
import main.model.entity.*;
import main.model.entity.enums.NotificationType;
import main.model.repository.PostLikeRepository;
import main.model.repository.PostRepository;
import main.model.repository.TagRepository;
import main.service.NotificationApi;
import main.service.PostCommentService;
import main.service.UserService;
import main.service.friends.FriendsService;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final FriendsService friendsService;
    private final PostCommentService postCommentService;
    private final NotificationApi notificationApi;
    private final PostLikeRepository postLikeRepository;
    private final Logger log = Logger.getLogger(PostService.class.getName());
    private final TagRepository tagRepository;

    public ResponseEntity<?> createPostResponse(String text, Long dateFromLong, Long dateToLong,
                                                Integer offset, Integer itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Timestamp stampFrom = new Timestamp(dateFromLong);
        Timestamp stampTo = new Timestamp(dateToLong);

        if (dateToLong == 0) {
            stampTo = new Timestamp(System.currentTimeMillis());
        }

        LocalDateTime dateFrom = stampFrom.toLocalDateTime();
        LocalDateTime dateTo = stampTo.toLocalDateTime();

        Pageable page = PageRequest.of(offset, itemPerPage);

        Page<Post> data = postRepository.search(page, currentUser, text, dateFrom, dateTo);

        List<Post> content = data.getContent();

        List<PostResponse> postResponses = getPostResponses(content);

        return new ResponseEntity<>(new PageCommonResponseList<>(
                "string",
                data.getTotalElements(), offset, itemPerPage,
                postResponses), HttpStatus.OK);

    }

    private List<PostResponse> getPostResponses(List<Post> content) {
        List<PostResponse> postResponseList = new ArrayList<>();

        content.forEach(post -> {
            Optional<PostLike> postMyLike = postLikeRepository.findMyLikeByPostId(userService.getCurrentUser().getId(), post.getId());
            List<CommentResponse> commentResponseList = getCommentResponses(post);
            PostResponse postResponse;
            postResponse = postMyLike.map(postLike -> new PostResponse(post, commentResponseList, postLike, tagRepository.getNameByPost(post.getId())))
                    .orElseGet(() -> new PostResponse(post, commentResponseList, null, tagRepository.getNameByPost(post.getId())));
            postResponseList.add(postResponse);
        });

        return postResponseList;
    }

    private List<CommentResponse> getCommentResponses(Post post) {
        List<CommentResponse> commentResponseList = new ArrayList<>();
        List<PostComment> commentList = postCommentService.searchByPost(post);

        commentList.forEach(postComment -> {
            Optional<PostLike> commentMyLike = postLikeRepository
                    .findMyLikeInComment(
                            userService.getCurrentUser().getId(),
                            postComment.getId());

            if (userService.getCurrentUser().getId() == postComment.getAuthor().getId() && (postComment.getIsDeleted() || !postComment.getIsDeleted())) {
                CommentResponse commentResponse = new CommentResponse(postComment, commentMyLike.orElse(null));
                commentResponseList.add(commentResponse);
            }

            if (userService.getCurrentUser().getId() != postComment.getAuthor().getId() && !postComment.getIsDeleted()) {
                CommentResponse commentResponse = new CommentResponse(postComment, commentMyLike.orElse(null));
                commentResponseList.add(commentResponse);
            }

        });

        return commentResponseList;
    }

    private long getSeconds(Date time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(time);
        return calendar.getTimeInMillis() / 1000L;
    }

    public ResponseEntity<?> createWallPostResponse(Integer id, Integer offset, Integer itemPerPage) {

        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        User user;
        try {
            user = userService.getUserById(id);
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        Pageable page = PageRequest.of(offset, itemPerPage);

        List<User> users = new ArrayList<>();
        users.add(user);

        Page<Post> data = postRepository.getAllPostByUsers(page, users, currentUser);

        List<Post> content = data.getContent();

        List<WallPostResponse> wallPostResponseList = getWallPostResponses(content);

        return new ResponseEntity<>(new PageCommonResponseList<>(
                "string",
                data.getTotalElements(), offset, itemPerPage,
                wallPostResponseList), HttpStatus.OK);

    }

    private List<WallPostResponse> getWallPostResponses(List<Post> content) {
        List<WallPostResponse> wallPostResponseList = new ArrayList<>();

        content.forEach(post -> {
            Optional<PostLike> postMyLike = postLikeRepository.findMyLikeByPostId(userService.getCurrentUser().getId(), post.getId());
            List<CommentResponse> commentResponseList = getCommentResponses(post);
            WallPostResponse wallPostResponse = new WallPostResponse(post, commentResponseList, postMyLike.orElse(null),
                    tagRepository.getNameByPost(post.getId()));
            wallPostResponseList.add(wallPostResponse);
        });

        return wallPostResponseList;
    }

    public ResponseEntity<?> createFeedsResponse(String s, Integer offset, Integer itemPerPage) {

        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        List<User> allMyFriends = friendsService.getAllMyFriends(currentUser);

        Pageable page = PageRequest.of(offset, itemPerPage);

        Page<Post> data = postRepository.getAllPostByUsers(page, allMyFriends, currentUser);

        List<Post> content = data.getContent();

        List<PostResponse> postResponses = getPostResponses(content);

        return new ResponseEntity<>(new PageCommonResponseList(
                "string",
                data.getTotalElements(), offset, itemPerPage,
                postResponses), HttpStatus.OK);
    }

    public ResponseEntity<?> getPostById(Integer id) {
        Post post;
        try {
            post = postRepository
                    .getPostById(id)
                    .orElseThrow(() -> new EntityNotFoundException("post "
                            + id + " " + "not found"));
        } catch (EntityNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }
        Optional<PostLike> postMyLike = postLikeRepository.findMyLikeByPostId(userService.getCurrentUser().getId(), post.getId());

        return postMyLike.map(postLike -> ResponseEntity.ok(new PostResponse(post, getCommentResponses(post), postLike, tagRepository.getNameByPost(post.getId()))))
                .orElseGet(() -> ResponseEntity.ok(new PostResponse(post, getCommentResponses(post), null, tagRepository.getNameByPost(post.getId()))));

    }

    public ResponseEntity<?> addPostOnWall(Integer id, Long publishDate, PostRequest postRequest) {

        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        User user;
        try {
            user = userService.getUserById(id);
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        Post newPost = addPost(postRequest.getTitle(), postRequest.getPostText(), currentUser, publishDate);
        PostResponse postResponse = new PostResponse(newPost, new ArrayList<>(), null, postRequest.getTags());
        checkTagsForRelevance(postRequest.getTags(), newPost);

        List<User> allMyFriends = friendsService.getAllMyFriends(currentUser);
        allMyFriends.forEach(friend ->
                notificationApi.createNotification(NotificationType.POST, friend, currentUser.getId()));


        return new ResponseEntity<>(new CommonResponseList<>(
                "string",
                postResponse), HttpStatus.OK);

    }

    public Post addPost(String title, String postText, User author, Long publishDate) {
        Post post = new Post();
        Timestamp datePost = new Timestamp(publishDate);
        if (publishDate == 0L) {
            datePost = new Timestamp(System.currentTimeMillis());
        }
        post.setTime(datePost.toLocalDateTime());
        post.setTimestamp(datePost.getTime() / 1000);
        post.setAuthor(author);
        post.setTitle(title);
        post.setPostText(postText);
        return postRepository.save(post);
    }

    public ResponseEntity<?> editPost(int id, Long publishDate, PostRequest postRequest) {
        Post post;
        try {
            post = postRepository
                    .getPostById(id)
                    .orElseThrow(() -> new EntityNotFoundException("post "
                            + id + " " + "not found"));
        } catch (EntityNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        post.setTitle(postRequest.getTitle());
        post.setPostText(postRequest.getPostText());
        Timestamp datePost = new Timestamp(publishDate);
        if (publishDate == 0L) {
            datePost = new Timestamp(System.currentTimeMillis());
        }
        post.setTime(datePost.toLocalDateTime());

        Post newPost = postRepository.save(post);
        Optional<PostLike> postMyLike = postLikeRepository.findMyLikeByPostId(userService.getCurrentUser().getId(), post.getId());
        checkTagsForRelevance(postRequest.getTags(), post);

        return new ResponseEntity<>(new CommonResponseList<>(
                "string",
                new PostResponse(newPost, getCommentResponses(newPost), postMyLike.orElse(null), postRequest.getTags())), HttpStatus.OK);

    }

    public ResponseEntity<?> deletePost(int id) {

        Post post;
        try {
            post = postRepository
                    .getPostById(id)
                    .orElseThrow(() -> new EntityNotFoundException("post "
                            + id + " " + "not found"));
        } catch (EntityNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }
        post.setIsDeleted(true);
        Post newPost = postRepository.save(post);

        return new ResponseEntity<>(new CommonResponseList<>(
                "string",
                new PostCommentDeleteResponse(newPost.getId())), HttpStatus.OK);

    }

    public ResponseEntity<?> recoverPost(int id) {

        Post post;
        try {
            post = postRepository
                    .getPostById(id)
                    .orElseThrow(() -> new EntityNotFoundException("post "
                            + id + " " + "not found"));
        } catch (EntityNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }
        post.setIsDeleted(false);
        Post newPost = postRepository.save(post);
        Optional<PostLike> postMyLike = postLikeRepository.findMyLikeByPostId(userService.getCurrentUser().getId(), post.getId());

        return ResponseEntity.ok(new PostResponse(newPost, getCommentResponses(newPost), postMyLike.orElse(null), tagRepository.getNameByPost(post.getId())));
    }

    private void checkTagsForRelevance(List<String> tags, Post post) {
        if (tags == null) {
            return;
        }
        List<Tag> tagList = new ArrayList<>();

        for (String tagName : tags) {
            Tag tag;
            Optional<Tag> optionalTag = tagRepository.findTagByName(tagName);
            if (optionalTag.isPresent()) {
                tag = optionalTag.get();
            } else {
                tag = new Tag();
                tag.setName(tagName);
            }
            tagList.add(tag);
        }
        post.setTags(tagList);
        postRepository.save(post);

        tagRepository.excessTags().forEach(tagRepository::delete);
    }
}
