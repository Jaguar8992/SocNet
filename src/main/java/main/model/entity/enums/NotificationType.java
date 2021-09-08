package main.model.entity.enums;

public enum NotificationType {
    POST("1"),
    POST_COMMENT("2"),
    COMMENT_COMMENT("3"),
    FRIEND_REQUEST("4"),
    FRIEND_BIRTHDAY("5"),
    MESSAGE("6");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String get() {
        return type;
    }

}
