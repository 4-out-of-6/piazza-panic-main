package Ingredients;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class Dough extends Ingredient{

    /**

     The Dough class represents a specific type of ingredient in the game, specifically the burger buns.
     It extends the {@link Ingredient} class and has a preparation time and cooking time.
     The Dough class sets the prepared flag to true in the constructor, and sets up an ArrayList of textures for its different skins.

     */

    public Dough(float prepareTime, float cookTime) {
        super(prepareTime, cookTime);
        super.setPrepared();
        super.tex = new ArrayList<>();
        super.tex.add(null);
        super.tex.add(new Texture("Food/Dough.png"));
    }
}
