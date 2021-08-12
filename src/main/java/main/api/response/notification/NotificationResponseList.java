package main.api.response.notification;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NotificationResponseList {

    private String error;
    private long timestamp;
    private int total;
    private int offset;
    private int perPage;
    private List<NotificationResponse> data;

    public NotificationResponseList(String error, long timestamp, int total, int offset, int perPage, List<NotificationResponse> data) {
        this.error = error;
        this.timestamp = timestamp;
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }

}
