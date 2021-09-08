package main.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    ADMIN(Set.of(Permission.USER, Permission.ADMIN, Permission.MODERATOR)),
    MODERATOR(Set.of(Permission.USER, Permission.MODERATOR)),
    USER(Set.of(Permission.USER));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream().map(
                        p -> new SimpleGrantedAuthority(p.getPermission())).
                collect(Collectors.toSet());
    }
}
