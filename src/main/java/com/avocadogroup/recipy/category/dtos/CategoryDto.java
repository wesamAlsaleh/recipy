package com.avocadogroup.recipy.category.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private Boolean deleted;
    private Instant createdAt;
    private Instant updatedAt;
}
