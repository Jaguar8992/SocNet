package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class PostCommentRequest {

    @JsonProperty("parent_id")
    private final Integer parentId;
    @JsonProperty("comment_text")
    private final String commentText;
}
