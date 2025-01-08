package org.example.echo01.auth.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.common.entities.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otps")
public class OTP extends BaseEntity {

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean used;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OTP otp = (OTP) o;
        return used == otp.used &&
               Objects.equals(code, otp.code) &&
               Objects.equals(email, otp.email) &&
               Objects.equals(expiryDate, otp.expiryDate) &&
               Objects.equals(user, otp.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, email, expiryDate, used, user);
    }
} 