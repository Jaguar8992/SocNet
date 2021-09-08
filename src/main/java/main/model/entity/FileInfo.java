package main.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "file_info")
public class FileInfo {
    /**
     * {
     * "id": "string",
     * "ownerId": 12,
     * "fileName": "string",
     * "relativeFilePath": "string",
     * "rawFileURL": "string",
     * "fileFormat": "string",
     * "bytes": 0,
     * "fileType": "IMAGE",
     * "createdAt": 0
     * }
     * }
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private String id;

    @Column(name = "hash_file", nullable = false)
    private String hashFile;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User ownerId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "relative_file_path", nullable = false)
    private String relativeFilePath;

    @Column(name = "raw_file_url", nullable = false)
    private String rawFileURL;

    @Column(name = "file_format", nullable = false)
    private String fileFormat;

    private long bytes;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(columnDefinition = "DATETIME(6)", name = "created_at", nullable = false)
    private LocalDate createdAt;
}
