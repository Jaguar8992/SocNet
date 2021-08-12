package main.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.enums.NotificationType;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_setting")
public class NotificationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "type", nullable = false, columnDefinition = "enum('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE', 'FRIEND_BIRTHDAY')")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "is_enable")
    private byte isEnable;
}
