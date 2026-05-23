package com.avocadogroup.recipy.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "profile_image_url", length = 2048)
    private String profileImageUrl = null;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified= false;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    /**
     * Function to verify the user
     */
    public void verify() {
        this.emailVerified = true;
    }

    /**
     * Soft-deletes this user by marking them as deleted and inactive.
     */
    public void softDelete() {
        // Mark as deleted
        this.deleted = true;

        // Mark as inactive
        this.isActive = false;
    }

    /**
     * Restores a soft-deleted user by marking them as not deleted and active again.
     */
    public void restore() {
        // Mark as not deleted
        this.deleted = false;

        // Activate the account
        this.isActive = true;
    }
}