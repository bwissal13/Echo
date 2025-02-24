package org.example.echo01.common.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.enums.ReactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chapter_reactions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"chapter_id", "user_id"})
    }
)
@EqualsAndHashCode(callSuper = true)
public class ChapterReaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReactionType type;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
} 