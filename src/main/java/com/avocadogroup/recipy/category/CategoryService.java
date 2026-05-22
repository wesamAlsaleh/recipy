package com.avocadogroup.recipy.category;

import com.avocadogroup.recipy.category.dtos.CategoryDto;
import com.avocadogroup.recipy.category.dtos.CreateCategoryRequest;
import com.avocadogroup.recipy.category.dtos.UpdateCategoryRequest;
import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Fetches a category by its ID.
     *
     * @param categoryId the ID of the category to find
     * @return the matching {@link Category} entity
     * @throws ResourceNotFoundException if the category does not exist in the database
     */
    private Category fetchCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    /**
     * Converts a Category entity into its corresponding Data Transfer Object.
     *
     * @param category the {@link Category} entity to convert
     * @return the mapped {@link CategoryDto}, or {@code null} if the input is null
     */
    private CategoryDto toDto(Category category) {
        return categoryMapper.toDto(category);
    }

    /**
     * Retrieves a category by its ID and converts it to a DTO.
     *
     * @param categoryId the unique identifier of the category to retrieve
     * @return the mapped {@link CategoryDto} representing the category metadata
     * @throws ResourceNotFoundException if the category does not exist in the database
     */
    public CategoryDto readCategory(Long categoryId) {
        // Fetch the category by id from the db
        var category = fetchCategory(categoryId);

        // Return the DTO version of the category
        return toDto(category);
    }

    /**
     * Retrieves a category entity by its ID.
     *
     * @param categoryId the unique identifier of the category to retrieve
     * @return the mapped {@link Category} representing the category entity
     * @throws ResourceNotFoundException if the category does not exist in the database
     */
    public Category getCategoryEntity(Long categoryId) {
        // Fetch the category by id from the db
        var category = fetchCategory(categoryId);

        // if the category deleted throw not found exception to client
        if (Boolean.TRUE.equals(category.getDeleted())) {
            throw new ResourceNotFoundException("Category not found");
        }

        return category;
    }

    /**
     * Creates a new category and returns its DTO representation.
     *
     * @param request the {@link CreateCategoryRequest} containing the details of the new category
     * @return the {@link CategoryDto} containing the category metadata
     */
    public CategoryDto createCategory(CreateCategoryRequest request) {
        // Create entity
        Category category = new Category();

        // Set the metadata
        category.setName(request.getName());

        // Save the changes and return the DTO version
        return toDto(categoryRepository.save(category));
    }

    /**
     * Performs a soft delete on a category by its ID and returns its updated DTO.
     *
     * @param categoryId the unique identifier of the category to be soft deleted
     * @return the updated {@link CategoryDto} with its deletion state applied
     * @throws ResourceNotFoundException if no category matches the provided ID
     */
    public CategoryDto deleteCategory(Long categoryId) {
        // Fetch the entity to delete
        Category category = fetchCategory(categoryId);

        // If the entity is deleted do nothing
        if (category.getDeleted()) {
            return toDto(category);
        }

        // Mark as deleted
        category.softDelete();

        // Return the DTO version of the soft deleted entity
        return toDto(categoryRepository.save(category));
    }


    /**
     * Restores a soft-deleted category by its ID and returns its updated DTO.
     *
     * @param categoryId the unique identifier of the category to be restored
     * @return the updated {@link CategoryDto} with its active state reapplied
     * @throws ResourceNotFoundException if no category matches the provided ID
     */
    public CategoryDto restoreCategory(Long categoryId) {
        // Fetch the entity to restore
        Category category = fetchCategory(categoryId);

        // If the entity is not soft deleted do nothing
        if (!category.getDeleted()) {
            return toDto(category);
        }

        // Restore the soft deleted entity
        category.restore();

        // Return the DTO version of the updated entity
        return toDto(categoryRepository.save(category));
    }

    /**
     * Updates an existing category's details and returns its updated DTO.
     *
     * @param categoryId the unique identifier of the category to update
     * @param request    the {@link UpdateCategoryRequest} containing the updated metadata
     * @return the updated {@link CategoryDto} saved in the database
     * @throws ResourceNotFoundException if no category matches the provided ID
     */
    public CategoryDto updateCategory(Long categoryId, UpdateCategoryRequest request) {
        // Fetch the entity from the db
        Category category = fetchCategory(categoryId);

        // Update the entity name
        category.setName(request.getName());

        // Return the DTO of the updated entity
        return toDto(categoryRepository.save(category));
    }


}
