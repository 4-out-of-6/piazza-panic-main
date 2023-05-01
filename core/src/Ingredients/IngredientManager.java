package Ingredients;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

/**
 * A final class that stores the currently added recipes within the game.
 * RecipeManager can be referenced from any other script.
 */
public class IngredientManager {

    /* Stores a reference to all added ingredients */
    private static Ingredient[] ingredients = {
            new Bun(0, 3),
            new Cheese(2, 0),
            new Dough(0, 0),
            new Lettuce(2, 0),
            new Onion(2, 0),
            new Potato(0, 3),
            new Steak(2, 3),
            new Tomato(2, 0),
            new UncookedPizza(0, 4)
    };


    /**
     * Returns the Ingredient object at the given index
     *
     * @param index the index of the ingredient to get
     * @return the Ingredient at the given index
     */
    public static Ingredient getIngredientAt(int index) { return ingredients[index]; }


    /**
     * Returns the index of the given ingredient in the Ingredients array
     * @param ingredient the ingredient to find the index of
     * @return an integer indicating the index of the ingredient in the array, or -1 if the ingredient cannot be found
     */
    public static int getIndexOfIngredient(Ingredient ingredient) {
        for(int i = 0; i < ingredients.length; i++)
        {
            if(ingredients[i].getClass().getName().equals(ingredient.getClass().getName()))
            {
                return i;
            }
        }
        return -1;
    }

}
