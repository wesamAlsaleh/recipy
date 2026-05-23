package com.avocadogroup.recipy.passwordResetToken;

import com.avocadogroup.recipy.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "used", nullable = false)
    private Boolean used = false;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    /**
     * Checks if the token has expired by comparing the expiration time to the current time.
     *
     * @return {@code true} if the token has not yet expired, {@code false} otherwise
     */
    public boolean isValid() {
        // Return true if the expiration time is AFTER the current instant
        return this.expiredAt != null && this.expiredAt.isAfter(Instant.now());
    }

    /**
     * Marks the password reset token as used.
     */
    public void consume() {
        this.used = true;
    }
}
