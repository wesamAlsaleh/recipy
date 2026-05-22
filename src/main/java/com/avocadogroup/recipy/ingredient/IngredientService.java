package com.avocadogroup.recipy.ingredient;

import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.ingredient.dtos.CreateIngredientRequest;
import com.avocadogroup.recipy.ingredient.dtos.IngredientDto;
import com.avocadogroup.recipy.ingredient.dtos.UpdateIngredientRequest;
import com.avocadogroup.recipy.recipe.Recipe;
import com.avocadogroup.recipy.recipe.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;
    private final RecipeService recipeService;

    /**
     * Fetches an ingredient by its ID or throws a ResourceNotFoundException.
     *
     * @param ingredientId the ID of the ingredient to fetch
     * @return the matching Ingredient entity
     * @throws ResourceNotFoundException if no ingredient is found with the given ID
     */
    private Ingredient fetchIngredient(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found"));
    }

    /**
     * Fetches a recipe by its ID via the RecipeService layer.
     *
     * @param recipeId the ID of the recipe to fetch
     * @return the matching Recipe entity
     * @throws ResourceNotFoundException if no recipe is found with the given ID
     */
    private Recipe fetchRecipe(Long recipeId) {
        return recipeService.getRecipeEntity(recipeId);
    }

    /**
     * Converts an Ingredient entity to its DTO representation.
     *
     * @param ingredient the Ingredient entity to convert
     * @return the mapped IngredientDto
     */
    private IngredientDto toDto(Ingredient ingredient) {
        return ingredientMapper.toDto(ingredient);
    }

    /**
     * Creates a new ingredient with the given details and returns its DTO representation.
     *
     * @param request the validated create request containing ingredient details and recipe ID
     * @return the saved IngredientDto
     * @throws ResourceNotFoundException if the specified recipe does not exist
     */
    public IngredientDto createIngredient(CreateIngredientRequest request) {
        // Fetch the recipe or throw if not found
        var recipe = fetchRecipe(request.getRecipeId());

        // Create and populate the new ingredient entity
        var ingredient = new Ingredient();
        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setNote(request.getNote());
        ingredient.setRecipe(recipe);

        // Return the DTO representation
        return toDto(ingredientRepository.save(ingredient));
    }

    /**
     * Retrieves a single ingredient by its ID.
     *
     * @param ingredientId the unique identifier of the ingredient
     * @return the IngredientDto for the matching ingredient
     * @throws ResourceNotFoundException if no ingredient is found with the given ID
     */
    public IngredientDto readIngredient(Long ingredientId) {
        // Fetch the entity from the db
        var ingredient = fetchIngredient(ingredientId);

        // Return the DTO version of the entity
        return toDto(ingredient);
    }

    /**
     * Updates an existing ingredient's fields and returns the updated DTO.
     * Only the fields that are provided in the request will be updated.
     *
     * @param ingredientId the unique identifier of the ingredient to update
     * @param request      the update request containing the fields to update
     * @return the updated IngredientDto
     * @throws ResourceNotFoundException if no ingredient or referenced recipe is found
     */
    public IngredientDto updateIngredient(Long ingredientId, UpdateIngredientRequest request) {
        // Fetch the existing entity
        var ingredient = fetchIngredient(ingredientId);

        // Update fields only if provided
        if (request.getName() != null)
            ingredient.setName(request.getName());
        if (request.getQuantity() != null)
            ingredient.setQuantity(request.getQuantity());
        if (request.getNote() != null)
            ingredient.setNote(request.getNote());
        if (request.getRecipeId() != null) {
            // Fetch the new recipe
            var recipe = fetchRecipe(request.getRecipeId());
            ingredient.setRecipe(recipe);
        }

        // Persist changes and return the updated DTO
        return toDto(ingredientRepository.save(ingredient));
    }

    /**
     * Deletes an ingredient by its ID.
     *
     * @param ingredientId the unique identifier of the ingredient to delete
     * @throws ResourceNotFoundException if no ingredient is found with the given ID
     */
    public void deleteIngredient(Long ingredientId) {
        // Fetch the entity to ensure it exists
        var ingredient = fetchIngredient(ingredientId);

        // Remove the ingredient from the database
        ingredientRepository.delete(ingredient);
    }

}
