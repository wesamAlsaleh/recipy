package com.avocadogroup.recipy.recipe;

import com.avocadogroup.recipy.category.CategoryMapper;
import com.avocadogroup.recipy.recipe.dtos.RecipeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface RecipeMapper {
    @Mapping(source = "category", target = "category") // Map Category entity to CategoryDto via CategoryMapper
    RecipeDto toDto(Recipe recipe);
}
