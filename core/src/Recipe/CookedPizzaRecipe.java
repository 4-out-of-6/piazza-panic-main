package Recipe;

import Ingredients.*;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;


/**

 The PizzaRecipe class is a subclass of the Recipe class and represents a pizza dish in the kitchen game.
 It holds an ArrayList of ingredients needed to make a pizza and a Texture of the completed dish image.
 The ingredients in the ArrayList consist of a {@link Ingredients.Bun} and a {@link Ingredients.Steak}.
 */


public class CookedPizzaRecipe extends Recipe{

    public CookedPizzaRecipe(){
        super.ingredients = new ArrayList<>();
        completedImg = new Texture("Food/Cooked_pizza.png");
    }
}
