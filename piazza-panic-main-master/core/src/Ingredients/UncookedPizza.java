package Ingredients;

import Recipe.CookedPizzaRecipe;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class UncookedPizza extends Ingredient{

    /**
     * The UncookedPizza class represents a specific type of ingredient in the game, specifically an uncooked pizza.
     * It extends the {@link Ingredient} class and has a preparation time and cooking time.
     * The UncookedPizza class sets the prepared flag to true in the constructor, and sets up an ArrayList of textures for its different skins.
     * It is the Ingredient class equivalent of the UncookedPizzaRecipe class.
     */

    public UncookedPizza(float prepareTime, float cookTime) {
        super(prepareTime, cookTime, true);
        super.setPrepared();
        super.setRecipeOverride(new CookedPizzaRecipe()); // Override the cooked pizza step with the Cooked Pizza Recipe
        super.tex = new ArrayList<>();

        super.tex.add(null);
        super.tex.add(new Texture("Food/Pizza.png"));
    }
}