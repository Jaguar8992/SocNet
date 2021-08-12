package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.PostComment;

import java.time.ZoneOffset;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private int id;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("comment_text")
    private String commentText;

    @JsonProperty("post_id")
    private int postId;

    @JsonProperty("author_id")
    private int authorId;

    private Long time;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    public CommentResponse(PostComment postComment) {
        this.id = postComment.getId();
        if (postComment.getParent() != null) {
            this.parentId = postComment.getParent().getId();
        }
        this.commentText = postComment.getCommentText();
        this.postId = postComment.getPost().getId();
        this.authorId = postComment.getAuthor().getId();
        this.time = postComment.getTime().toEpochSecond(ZoneOffset.of("+03:00"));
        this.isBlocked = postComment.getIsBlocked();
    }
}
