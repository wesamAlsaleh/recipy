package com.avocadogroup.recipy.ingredient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientDto {
    private Long id;
    private String name;
    private String quantity;
    private String note;
    private Long recipeId;

}
