package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LikeRequest {

    @JsonProperty("item_id")
    private int itemId;

    private String type;
}
