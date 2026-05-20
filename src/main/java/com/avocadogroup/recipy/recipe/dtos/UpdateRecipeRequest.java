package com.avocadogroup.recipy.recipe.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateRecipeRequest {
    private  String name;
    private String description;
    private String instructions;
    private Integer cookingTime;
    private String difficulty;
    private String imageUrl;
    private Long categoryId;
}
