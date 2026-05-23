package com.avocadogroup.recipy.recipe;

import com.avocadogroup.recipy.recipe.dtos.RecipeDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/recipes")
public class RecipeAdminController {
    private final RecipeService recipeService;

    /**
     * Retrieves a paginated list of all recipes (including soft-deleted ones).
     *
     * @param page the zero-based page number to retrieve (default 0)
     * @param size the number of recipes per page (default 10)
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link RecipeDto} with HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Delegate to the service layer to fetch paginated recipes
        var recipes = recipeService.getAllRecipes(page, size);

        // Return the paginated result with HTTP 200 OK status
        return ResponseEntity.ok(recipes);
    }
}
