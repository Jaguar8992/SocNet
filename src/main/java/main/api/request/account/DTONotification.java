package main.api.request.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.model.entity.enums.NotificationType;

@Data
public class DTONotification {
    @JsonProperty("notification_type")
    private NotificationType notificationType;
    private boolean enable;
}
