package Recipe;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

/**
 * A final class that stores the currently added recipes within the game.
 * RecipeManager can be referenced from any other script.
 */
public class RecipeManager {

    /* Stores a reference to all added recipes */
    private static Recipe[] recipes = {
            new BurgerRecipe(),
            new SaladRecipe(),
            new UncookedPizzaRecipe(),
            new CookedPizzaRecipe(),
            new JacketPotatoRecipe()
    };

    /* Stores the textures for each 'complete' recipe (recipes withoout further preparation steps */
    private static Texture[] recipeTextures = {
            new Texture("Food/burger_recipe.png"),
            new Texture("Food/salad_recipe.png"),
            new Texture("Food/pizza_recipe.png"),
            new Texture("Food/jacket_potato_recipe.png")
    };

    private static Recipe[] completeRecipes;

    /** Initialises the RecipeManager */
    public static void initialise()
    {
        // Find and assign complete recipes from the recipes list
        // A complete recipe is one that doesn't require any further preparation steps.
        ArrayList<Recipe> completeRecipesList = new ArrayList<>();
        for(Recipe r : recipes)
        {
            if(r.getIngredientOverride() == null)
            {
                completeRecipesList.add(r);
            }
        }
        completeRecipes = completeRecipesList.toArray(new Recipe[completeRecipesList.size()]);
    }


    /**
     * Returns the complete Recipe object at the given index
     *
     * @param index the index of the recipe to get
     * @return the Recipe at the given index
     */
    public static Recipe getCompleteRecipeAt(int index) { return completeRecipes[index]; }


    /**
     * Returns the Texture of the Recipe at the given index
     * @param index the index of the recipe texture to get
     * @return the Texture at the given index
     */
    public static Texture getRecipeTextureAt(int index) { return recipeTextures[index]; }

    /**
     * Gets all the recipes implemented in the game
     * @return an array of Recipe classes.
     */
    public static Recipe[] getRecipes() { return recipes; }

    /**
     * Gets only the recipes that are completed dishes in the game
     */
    public static Recipe[] getCompleteRecipes() { return completeRecipes; }

}