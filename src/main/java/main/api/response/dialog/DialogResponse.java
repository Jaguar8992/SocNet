package main.api.response.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.entity.Dialog;

@Getter
@Setter
public class DialogResponse extends AbstractResponse {

    @JsonProperty("unread_count")
    private int unreadCount;

    @JsonProperty("last_message")
    private MessageResponse lastMessage;

    public DialogResponse(Dialog dialog) {
        super(dialog.getId());
        this.unreadCount = dialog.getUnreadCount();
        this.lastMessage = new MessageResponse(dialog.getLastMessage());
    }
}
