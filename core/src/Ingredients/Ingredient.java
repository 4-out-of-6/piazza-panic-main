package Ingredients;

import Recipe.Recipe;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team13.piazzapanic.MainGame;

import java.util.ArrayList;

public abstract class Ingredient extends Sprite {
    /**
     * The time required to prepare the ingredient.
     */
    public float prepareTime;
    /**
     * The time required to cook the ingredient.
     */
    public float cookTime;
    /**
     * A flag to indicate if the ingredient needs to be pan-fried or cooked in the oven
     */
    public boolean cookInOven;
    /**
     * An overriding value that replaces the ingredient with a finished Recipe when prepared
     */
    private Recipe recipeOverride;
    /**
     * A flag to indicate whether the ingredient has been cooked.
     */
    private boolean amICooked;
    /**
     * A flag to indicate whether the ingredient has been prepared.
     */
    private boolean amIPrepared;
    /**
     * An array of textures representing different states of the ingredient.
     */
    public ArrayList<Texture> tex;

    /**
     * Constructs a new Ingredient object with the specified preparation and cooking times.
     *
     * @param prepareTime The time required to prepare the ingredient.
     * @param cookTime The time required to cook the ingredient.
     * @param cookInOven Whether the ingredient is cooked in an oven (=true) or pan (=false). Default = false
     */
    public Ingredient(float prepareTime, float cookTime, boolean cookInOven)
    {
        this.prepareTime = prepareTime;
        this.cookTime = cookTime;
        this.cookInOven = cookInOven;
        this.recipeOverride = null;
        this.amICooked = false;
        this.amIPrepared = false;
        this.tex = null;
    }

    public Ingredient(float prepareTime, float cookTime) {
        this.prepareTime = prepareTime;
        this.cookTime = cookTime;
        this.cookInOven = false;
        this.recipeOverride = null;
        this.amICooked = false;
        this.amIPrepared = false;
        this.tex = null;
    }

    /**
     * Sets the flag indicating that the ingredient has been prepared.
     *
     */
    public void setPrepared() {
        amIPrepared = true;
    }

    /**
     * Sets the recipe that overrides the prepared ingredient.
     * When preparing an ingredient completes a dish, it needs to be overridden in the
     * chef's stack with the corresponding recipe (e.g. cooking an Uncooked Pizza
     * ingredient turns it into a Cooked Pizza - a completed dish and therefore a
     * Recipe). See Chef class for full implementation
     */
    public void setRecipeOverride(Recipe recipe) { recipeOverride = recipe; }

    public Recipe getRecipeOverride() { return recipeOverride; }

    /**
     * Returns the value of the flag indicating whether the ingredient has been prepared.
     *
     * @return A boolean indicating whether the ingredient has been prepared.
     */
    public boolean isPrepared() {
        return amIPrepared;
    }

    /**
     * Sets the flag indicating that the ingredient has been cooked.
     *
     */
    public void setCooked() {
        amICooked= true;
    }

    /**
     * Returns the value of the flag indicating whether the ingredient has been cooked.
     *
     * @return A boolean indicating whether the ingredient has been cooked.
     */
    public boolean isCooked() {
        return amICooked;
    }

    /**
     * Creates and draws a new Sprite object representing the ingredient.
     *
     * @param x The x coordinate of the ingredient.
     * @param y The y coordinate of the ingredient.
     * @param batch The SpriteBatch object used to draw the ingredient.
     */
    public void create(float x, float y, SpriteBatch batch){
        Sprite sprite = new Sprite(tex.get(findCorrectSkin()));
        float adjustedX =  x - (5/MainGame.PPM);
        float adjustedY =  y - (4.95f / MainGame.PPM);
        sprite.setBounds(adjustedX,adjustedY,10/ MainGame.PPM,10/ MainGame.PPM);
        sprite.draw(batch);
    }

    /**
     * Determines the correct texture to use for the ingredient sprite based on its prepare and cook state.
     * @return int - The correct skin of the ingredient, either 0, 1, or 2.
     * 0 represents the uncooked and unprepared ingredient.
     * 1 represents the prepared but uncooked ingredient.
     * 2 represents the fully cooked and prepared ingredient.
     *
     * */
    private int findCorrectSkin(){
        if (isPrepared() && isCooked()){
            return 2;
        } else if (isPrepared() || isCooked()){
            return 1;
        } else {
            return 0;
        }
    }
}