package org.example.echo01.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import org.example.echo01.auth.enums.Genre;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private boolean isPublic;
    private int views;

    @OneToMany(mappedBy = "book")
    private List<Chapter> chapters;
} 