package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.api.response.user.UserResponse;
import main.model.entity.Post;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WallPostResponse {

    private int id;

    @JsonProperty("time")
    private Long timestamp;

    @JsonProperty("author")
    private UserResponse userResponse;

    private String title;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    private long likes;

    @JsonProperty("comments")
    private List<CommentResponse> commentResponseList;

    private DTOTypePost type;

    public WallPostResponse(Post post, List<CommentResponse> commentResponseList)
    {
        this.id = post.getId();
        this.timestamp = post.getTimestamp();
        this.userResponse = new UserResponse(post.getAuthor());
        this.title = post.getTitle();
        this.postText = post.getPostText();
        this.isBlocked = post.getIsBlocked();
        this.likes = post.getLikes();
        this.commentResponseList = commentResponseList;
        long currentTime = System.currentTimeMillis();
        this.type = post.getTimestamp() > currentTime ? DTOTypePost.QUEUED :
                DTOTypePost.POSTED;
    }

}