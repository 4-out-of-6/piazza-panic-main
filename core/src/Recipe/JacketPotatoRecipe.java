package Recipe;

import Ingredients.Bun;
import Ingredients.Cheese;
import Ingredients.Potato;
import Ingredients.Steak;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;


/**

 The JacketPotato class is a subclass of the Recipe class and represents a jacket potato dish in the kitchen game.
 It holds an ArrayList of ingredients needed to make a jacket potato and a Texture of the completed dish image.
 The ingredients in the ArrayList consist of a {@link Ingredients.Bun} and a {@link Ingredients.Steak}.
 */


public class JacketPotatoRecipe extends Recipe{

    public JacketPotatoRecipe(){
        super.ingredients = new ArrayList<>();
        ingredients.add(new Potato(0, 0));
        ingredients.add(new Cheese(0, 0));
        //completedImg = new Texture("Food/Burger.png");
    }
}
