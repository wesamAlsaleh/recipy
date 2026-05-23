package com.avocadogroup.recipy.recipe.dtos;

import com.avocadogroup.recipy.category.dtos.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class RecipeSummaryDto {
    private Long id;
    private String name;
    private String description;
    private Integer cookingTime;
    private String difficulty;
    private String imageUrl;
    private Boolean deleted;
    private CategoryDto category;
    private Instant createdAt;
    private Instant updatedAt;
}
