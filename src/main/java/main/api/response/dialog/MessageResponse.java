package main.api.response.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.Message;

import java.time.ZoneOffset;

@Getter
@Setter
public class MessageResponse extends AbstractResponse {

    private long time;

    @JsonProperty("author_id")
    private int authorId;

    @JsonProperty("recipient_id")
    private int recipientId;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private String readStatus;

    public MessageResponse(Message message) {
        super(message.getId());
        this.time = message.getTime().toEpochSecond(ZoneOffset.UTC);
        this.authorId = message.getAuthor().getId();
        this.recipientId = message.getRecipient().getId();
        this.messageText = message.getMessageText();
        this.readStatus = message.getReadStatus().toString();
    }
}
