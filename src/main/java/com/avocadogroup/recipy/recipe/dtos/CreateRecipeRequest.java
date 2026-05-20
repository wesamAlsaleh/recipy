package com.avocadogroup.recipy.recipe.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@Data
public class CreateRecipeRequest {
    @NotBlank(message = "Name is required")
    @NotNull(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private  String name;

    private String description; // optional

    @NotBlank(message = "Instructions are required")
    @NotNull(message = "Instructions are required")
    private String instructions;

    @NotNull(message = "Cooking time is required")
    @Positive(message = "Cooking time must be a positive number")
    private Integer cookingTime;

    @NotBlank(message = "Difficulty is required")
    @Size(max = 10, message = "Difficulty must not exceed 10 characters")

    private String difficulty;

    @Size(max = 2048, message = "Image URL must not exceed 2048 characters")
    @URL(message = "Image URL must be a valid URL")

    private String imageUrl;  // optional

    @NotNull(message = "Category is required")
    private Long categoryId;
}
