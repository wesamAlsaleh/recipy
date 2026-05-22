package com.avocadogroup.recipy.category.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateCategoryRequest {
    @NotNull(message = "Category name is required")
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 255)
    private String name;
}
