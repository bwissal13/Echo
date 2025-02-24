package org.example.echo01.common.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.echo01.auth.entities.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_subscribers")
@EqualsAndHashCode(callSuper = true)
public class BookSubscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
} 