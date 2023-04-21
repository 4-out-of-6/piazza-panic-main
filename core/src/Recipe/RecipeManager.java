package Recipe;

import com.badlogic.gdx.graphics.Texture;

/**
 * A final class that stores the currently added recipes within the game.
 * RecipeManager can be referenced from any other script.
 */
public class RecipeManager {

    /* Stores a reference to all added recipes */
    private static Recipe[] recipes = {
            new BurgerRecipe(),
            new SaladRecipe()
    };

    /* Stores the textures for each recipe */
    private static Texture[] recipeTextures = {
            new Texture("Food/burger_recipe.png"),
            new Texture("Food/salad_recipe.png")
    };


    /**
     * Returns the Recipe object at the given index
     *
     * @param index the index of the recipe to get
     * @return the Recipe at the given index
     */
    public static Recipe getRecipeAt(int index) { return recipes[index]; }


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

}
