package main.model.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(name = "post_text", nullable = false, columnDefinition = "TEXT")
    @Type(type = "text")
    private String postText;

    @Column(name = "is_blocked", nullable = false)
    private byte isBlocked;

    @Column(name = "is_deleted", nullable = false)
    private byte isDeleted = 0;

    @OneToMany(mappedBy = "entityId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> entity;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "post2tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @OneToMany(mappedBy = "post")
    private List<PostComment> comments;

    @Transient
    private long timestamp;

    @Transient
    private long likes;

    public Post(int id, Long timestamp, User author, String title, String postText,
                byte isBlocked, byte isDeleted, long likes) {
        this.id = id;
        this.timestamp = timestamp;
        this.author = author;
        this.title = title;
        this.postText = postText;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.likes = likes;
    }

    public List<Tag> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public boolean getIsBlocked() {
        return isBlocked == 1;
    }

    public void setIsBlocked(boolean blocked) {
        isBlocked = blocked ? (byte) 1 : (byte) 0;
    }

    public boolean getIsDeleted() {
        return isDeleted == 1;
    }

    public void setIsDeleted(boolean delete) {
        this.isDeleted = delete ? (byte) 1 : (byte) 0;
    }
}

