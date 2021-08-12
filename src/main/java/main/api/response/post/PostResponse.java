package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.api.response.user.UserResponse;
import main.model.entity.Post;
import main.model.entity.User;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

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

    public PostResponse(Post post, List<CommentResponse> commentResponseList)
    {
        this.id = post.getId();
        this.timestamp = post.getTimestamp();
        this.userResponse = new UserResponse(post.getAuthor());
        this.title = post.getTitle();
        this.postText = post.getPostText();
        this.isBlocked = post.getIsBlocked();
        this.likes = post.getLikes();
        this.commentResponseList = commentResponseList;
    }
}
