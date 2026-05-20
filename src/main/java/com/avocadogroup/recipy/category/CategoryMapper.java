package com.avocadogroup.recipy.category;

import com.avocadogroup.recipy.category.dtos.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
}
