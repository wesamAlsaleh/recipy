package com.avocadogroup.recipy.ingredient;

import com.avocadogroup.recipy.ingredient.dtos.CreateIngredientRequest;
import com.avocadogroup.recipy.ingredient.dtos.UpdateIngredientRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    /**
     * Endpoint to creates a new ingredient and returns the created resource with its location URI.
     *
     * @param request    the validated create request body
     * @param uriBuilder the URI builder for constructing the Location header
     * @return HTTP 201 Created with the created IngredientDto and Location header
     */
    @PostMapping
    public ResponseEntity<?> createIngredient(
            @Valid @RequestBody CreateIngredientRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        // Delegate entity creation to the service layer
        var ingredientDto = ingredientService.createIngredient(request);

        // Build URI for the newly created resource (REST best practice)
        var uri = uriBuilder
                .path("/api/ingredients/{id}")
                .buildAndExpand(ingredientDto.getId())
                .toUri();

        // Return HTTP 201 Created with Location header and created entity as payload
        return ResponseEntity.created(uri).body(ingredientDto);
    }

    /**
     * Endpoint to retrieves a single ingredient by its ID.
     *
     * @param ingredientId the unique ID of the ingredient to fetch from the URL path
     * @return HTTP 200 OK with the matching IngredientDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredient(@PathVariable("id") Long ingredientId) {
        // Retrieve the ingredient by ID from the service layer
        var ingredientDto = ingredientService.readIngredient(ingredientId);

        // Return the entity details with OK response
        return ResponseEntity.ok(ingredientDto);
    }

    /**
     * Endpoint to updates an existing ingredient by its ID.
     * Only the fields provided in the request body will be updated.
     *
     * @param ingredientId the unique ID of the ingredient to update from the URL path
     * @param request      the validated update request body
     * @return HTTP 200 OK with the updated IngredientDto
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateIngredient(
            @PathVariable("id") Long ingredientId,
            @Valid @RequestBody UpdateIngredientRequest request
    ) {
        // Delegate the update to the service layer
        var ingredientDto = ingredientService.updateIngredient(ingredientId, request);

        // Return the updated entity with OK response
        return ResponseEntity.ok(ingredientDto);
    }

    /**
     * Endpoint to deletes an ingredient by its ID.
     *
     * @param ingredientId the unique ID of the ingredient to delete from the URL path
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIngredient(@PathVariable("id") Long ingredientId) {
        // Delegate the deletion to the service layer
        ingredientService.deleteIngredient(ingredientId);

        // Return HTTP 204 No Content response
        return ResponseEntity.noContent().build();
    }

}
