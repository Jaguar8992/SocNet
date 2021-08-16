package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewDialogRequest {

    @JsonProperty("user_ids")
    private List userIds;
}
