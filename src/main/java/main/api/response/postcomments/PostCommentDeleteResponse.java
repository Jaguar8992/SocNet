package main.api.response.postcomments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCommentDeleteResponse {

    private Integer id;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    public PostCommentDeleteResponse(Integer id) {
        this.id = id;
    }
}
