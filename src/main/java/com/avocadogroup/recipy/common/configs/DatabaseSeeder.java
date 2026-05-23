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
        var asian = saveCategory("Asian");
        var american = saveCategory("American");
        var middleEastern = saveCategory("Middle Eastern");

        // Create recipes with their ingredients
        var carbonara = saveRecipe(
                "Spaghetti Carbonara",
                "Classic Roman pasta",
                "Boil spaghetti in salted water until al dente. Fry pancetta until crispy. Whisk eggs with grated parmesan and black pepper. Drain pasta and mix with pancetta off heat. Add egg mixture and toss quickly to coat without scrambling. Serve immediately.",
                30,
                "Medium",
                italian,
                admin,
                List.of(
                        saveIngredient("Spaghetti", "200g", null),
                        saveIngredient("Eggs", "4", null),
                        saveIngredient("Pancetta", "150g", "diced"),
                        saveIngredient("Parmesan", "50g", "grated"),
                        saveIngredient("Black pepper", "1 tsp", "freshly ground")
                )
        );

        var margheritaPizza = saveRecipe(
                "Margherita Pizza",
                "Classic Neapolitan pizza",
                "Preheat oven to 250C. Stretch pizza dough on a floured surface. Spread tomato sauce evenly. Top with mozzarella slices. Bake for 10-12 minutes until crust is golden. Remove from oven and top with fresh basil leaves and a drizzle of olive oil.",
                45,
                "Medium",
                italian,
                admin,
                List.of(
                        saveIngredient("Pizza dough", "300g", null),
                        saveIngredient("Tomato sauce", "150ml", null),
                        saveIngredient("Mozzarella", "200g", "sliced"),
                        saveIngredient("Fresh basil", "10 leaves", null),
                        saveIngredient("Olive oil", "2 tbsp", null)
                )
        );

        var risotto = saveRecipe(
                "Mushroom Risotto",
                "Creamy Italian rice dish",
                "Saute onion in butter until soft. Add arborio rice and toast for 2 minutes. Pour in white wine and stir until absorbed. Add warm broth one ladle at a time, stirring constantly until absorbed before adding more. After 18 minutes, stir in mushrooms, parmesan, and remaining butter. Season and serve.",
                40,
                "Hard",
                italian,
                admin,
                List.of(
                        saveIngredient("Arborio rice", "300g", null),
                        saveIngredient("Mushrooms", "250g", "sliced"),
                        saveIngredient("Parmesan", "80g", "grated"),
                        saveIngredient("White wine", "100ml", null),
                        saveIngredient("Vegetable broth", "1L", "warm"),
                        saveIngredient("Butter", "30g", null),
                        saveIngredient("Onion", "1", "finely chopped")
                )
        );

        var chocoCake = saveRecipe(
                "Chocolate Cake",
                "Rich and moist",
                "Preheat oven to 180C. Mix flour, sugar, and cocoa powder in a bowl. Beat eggs and melted butter separately then combine with dry ingredients. Pour into a greased cake tin. Bake for 35-40 minutes until a skewer comes out clean. Cool before serving.",
                60,
                "Hard",
                dessert,
                admin,
                List.of(
                        saveIngredient("Flour", "200g", null),
                        saveIngredient("Sugar", "150g", null),
                        saveIngredient("Cocoa powder", "50g", null),
                        saveIngredient("Eggs", "3", null),
                        saveIngredient("Butter", "100g", "softened")
                )
        );

        var tiramisu = saveRecipe(
                "Tiramisu",
                "Classic Italian coffee dessert",
                "Separate eggs and beat yolks with sugar until pale. Fold in mascarpone. Whisk egg whites to stiff peaks and fold into mascarpone mixture. Dip ladyfingers briefly in cooled espresso and layer in a dish. Spread mascarpone cream on top. Repeat layers. Refrigerate for at least 4 hours. Dust with cocoa powder before serving.",
                30,
                "Medium",
                dessert,
                admin,
                List.of(
                        saveIngredient("Ladyfinger biscuits", "200g", null),
                        saveIngredient("Mascarpone", "250g", null),
                        saveIngredient("Eggs", "3", "separated"),
                        saveIngredient("Sugar", "80g", null),
                        saveIngredient("Espresso", "200ml", "cooled"),
                        saveIngredient("Cocoa powder", "2 tbsp", "for dusting")
                )
        );

        var pancakes = saveRecipe(
                "Fluffy Pancakes",
                "Light and airy breakfast pancakes",
                "Mix flour, sugar, and baking powder in a bowl. Whisk milk, eggs, and melted butter in a separate bowl. Combine wet and dry ingredients until just mixed, do not over-mix. Heat a non-stick pan over medium heat. Pour a ladleful of batter and cook until bubbles form on surface. Flip and cook for another minute. Serve with syrup.",
                20,
                "Easy",
                dessert,
                admin,
                List.of(
                        saveIngredient("Flour", "200g", null),
                        saveIngredient("Milk", "300ml", null),
                        saveIngredient("Eggs", "2", null),
                        saveIngredient("Baking powder", "2 tsp", null),
                        saveIngredient("Sugar", "2 tbsp", null),
                        saveIngredient("Butter", "30g", "melted")
                )
        );

        var chickenTeriyaki = saveRecipe(
                "Chicken Teriyaki",
                "Japanese glazed chicken",
                "Mix soy sauce, mirin, sugar, garlic, and ginger in a bowl. Marinate chicken thighs for 30 minutes. Heat a pan over medium-high heat and cook chicken for 5-6 minutes per side. Pour remaining marinade into the pan and simmer until it thickens into a glaze. Slice and serve over steamed rice.",
                25,
                "Easy",
                asian,
                admin,
                List.of(
                        saveIngredient("Chicken thighs", "500g", "boneless"),
                        saveIngredient("Soy sauce", "4 tbsp", null),
                        saveIngredient("Mirin", "2 tbsp", null),
                        saveIngredient("Sugar", "1 tbsp", null),
                        saveIngredient("Garlic", "2 cloves", "minced"),
                        saveIngredient("Ginger", "1 tsp", "grated")
                )
        );

        var friedRice = saveRecipe(
                "Egg Fried Rice",
                "Quick and savory Chinese fried rice",
                "Heat oil in a wok over high heat. Add beaten eggs and scramble until just set, then push to the side. Add day-old rice and stir-fry for 3 minutes breaking up any clumps. Add peas and spring onions. Pour soy sauce over everything and toss well. Finish with a drizzle of sesame oil and serve immediately.",
                15,
                "Easy",
                asian,
                admin,
                List.of(
                        saveIngredient("Cooked rice", "400g", "day-old"),
                        saveIngredient("Eggs", "3", "beaten"),
                        saveIngredient("Soy sauce", "3 tbsp", null),
                        saveIngredient("Spring onions", "4", "chopped"),
                        saveIngredient("Frozen peas", "100g", null),
                        saveIngredient("Sesame oil", "1 tbsp", null)
                )
        );

        var beefPho = saveRecipe(
                "Beef Pho",
                "Vietnamese aromatic noodle soup",
                "Roast bones at 220C for 30 minutes. Transfer to a pot with star anise and cinnamon stick. Cover with water and simmer for 2 hours. Strain broth and season with fish sauce. Soak rice noodles in hot water until tender. Divide noodles into bowls, top with thinly sliced raw beef, and pour boiling broth over to cook the beef. Garnish with bean sprouts and fresh basil.",
                120,
                "Hard",
                asian,
                admin,
                List.of(
                        saveIngredient("Beef bones", "1kg", null),
                        saveIngredient("Rice noodles", "300g", null),
                        saveIngredient("Beef sirloin", "300g", "thinly sliced"),
                        saveIngredient("Star anise", "4", null),
                        saveIngredient("Cinnamon stick", "1", null),
                        saveIngredient("Fish sauce", "3 tbsp", null),
                        saveIngredient("Bean sprouts", "100g", null),
                        saveIngredient("Fresh basil", "handful", null)
                )
        );

        var burgers = saveRecipe(
                "Classic Beef Burger",
                "Juicy homemade beef burger",
                "Divide ground beef into 4 patties and season with salt and pepper. Heat a grill or pan over high heat. Cook patties for 3-4 minutes per side for medium. Add cheese slice in the last minute. Toast buns lightly. Assemble with lettuce, tomato, and onion. Serve immediately.",
                20,
                "Easy",
                american,
                admin,
                List.of(
                        saveIngredient("Ground beef", "500g", "80/20 fat ratio"),
                        saveIngredient("Burger buns", "4", null),
                        saveIngredient("Cheddar cheese", "4 slices", null),
                        saveIngredient("Lettuce", "4 leaves", null),
                        saveIngredient("Tomato", "1", "sliced"),
                        saveIngredient("Red onion", "1", "sliced")
                )
        );

        var bbqRibs = saveRecipe(
                "BBQ Baby Back Ribs",
                "Slow cooked smoky ribs",
                "Mix brown sugar, paprika, garlic powder, and onion powder to make a dry rub. Coat ribs generously and refrigerate for 2 hours. Wrap in foil and bake at 150C for 2.5 hours. Unwrap, brush with BBQ sauce, and grill on high heat for 10 minutes until caramelized. Rest for 5 minutes before cutting.",
                180,
                "Hard",
                american,
                admin,
                List.of(
                        saveIngredient("Baby back ribs", "1.5kg", null),
                        saveIngredient("BBQ sauce", "200ml", null),
                        saveIngredient("Brown sugar", "2 tbsp", null),
                        saveIngredient("Smoked paprika", "1 tbsp", null),
                        saveIngredient("Garlic powder", "1 tsp", null),
                        saveIngredient("Onion powder", "1 tsp", null)
                )
        );

        var hummus = saveRecipe(
                "Classic Hummus",
                "Creamy Middle Eastern chickpea dip",
                "Drain and rinse chickpeas, reserving some liquid. Blend chickpeas, tahini, lemon juice, garlic, and cumin in a food processor until smooth. Add reserved chickpea liquid gradually until desired consistency is reached. Season with salt. Transfer to a bowl, drizzle with olive oil, and sprinkle with paprika.",
                15,
                "Easy",
                middleEastern,
                admin,
                List.of(
                        saveIngredient("Chickpeas", "400g", "canned, drained"),
                        saveIngredient("Tahini", "3 tbsp", null),
                        saveIngredient("Lemon juice", "2 tbsp", null),
                        saveIngredient("Garlic", "2 cloves", null),
                        saveIngredient("Olive oil", "2 tbsp", null),
                        saveIngredient("Cumin", "0.5 tsp", null)
                )
        );

        var shawarma = saveRecipe(
                "Chicken Shawarma",
                "Spiced Middle Eastern grilled chicken wrap",
                "Mix cumin, turmeric, cinnamon, garlic, lemon juice, and yogurt to make a marinade. Coat chicken thighs and marinate for at least 2 hours. Grill or pan fry over medium-high heat for 6-7 minutes per side until cooked through. Slice thinly. Warm pita bread, fill with chicken slices, and serve with extra yogurt sauce.",
                35,
                "Medium",
                middleEastern,
                admin,
                List.of(
                        saveIngredient("Chicken thighs", "600g", "boneless"),
                        saveIngredient("Pita bread", "4", null),
                        saveIngredient("Yogurt", "100ml", null),
                        saveIngredient("Cumin", "1 tsp", null),
                        saveIngredient("Turmeric", "0.5 tsp", null),
                        saveIngredient("Cinnamon", "0.5 tsp", null),
                        saveIngredient("Garlic", "3 cloves", "minced"),
                        saveIngredient("Lemon juice", "2 tbsp", null)
                )
        );
    }
}
