package com.avocadogroup.recipy.recipe.dtos;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@Data
public class UpdateRecipeRequest {
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private  String name;

    private String description; // no constraints, free text

    private String instructions; // no constraints, free text

    @Positive(message = "Cooking time must be a positive number")
    private Integer cookingTime;

    @Size(max = 10, message = "Difficulty must not exceed 10 characters")
    private String difficulty;

    @Size(max = 2048, message = "Image URL must not exceed 2048 characters")
    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;

    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;
}
