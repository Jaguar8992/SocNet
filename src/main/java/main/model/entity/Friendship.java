package main.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.enums.FriendshipStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friendship")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    /**
     * srcUser - Пользователь отправивший запрос на дружбу
     */
    @ManyToOne
    @JoinColumn(name = "src_user_id", nullable = false)
    private User srcUser;

    /**
     * dstUser Пользователь кому пришел запрос на дружбу
     */
    @ManyToOne
    @JoinColumn(name = "dst_user_id", nullable = false)
    private User dstUser;

    @OneToMany(mappedBy = "entityId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> entity;

    @Column(columnDefinition = "DATETIME(6)")
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum('REQUEST','FRIEND','BLOCKED','DECLINED','SUBSCRIBED')")
    private FriendshipStatus status;

}
