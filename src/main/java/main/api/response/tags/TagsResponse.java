package main.api.response.tags;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class TagsResponse {

    private String error;
    private long timestamp;
    private long total;
    private int offset;
    private int perPage;
    private HashMap<Integer, String> data;

}
