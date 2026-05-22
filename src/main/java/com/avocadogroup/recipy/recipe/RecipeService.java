package com.avocadogroup.recipy.recipe;

import com.avocadogroup.recipy.authentication.services.AuthenticationService;
import com.avocadogroup.recipy.category.CategoryService;
import com.avocadogroup.recipy.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.recipy.recipe.dtos.CreateRecipeRequest;
import com.avocadogroup.recipy.recipe.dtos.RecipeDto;
import com.avocadogroup.recipy.recipe.dtos.UpdateRecipeRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final CategoryService categoryService;
    private final AuthenticationService authenticationService;

    /**
     * Fetches a recipe by its ID.
     *
     * @param recipeId the ID of the recipe to find
     * @return the matching {@link Recipe} entity
     * @throws ResourceNotFoundException if no recipe is found with the given ID
     */
    private Recipe fetchRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
    }

    /**
     * Fetches a recipe by its ID and returns the entity.
     * Exposed as package-private for use by other services (e.g. IngredientService).
     *
     * @param recipeId the ID of the recipe to find
     * @return the matching {@link Recipe} entity
     * @throws ResourceNotFoundException if no recipe is found with the given ID
     */
    public Recipe getRecipeEntity(Long recipeId) {
        return fetchRecipe(recipeId);
    }

    /**
     * Converts a Recipe entity into its corresponding Data Transfer Object.
     *
     * @param recipe the {@link Recipe} entity to convert
     * @return the mapped {@link RecipeDto}, or {@code null} if the input is null
     */
    private RecipeDto toDto(Recipe recipe) {
        return recipeMapper.toDto(recipe);
    }

    /**
     * Creates a new recipe with the associated category and returns its DTO representation.
     *
     * @param request the {@link CreateRecipeRequest} containing the details and category ID for the new recipe
     * @return the saved {@link RecipeDto} containing the generated database ID and metadata
     * @throws ResourceNotFoundException if the specified category ID in the request does not exist
     */
    public RecipeDto createRecipe(CreateRecipeRequest request) {
        // Create entity
        var recipe = new Recipe();

        // Fetch the category or throw resource not found exception
        var category = categoryService.getCategoryEntity(request.getCategoryId());

        // Fetch the authenticated user details or throw resource not found exception
        var user = authenticationService.getAuthenticatedUser();

        // Set the metadata
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setCookingTime(request.getCookingTime());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setImageUrl(null);
        recipe.setCategory(category);
        recipe.setUser(user);

        // Save the changes and return the DTO version
        return toDto(recipeRepository.save(recipe));
    }

    /**
     * Retrieves a single recipe by its ID and returns its DTO representation.
     *
     * @param recipeId the unique identifier of the recipe to retrieve
     * @return the mapped {@link RecipeDto} representing the recipe
     * @throws ResourceNotFoundException if no recipe matches the provided ID
     */
    public RecipeDto readRecipe(Long recipeId) {
        // Fetch the entity from the db
        var recipe = fetchRecipe(recipeId);

        // Return the DTO version of the entity
        return toDto(recipe);
    }

    /**
     * Updates an existing recipe's metadata and returns its updated DTO.
     *
     * @param recipeId the unique identifier of the recipe to update
     * @param request  the {@link UpdateRecipeRequest} containing the updated recipe details
     * @return the updated {@link RecipeDto} saved in the database
     * @throws ResourceNotFoundException if no recipe matches the provided ID
     */
    public RecipeDto updateRecipe(Long recipeId, UpdateRecipeRequest request) {
        // Fetch the entity from the db
        var recipe = fetchRecipe(recipeId);

        // Update fields only if provided
        if (request.getName() != null)
            recipe.setName(request.getName());
        if (request.getDescription() != null)
            recipe.setDescription(request.getDescription());
        if (request.getInstructions() != null)
            recipe.setInstructions(request.getInstructions());
        if (request.getCookingTime() != null)
            recipe.setCookingTime(request.getCookingTime());
        if (request.getDifficulty() != null)
            recipe.setDifficulty(request.getDifficulty());
        if (request.getImageUrl() != null)
            recipe.setImageUrl(request.getImageUrl());
        if (request.getCategoryId() != null) {
            // Fetch the new category
            var category = categoryService.getCategoryEntity(request.getCategoryId());

            // Set the new category
            recipe.setCategory(category);
        }

        // Return the DTO of the updated entity
        return toDto(recipeRepository.save(recipe));
    }

    /**
     * Soft-deletes a recipe by its ID and returns its updated DTO.
     *
     * @param recipeId the unique identifier of the recipe to delete
     * @return the updated {@link RecipeDto} with its deletion state applied
     * @throws ResourceNotFoundException if no recipe matches the provided ID
     */
    public RecipeDto deleteRecipe(Long recipeId) {
        // Fetch the entity
        var recipe = fetchRecipe(recipeId);

        // If the entity is deleted do nothing
        if (recipe.getDeleted()) {
            return toDto(recipe);
        }

        // Mark as deleted
        recipe.softDelete();

        // Return the DTO version of the soft deleted entity
        return toDto(recipeRepository.save(recipe));
    }

    /**
     * Restores a soft-deleted recipe by its ID and returns its updated DTO.
     *
     * @param recipeId the unique identifier of the recipe to restore
     * @return the updated {@link RecipeDto} with its active state reapplied
     * @throws ResourceNotFoundException if no recipe matches the provided ID
     */
    public RecipeDto restoreRecipe(Long recipeId) {
        // Fetch the entity
        var recipe = fetchRecipe(recipeId);

        // If the entity is not soft deleted (already active), do nothing
        if (!recipe.getDeleted()) {
            return toDto(recipe);
        }

        // Restore the soft deleted entity
        recipe.restore();

        // Return the DTO version of the updated entity
        return toDto(recipeRepository.save(recipe));
    }
}
