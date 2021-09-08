package main.api.response.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.Message;
import main.model.entity.User;

import java.time.ZoneOffset;


@Getter
@Setter
@NoArgsConstructor
public class MessageResponse {

    private int id;

    private long time;

    @JsonProperty("isSentByMe")
    private boolean isSentByMe;

    @JsonProperty("message_text")
    private String messageText;

    public MessageResponse(Message message, User currentUser) {
        this.id = message.getId();
        this.time = message.getTime().toEpochSecond(ZoneOffset.UTC);
        User author = message.getAuthor();
        if (author.getId() == currentUser.getId()) {
            this.isSentByMe = true;
        }
        this.messageText = message.getMessageText();
    }
}
