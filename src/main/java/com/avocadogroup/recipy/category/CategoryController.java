package com.avocadogroup.recipy.category;

import com.avocadogroup.recipy.category.dtos.CategoryDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Endpoint to retrieve all active and available categories.
     *
     * @return a {@link ResponseEntity} containing a {@link List} of {@link CategoryDto} objects
     */
    @GetMapping
    public ResponseEntity<?> getAllAvailableCategories() {
        // Delegate the fetching to the service layer
        var categories = categoryService.getAllAvailableCategories();

        // Return the categories with 200 HTTP response
        return ResponseEntity.ok(categories);
    }
}
