package com.avocadogroup.recipy.common.configs;

import com.avocadogroup.recipy.category.Category;
import com.avocadogroup.recipy.category.CategoryRepository;
import com.avocadogroup.recipy.ingredient.Ingredient;
import com.avocadogroup.recipy.recipe.Recipe;
import com.avocadogroup.recipy.recipe.RecipeRepository;
import com.avocadogroup.recipy.user.User;
import com.avocadogroup.recipy.user.UserRepository;
import com.avocadogroup.recipy.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Finds or creates a category by name.
     *
     * @param categoryName the unique name of the category to find or create
     * @return the existing or newly saved {@link Category} entity
     */
    private Category saveCategory(String categoryName) {
        // Return existing category if found, otherwise create and save a new one
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    var category = new Category();
                    category.setName(categoryName);
                    return categoryRepository.save(category);
                });
    }

    /**
     * Helper method to create a new recipe with the given attributes and ingredients.
     *
     * @param name         the name of the recipe
     * @param description  a short description of the recipe
     * @param instructions the cooking instructions
     * @param cookingTime  the cooking time in minutes
     * @param difficulty   the difficulty level (Easy, Medium, Hard)
     * @param category     the category entity this recipe belongs to
     * @param user         the user entity who created this recipe
     * @param ingredients  the list of ingredient entities to associate
     * @return the persisted Recipe entity
     */
    private Recipe saveRecipe(
            String name,
            String description,
            String instructions,
            Integer cookingTime,
            String difficulty,
            Category category,
            User user,
            List<Ingredient> ingredients
    ) {
        // Create new recipe entity instance
        var recipe = new Recipe();

        // Set basic fields
        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setInstructions(instructions);
        recipe.setCookingTime(cookingTime);
        recipe.setDifficulty(difficulty);
        recipe.setCategory(category);
        recipe.setUser(user);

        // Associate each ingredient with this recipe
        ingredients.forEach(ingredient -> ingredient.setRecipe(recipe)); // Each ingredient belong to recipe

        // Attach the ingredients list to the recipe (cascade persist)
        recipe.setIngredients(ingredients); // recipe has list of ingredients

        // Save recipe and cascade to ingredients, then return
        return recipeRepository.save(recipe);
    }

    /**
     * Helper method to create a new ingredient entity without persisting it.
     * Persistence is handled by the parent Recipe's cascade.
     *
     * @param name     the ingredient name
     * @param quantity the quantity as a string (e.g. "200g", "3")
     * @param note     an optional preparation note (e.g. "diced", "grated")
     * @return a transient Ingredient entity (not yet saved)
     */
    private Ingredient saveIngredient(String name, String quantity, String note) {
        // Create new ingredient entity instance
        var ingredient = new Ingredient();

        // Set the ingredient fields
        ingredient.setName(name);
        ingredient.setQuantity(quantity);
        ingredient.setNote(note);

        // Return without persisting @NOTE the recipe's cascade = ALL will handle it
        return ingredient;
    }

    @Override
    public void run(String... args) throws Exception {
        // Get the user records count
        var usersCount = userRepository.count();

        // If there is data skip seeding
        if (usersCount > 0) {
            return; // seed runs once, skips on restarts
        }

        // Create the admin account
        var admin = new User();
        admin.setEmail("admin@recipy.com");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setRole(UserRole.ADMIN.toString());
        admin.setEmailVerified(true);
        admin.setIsActive(true);
        userRepository.save(admin);

        // Create the user account
        var user = new User();
        user.setEmail("user@recipy.com");
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("User@123"));
        user.setRole(UserRole.USER.toString());
        user.setEmailVerified(true);
        user.setIsActive(true);
        userRepository.save(user);

        // Create categories
        var italian = saveCategory("Italian");
        var dessert = saveCategory("Dessert");
        var vegetarian = saveCategory("Vegetarian");
        var mexican = saveCategory("Mexican");
        var japanese = saveCategory("Japanese");

        // Create recipes with their ingredients
        var carbonara = saveRecipe(
                "Spaghetti Carbonara",
                "Classic Roman pasta",
                "...",
                30,
                "Medium",
                italian, // category
                admin, // created by
                List.of(
                        saveIngredient("Spaghetti", "200g", null),
                        saveIngredient("Eggs", "4", null),
                        saveIngredient("Pancetta", "150g", "diced"),
                        saveIngredient("Parmesan", "50g", "grated"),
                        saveIngredient("Black pepper", "1 tsp", "freshly ground")
                )
        );

        var chocoCake = saveRecipe(
                "Chocolate Cake",
                "Rich and moist",
                "...",
                60,
                "Hard",
                dessert, // category
                admin, // created by
                List.of(
                        saveIngredient("Flour", "200g", null),
                        saveIngredient("Sugar", "150g", null),
                        saveIngredient("Cocoa powder", "50g", null),
                        saveIngredient("Eggs", "3", null),
                        saveIngredient("Butter", "100g", "softened")
                )
        );
    }
}
