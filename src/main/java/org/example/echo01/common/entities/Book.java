package org.example.echo01.common.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.enums.Genre;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE books SET deleted = true, deleted_at = NOW() WHERE id=?")
@Where(clause = "deleted=false")
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private boolean isPublic;
    
    @Builder.Default
    private int views = 0;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNumber ASC")
    @Builder.Default
    private List<Chapter> chapters = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_subscribers",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> subscribers = new HashSet<>();

    @Column(name = "notify_on_new_chapter")
    @Builder.Default
    private boolean notifyOnNewChapter = true;

    @Column(name = "notify_on_chapter_update")
    @Builder.Default
    private boolean notifyOnChapterUpdate = true;

    @Column(name = "notify_on_new_comment")
    @Builder.Default
    private boolean notifyOnNewComment = true;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "permanent_delete_at")
    private LocalDateTime permanentDeleteAt;

    @PreRemove
    public void preRemove() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.permanentDeleteAt = this.deletedAt.plusYears(5);
    }
} 