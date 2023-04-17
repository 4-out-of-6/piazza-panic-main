package Sprites;

import Ingredients.Ingredient;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Represents a Worktop in the game in which blocks the chefs movement around the kitchen to stop him from
 * escaping the bounds.
 *
 * Worktops can also store a single ingredient on them.
 */
public class Worktop extends InteractiveTileObject {

    private Ingredient ingredient;

    public Worktop(World world, TiledMap map, BodyDef bdef, Rectangle rectangle) {
        super(world, map, bdef, rectangle);
        fixture.setUserData(this);
        ingredient = null;
    }

    /**
     * @return the worktop's stored ingredient.
     */
    public Ingredient getIngredient()
    {
        return ingredient;
    }


    /**
     * Gets the x-coordinate of the plate station.
     * @return The x-coordinate of the plate station.
     */
    public float getX(){
        return super.bdefNew.position.x;
    }


    /**
     * Gets the y-coordinate of the plate station.
     * @return The y-coordinate of the plate station.
     */
    public float getY(){
        return super.bdefNew.position.y;
    }


    /**
     * @param ingredient The ingredient to place on the worktop.
     */
    public void setIngredient(Ingredient ingredient)
    {
        this.ingredient = ingredient;
    }
}