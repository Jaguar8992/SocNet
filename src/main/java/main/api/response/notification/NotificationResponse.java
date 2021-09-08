package main.api.response.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import main.model.entity.Notification;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
@Setter
public class NotificationResponse {

    //ID notification в MySQL
    private Integer id;

    @JsonProperty("type_id")
    private Integer typeId;

    @JsonProperty("sent_time")
    private Long sentTime;

    @JsonProperty("entity_id")
    private Integer entityId;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("entity_author")
    private NotificationUserData entityAuthor;

    public NotificationResponse(Notification notification, NotificationUserData notificationUserData) {
        ZonedDateTime now = ZonedDateTime.of(notification.getSentTime(), ZoneOffset.UTC);
        Timestamp timestamp = Timestamp.valueOf(now.toLocalDateTime());
        this.id = notification.getId();
        this.typeId = Integer.parseInt(notification.getType().get());
        this.sentTime = timestamp.getTime();
        this.entityId = notification.getEntityId();
        this.eventType = notification.getType().name();
        this.entityAuthor = notificationUserData;

    }
}
