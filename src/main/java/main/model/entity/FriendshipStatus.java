package main.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.enums.FriendshipStatusCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friendship_status")
public class FriendshipStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    private String name;

    @Enumerated(EnumType.STRING)
    private FriendshipStatusCode code;
}
