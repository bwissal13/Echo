package org.example.echo01.common.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.echo01.auth.entities.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmark_groups")
@EqualsAndHashCode(callSuper = true)
public class BookmarkGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "bookmark_group_books",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @Builder.Default
    private Set<Book> books = new HashSet<>();

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @PrePersist
    public void prePersist() {
        if (user != null) {
            setCreatedBy(user.getEmail());
            setUpdatedBy(user.getEmail());
            setModifiedBy(user.getEmail());
        }
        LocalDateTime now = LocalDateTime.now();
        setCreatedAt(now);
        setUpdatedAt(now);
        setModifiedAt(now);
    }

    @PreUpdate
    public void preUpdate() {
        if (user != null) {
            setUpdatedBy(user.getEmail());
            setModifiedBy(user.getEmail());
        }
        LocalDateTime now = LocalDateTime.now();
        setUpdatedAt(now);
        setModifiedAt(now);
    }
} 