package com.avocadogroup.recipy.recipe.dtos;

import com.avocadogroup.recipy.category.dtos.CategoryDto;
import com.avocadogroup.recipy.ingredient.dtos.IngredientDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private String description;
    private String instructions;
    private Integer cookingTime;
    private String difficulty;
    private String imageUrl;
    private Boolean deleted;
    private CategoryDto category;
    private List<IngredientDto> ingredients;
    private Instant createdAt;
    private Instant updatedAt;
}
