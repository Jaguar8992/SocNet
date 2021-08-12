package main.service;

import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.response.post.*;
import main.model.entity.Post;
import main.model.entity.PostComment;
import main.model.entity.User;
import main.model.repository.PostRepository;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostCommentService postCommentService;
    private final Logger log = Logger.getLogger(PostService.class.getName());

    public PostService(PostRepository postRepository, UserService userService, PostCommentService postCommentService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.postCommentService = postCommentService;
    }

    public ResponseEntity<?> createPostResponse(String text, Long dateFromLong, Long dateToLong,
                                                Integer offset, Integer itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new Error(
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

        return new ResponseEntity<>(new PostResponseList(
                "string", getSeconds(new Date()),
                data.getTotalElements(), offset, itemPerPage,
                postResponses), HttpStatus.OK);

    }

    private List<PostResponse> getPostResponses(List<Post> content) {
        List<PostResponse> postResponseList = new ArrayList<>();

        content.forEach(post -> {
            List<CommentResponse> commentResponseList = getCommentResponses(post);
            PostResponse postResponse = new PostResponse(post, commentResponseList);
            postResponseList.add(postResponse);
        });

        return postResponseList;
    }

    private List<CommentResponse> getCommentResponses(Post post) {
        List<CommentResponse> commentResponseList = new ArrayList<>();
        List<PostComment> commentList = postCommentService.searchByPost(post);
        commentList.forEach(postComment -> {
            CommentResponse commentResponse = new CommentResponse(postComment);
            commentResponseList.add(commentResponse);
        });
        return commentResponseList;
    }

    private long getSeconds(Date time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(time);
        return calendar.getTimeInMillis() / 1000L;
    }

    public ResponseEntity<?> createWallPostResponse(Integer id, Integer offset, Integer itemPerPage) {

        try {
            userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new Error(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        User user;
        try {
            user = userService.getUserById(id);
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new Error(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        Pageable page = PageRequest.of(offset, itemPerPage);

        Page<Post> data = postRepository.getAllPostByUser(page, user);

        List<Post> content = data.getContent();

        List<WallPostResponse> wallPostResponseList = getWallPostResponses(content);

        return new ResponseEntity<>(new WallPostResponseList(
                "string", getSeconds(new Date()),
                data.getTotalElements(), offset, itemPerPage,
                wallPostResponseList), HttpStatus.OK);

    }

    private List<WallPostResponse> getWallPostResponses(List<Post> content) {
        List<WallPostResponse> wallPostResponseList = new ArrayList<>();

        content.forEach(post -> {
            List<CommentResponse> commentResponseList = getCommentResponses(post);
            WallPostResponse wallPostResponse = new WallPostResponse(post, commentResponseList);
            wallPostResponseList.add(wallPostResponse);
        });

        return wallPostResponseList;
    }
}
