package main.api.response.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.PostComment;
import main.model.entity.PostLike;

import java.time.ZoneOffset;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private int id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String photo;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("comment_text")
    private String commentText;

    @JsonProperty("post_id")
    private int postId;

    @JsonProperty("author_id")
    private int authorId;

    private Long time;

    @JsonProperty("my_like")
    private Boolean myLike;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    public CommentResponse(PostComment postComment, PostLike postMyLike) {
        this.id = postComment.getId();
        if (postComment.getParent() != null) {
            this.parentId = postComment.getParent().getId();
        }
        this.firstName = postComment.getAuthor().getFirstName();
        this.lastName = postComment.getAuthor().getLastName();
        this.photo = postComment.getAuthor().getPhoto();
        this.commentText = postComment.getCommentText();
        this.postId = postComment.getPost().getId();
        this.authorId = postComment.getAuthor().getId();
        this.time = postComment.getTime().toEpochSecond(ZoneOffset.of("+03:00"));
        this.myLike = postMyLike != null;
        this.isBlocked = postComment.getIsBlocked();
        this.isDeleted = postComment.getIsDeleted();
    }
}
