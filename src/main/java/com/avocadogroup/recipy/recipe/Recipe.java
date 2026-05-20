package com.avocadogroup.recipy.recipe;

import com.avocadogroup.recipy.category.Category;
import com.avocadogroup.recipy.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description = null;

    @Column(name = "instructions", nullable = false, length = Integer.MAX_VALUE)
    private String instructions;

    @Column(name = "cooking_time", nullable = false)
    private Integer cookingTime;

    @Column(name = "difficulty", nullable = false, length = 10)
    private String difficulty;

    @Column(name = "image_url", length = 2048)
    private String imageUrl = null;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt; // Updated time will @UpdateTimestamp handle it

    /**
     * Marks this entity as deleted without removing it from the database.
     */
    public void softDelete() {
        // Mark as deleted
        this.deleted = true;
    }

    /**
     * Restores a soft-deleted entity.
     */
    public void restore(){
        // Mark as restored
        this.deleted = false;
    }
}