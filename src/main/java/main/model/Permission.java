package main.model;

public enum Permission {
    USER("user:write"),
    MODERATOR("user:moderation"),
    ADMIN("user:administration");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
