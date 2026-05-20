package com.avocadogroup.recipy.category.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCategoryResponse {
    public CategoryDto category;
}
