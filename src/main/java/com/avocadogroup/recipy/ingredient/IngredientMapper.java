package com.avocadogroup.recipy.ingredient;

import com.avocadogroup.recipy.ingredient.dtos.IngredientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    @Mapping(target = "recipeId", source = "recipe.id")
    IngredientDto toDto(Ingredient ingredient);

}
