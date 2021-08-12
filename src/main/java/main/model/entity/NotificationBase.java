package main.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_base")
public class NotificationBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "type_id", nullable = false)
    private int typeId;

    @Column(name = "sent_time", nullable = false)
    private long sentTime;

    @Column(name = "entity_id", nullable = false)
    private int entityId;

    @Column(nullable = false)
    private String info;
}
