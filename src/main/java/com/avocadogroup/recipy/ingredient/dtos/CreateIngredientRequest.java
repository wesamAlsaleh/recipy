package com.avocadogroup.recipy.ingredient.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateIngredientRequest {
    @NotBlank(message = "Ingredient name is required")
    @NotNull(message = "Ingredient name is required")
    @Size(max = 255, message = "Ingredient name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Quantity is required")
    @NotNull(message = "Quantity is required")
    @Size(max = 255, message = "Quantity must not exceed 255 characters")
    private String quantity;

    private String note;

    @NotNull(message = "Recipe ID is required")
    private Long recipeId;
}
