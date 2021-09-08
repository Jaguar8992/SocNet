package main.api.dto.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.api.dto.DTO;

@Data
public class DTOSendMessage implements DTO {

    private int id;
    private long time;
    @JsonProperty("author_id")
    private int authorId;
    @JsonProperty("message_text")
    private String messageText;
    @JsonProperty("read_status")
    private String readStatus;

    public DTOSendMessage(int id, long time, int authorId, String messageText) {
        this.id = id;
        this.time = time;
        this.authorId = authorId;
        this.messageText = messageText;
        this.readStatus = "SENT";
    }
}
