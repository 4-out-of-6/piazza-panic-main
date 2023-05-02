package Sprites;

import Ingredients.Dough;
import Ingredients.Ingredient;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Class representing the Dough Station in the game.
 * Extends the InteractiveTileObject class, which handles the Box2D physics.
 */
public class DoughStation extends InteractiveTileObject {

    /**
     * Constructor for DoughStation.
     * Creates a new DoughStation with a Box2D body and fixture.
     *
     * @param world The playable world.
     * @param map The tiled map.
     * @param bdef The body definition of a tile.
     * @param rectangle Rectangle shape.
     */
    public DoughStation(World world, TiledMap map, BodyDef bdef, Rectangle rectangle) {
        super(world, map, bdef, rectangle);
        fixture.setUserData(this);
    }

    /**
     * Gets an Ingredient (in this case a bun) from the station.
     *
     * @return A new Bun object.
     */
    public Ingredient getIngredient() {
        return new Dough(0, 0);
    }
}


