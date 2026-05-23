package com.avocadogroup.recipy.recipe;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @EntityGraph(attributePaths = "ingredients")
        // Left join ingredients "ingredients is a field name"
    Page<Recipe> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = "category") // Left join category "category is a field name"
    @Query("SELECT r FROM Recipe r WHERE r.deleted = FALSE AND (:difficulty IS NULL OR r.difficulty = :difficulty) AND (:categoryId IS NULL  OR r.category.id = :categoryId) ")
    Page<Recipe> findByFilters(
            @Param("categoryId") Long categoryId,
            @Param("difficulty") String difficulty,
            Pageable pageable
    );
}
