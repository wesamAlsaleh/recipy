package com.avocadogroup.recipy.verificationToken;

import com.avocadogroup.recipy.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Function to check if the token is valid
     *
     * @param userId user id to validate the token
     * @return true if the token is valid and belongs to the user
     */
    public boolean isValid(Long userId) {
        // If the token does not belong to the real user
        if (user == null || user.getId() == null || !user.getId().equals(userId)) {
            return false;
        }

        // If the token is expired return false
        if (expiryDate.isBefore(Instant.now())) {
            return false;
        }

        // Return true if the token is not revoked
        return !revoked;
    }

    /**
     * Invalidates the current token by marking it as revoked
     */
    public void revokeToken() {
        // Marks the token as invalid for future authentication checks
        this.revoked = true;
    }
}