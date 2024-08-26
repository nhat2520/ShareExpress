package com.nhat.fileservice.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shares")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "folder_id")
    private Long folderId;

    @Column(name = "shared_with_user_id", nullable = false)
    private Long sharedWithUserId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "shared_at")
    private LocalDateTime sharedAt;

    // Getters and setters

    @PrePersist
    protected void onCreate() {
        sharedAt = LocalDateTime.now();
    }
}