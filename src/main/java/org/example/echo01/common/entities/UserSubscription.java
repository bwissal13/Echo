package org.example.echo01.common.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.echo01.auth.entities.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_subscribers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target_user_id", "subscriber_id"})
    }
)
@EqualsAndHashCode(callSuper = true)
public class UserSubscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @Column(name = "notify_on_new_book")
    @Builder.Default
    private boolean notifyOnNewBook = true;
} 