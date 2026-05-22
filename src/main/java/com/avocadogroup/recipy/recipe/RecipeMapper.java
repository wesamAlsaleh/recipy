package com.avocadogroup.recipy.recipe;

import com.avocadogroup.recipy.category.CategoryMapper;
import com.avocadogroup.recipy.ingredient.IngredientMapper;
import com.avocadogroup.recipy.recipe.dtos.RecipeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, IngredientMapper.class})
public interface RecipeMapper {
    @Mapping(source = "category", target = "category")
    @Mapping(source = "ingredients", target = "ingredients")
    RecipeDto toDto(Recipe recipe);
}
