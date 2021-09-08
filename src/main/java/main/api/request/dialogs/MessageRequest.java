package main.api.request.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageRequest {

    @JsonProperty("message_text")
    private String messageText;
}
