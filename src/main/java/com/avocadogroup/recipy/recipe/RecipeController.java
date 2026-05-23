package com.avocadogroup.recipy.recipe;

import com.avocadogroup.recipy.recipe.dtos.CreateRecipeRequest;
import com.avocadogroup.recipy.recipe.dtos.RecipeDto;
import com.avocadogroup.recipy.recipe.dtos.UpdateRecipeRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    /**
     * Endpoint to create a new recipe.
     *
     * @param request    the validated {@link CreateRecipeRequest} body containing the new recipe details
     * @param uriBuilder a builder component to automatically construct the location URI
     * @return a {@link ResponseEntity} containing the created {@link RecipeDto} and the location URI
     */
    @PostMapping
    public ResponseEntity<?> createRecipe(
            @Valid @ModelAttribute CreateRecipeRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        // Delegate entity creation to the service layer
        var recipeDto = recipeService.createRecipe(request);

        // Build URI for the newly created resource (REST best practice)
        var uri = uriBuilder
                .path("/api/recipes/{id}")
                .buildAndExpand(recipeDto.getId())
                .toUri();

        // Return HTTP 201 Created with Location header and created entity as payload
        return ResponseEntity.created(uri).body(recipeDto);
    }

    /**
     * Endpoint to retrieve a single recipe by its ID.
     *
     * @param recipeId the unique ID of the recipe to fetch from the URL path
     * @return a {@link ResponseEntity} containing the matching {@link RecipeDto} with HTTP 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipe(@PathVariable("id") Long recipeId) {
        // Retrieve the recipe by ID from the service layer
        var recipeDto = recipeService.readRecipe(recipeId);

        // Return the entity details with OK response
        return ResponseEntity.ok(recipeDto);
    }

    /**
     * Endpoint to update an existing recipe's metadata by its ID.
     *
     * @param recipeId the unique ID of the recipe to update from the URL path
     * @param request  the validated {@link UpdateRecipeRequest} body containing the updated fields
     * @return a {@link ResponseEntity} containing the updated {@link RecipeDto} with HTTP 200 OK
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRecipe(
            @PathVariable("id") Long recipeId,
            @Valid @ModelAttribute UpdateRecipeRequest request
    ) {
        // Delegate the update to the service layer
        var recipeDto = recipeService.updateRecipe(recipeId, request);

        // Return the updated entity with OK response
        return ResponseEntity.ok(recipeDto);
    }

    /**
     * Endpoint to soft-delete a recipe by its ID.
     *
     * @param recipeId the unique ID of the recipe to delete from the URL path
     * @return a {@link ResponseEntity} with HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable("id") Long recipeId) {
        // Delegate the soft delete to the service layer
        recipeService.deleteRecipe(recipeId);

        // Return HTTP 204 No Content response
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to restore a soft-deleted recipe by its ID.
     *
     * @param recipeId the unique ID of the recipe to restore from the URL path
     * @return a {@link ResponseEntity} containing the restored {@link RecipeDto} with HTTP 200 OK
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreRecipe(@PathVariable("id") Long recipeId) {
        // Delegate the restore to the service layer
        var recipeDto = recipeService.restoreRecipe(recipeId);

        // Return the restored entity with OK response
        return ResponseEntity.ok(recipeDto);
    }

    // TODO: list recipes with pagination/filtering (by category, difficulty, etc.)
}
