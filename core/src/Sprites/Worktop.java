package Sprites;

import Ingredients.Ingredient;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    private Sprite heldItem;

    public Worktop(World world, TiledMap map, BodyDef bdef, Rectangle rectangle) {
        super(world, map, bdef, rectangle);
        fixture.setUserData(this);
        heldItem = null;
    }

    /**
     * @return the worktop's stored ingredient.
     */
    public Sprite getHeldItem()
    {
        return heldItem;
    }


    /**
     * Gets the x-coordinate of the worktop.
     * @return The x-coordinate of the worktop.
     */
    public float getX(){
        return super.bdefNew.position.x;
    }


    /**
     * Gets the y-coordinate of the worktop.
     * @return The y-coordinate of the worktop.
     */
    public float getY(){
        return super.bdefNew.position.y;
    }


    /**
     * @param item The ingredient/recipe to place on the worktop.
     */
    public void setHeldItem(Sprite item)
    {
        this.heldItem = item;
    }
}