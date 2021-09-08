package main.api.response.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import main.api.response.user.UserResponse;
import main.model.entity.Dialog;
import main.model.entity.User;

@Getter
@Setter
public class DialogResponse {

    private int id;

    @JsonProperty("unread_count")
    private int unreadCount;

    @JsonProperty("recipient")
    private UserResponse recipient;

    @JsonProperty("last_message")
    private MessageResponse lastMessage;

    public DialogResponse(Dialog dialog, User currentUser, Long unreadMessage) {
        this.id = dialog.getId();
        this.unreadCount = unreadMessage == null ? 0 : unreadMessage.intValue();
        if (dialog.getOwner().equals(currentUser)) {
            this.recipient = new UserResponse(dialog.getRecipient());
        } else {
            this.recipient = new UserResponse(dialog.getOwner());
        }
        if (dialog.getLastMessage() != null) {
            this.lastMessage = new MessageResponse(dialog.getLastMessage(), currentUser);
        } else {
            this.lastMessage = new MessageResponse();
        }
    }
}
