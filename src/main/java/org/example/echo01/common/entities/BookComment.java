package org.example.echo01.common.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.echo01.auth.entities.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_comments")
@EqualsAndHashCode(callSuper = true)
public class BookComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private BookComment parentComment;
} 