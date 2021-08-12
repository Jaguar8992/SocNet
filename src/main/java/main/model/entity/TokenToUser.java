package main.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "token2user")
public class TokenToUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime time;

}
