package main.api.response.postcomments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.PostComment;

import java.time.ZoneOffset;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentsResponse {

    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("post_id")
    private int postId;
    private long time;
    @JsonProperty("author_id")
    private int authorId;
    @JsonProperty("is_blocked")
    private boolean isBlocked;


    public PostCommentsResponse(PostComment postComment) {
        if (postComment.getParent() != null) this.parentId = postComment.getParent().getId();
        this.commentText = postComment.getCommentText();
        this.postId = postComment.getPost().getId();
        this.time = postComment.getTime().toEpochSecond(ZoneOffset.UTC);
        this.authorId = postComment.getAuthor().getId();
        this.isBlocked = postComment.getIsBlocked();
    }
}