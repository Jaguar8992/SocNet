package main.api.response.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.Post;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseList {

    private String error;

    private long timestamp;

    private long total;

    private int offset;

    private int perPage;

    private List<PostResponse> data;

}
