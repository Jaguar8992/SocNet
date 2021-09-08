package main.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "invite_url")
    private String inviteUrl;

    @OneToMany(mappedBy = "dialog", fetch = FetchType.LAZY)
    private List<Message> messages;

    @OneToOne
    @JoinColumn(name = "last_message")
    private Message lastMessage;

    public Dialog() {
        byte[] array = new byte[25];
        new Random().nextBytes(array);
        this.inviteUrl = UUID.nameUUIDFromBytes(array).toString();
    }

}
