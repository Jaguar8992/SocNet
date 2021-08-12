package main.model.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.Role;
import main.model.entity.enums.MessagesPermission;
import main.model.entity.enums.UserType;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @CreationTimestamp
    @Column(name = "reg_date", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "birth_date", columnDefinition = "DATE")
    private LocalDateTime birthDate;

    @Column(nullable = false)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(columnDefinition = "TEXT")
    private String about;

    @ManyToOne
    @JoinColumn(name = "town_id")
    private Town town;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "is_approved", nullable = false)
    private byte isApproved;

    @Column(name = "messages_permission", nullable = false, columnDefinition = "enum('ALL', 'FRIENDS')")
    @Enumerated(EnumType.STRING)
    private MessagesPermission messagesPermission;

    @Column(name = "last_online_time", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime lastOnlineTime;

    @Column(name = "is_blocked", nullable = false)
    private byte isBlocked;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BlockHistory> blockHistories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComment> comments;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id")
    private List <NotificationSetting> notificationSetting;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user2dialog",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "dialog_id"))
    private List<Dialog> dialogs;

    public boolean getIsApproved() {
        return isApproved == 1;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved ? (byte) 1 : (byte) 0;
    }

    public boolean getIsBlocked() {
        return isBlocked == 1;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked ? (byte) 1 : (byte) 0;
    }

    public Role getRole() {
        if (type.toString().equals("MODERATOR")) {
            return Role.MODERATOR;
        }
        if (type.toString().equals("ADMIN")) {
            return Role.ADMIN;
        }
        return Role.USER;
    }
}
