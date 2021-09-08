package main.model.entity;

import lombok.Data;
import main.model.entity.enums.UserStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_print_status")
@Data
public class UserPrintStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "dialog_id", nullable = false)
    private int dialogId;

    @Column(nullable = false, columnDefinition = "enum('ACTIVE', 'PRINTS')")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime time;
}
