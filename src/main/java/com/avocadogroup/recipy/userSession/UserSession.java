package com.avocadogroup.recipy.userSession;

import com.avocadogroup.recipy.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // TODO: enhance the javadoc
    /**
     * Function to check if the token is valid
     *
     * @param userId
     * @return
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
     * Invalidates the current token by marking it as revoked and recording the exact timestamp.
     */
    public void revokeToken() {
        // Marks the token as invalid for future authentication checks
        this.revoked = true;
    }
}