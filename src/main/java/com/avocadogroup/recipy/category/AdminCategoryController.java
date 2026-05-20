package com.avocadogroup.recipy.category;

import com.avocadogroup.recipy.category.dtos.CategoryDto;
import com.avocadogroup.recipy.category.dtos.CreateCategoryRequest;
import com.avocadogroup.recipy.category.dtos.UpdateCategoryRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    private CategoryService categoryService;

    /**
     * Endpoint to create a new category.
     *
     * @param request    the validated {@link CreateCategoryRequest} body containing the new category name
     * @param uriBuilder a builder component to automatically construct the location URI
     * @return a {@link ResponseEntity} containing the created {@link CategoryDto} and the location URI
     */
    @PostMapping
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        // Delegate entity creation to the service layer
        var categoryDto = categoryService.createCategory(request);

        // Build URI for the newly created resource (REST best practice)
        var uri = uriBuilder
                .path("/api/category/{id}")
                .buildAndExpand(categoryDto.getId())
                .toUri();

        // Return HTTP 201 Created with Location header and created entity as payload
        return ResponseEntity.created(uri).body(categoryDto);
    }

    /**
     * Endpoint to retrieve a single category by its ID.
     *
     * @param categoryId the unique ID of the category to fetch from the URL path
     * @return a {@link ResponseEntity} containing the matching {@link CategoryDto} with an HTTP 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable("id") Long categoryId) {
        // Retrieves the category
        var categoryDto = categoryService.getCategoryById(categoryId);

        // Return the entity details with OK response
        return ResponseEntity.ok(categoryDto);
    }

    /**
     * Endpoint to soft-delete a category by its ID.
     *
     * @param categoryId the unique ID of the category to delete from the URL path
     * @return a {@link ResponseEntity} with an HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long categoryId) {
        // Try to delete the entity
        categoryService.deleteCategory(categoryId);

        // Return HTTP 204 no content response
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to update an existing category's details.
     *
     * @param categoryId the unique ID of the category to update from the URL path
     * @param request    the validated {@link UpdateCategoryRequest} body containing the new updates
     * @return a {@link ResponseEntity} containing the updated {@link CategoryDto} with an HTTP 200 OK status
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @Valid @RequestBody UpdateCategoryRequest request,
            @PathVariable("id") Long categoryId) {
        // Try to update the entity
        var categoryDto = categoryService.updateCategory(categoryId, request);

        // Return updated user with HTTP 200
        return ResponseEntity.ok(categoryDto);
    }
}
