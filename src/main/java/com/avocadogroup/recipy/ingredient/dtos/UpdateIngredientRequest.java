package com.avocadogroup.recipy.ingredient.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateIngredientRequest {
    @Size(max = 255, message = "Ingredient name must not exceed 255 characters")
    private String name;

    @Size(max = 255, message = "Quantity must not exceed 255 characters")
    private String quantity;

    private String note;

    private Long recipeId;

}
