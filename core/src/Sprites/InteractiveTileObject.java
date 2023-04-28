package Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.team13.piazzapanic.MainGame;


public abstract class InteractiveTileObject {

    protected Fixture fixture;

    protected BodyDef bdefNew;

    protected boolean locked;

    /**
     * Constructor for the class, initialises b2bodies.
     *
     * @param world The playable world.
     * @param map The tiled map.
     * @param bdef The body definition of a tile.
     * @param rectangle Rectangle shape.
     */
    public InteractiveTileObject(World world, TiledMap map, BodyDef bdef, Rectangle rectangle) {

        bdefNew = bdef;

        Body b2body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rectangle.getWidth() / 2f) / MainGame.PPM, (rectangle.getHeight() / 2f) / MainGame.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fixture = b2body.createFixture(fdef);

        locked = false;
    }

    /**
     * Puts the interactive station in a 'locked' state. This state can be undone
     * with money obtained throughout the game.
     */
    public void setLocked() { locked = true; }

    /**
     * Unlocks the station, allowing it to be used
     */
    public void setUnlocked() { locked = false; }

    /**
     * Returns whether the tile object is in its locked state
     * @return Boolean stating whether the station is locked
     */
    public boolean isLocked() { return locked; }

    /**
     * Gets the x-coordinate of the worktop.
     * @return The x-coordinate of the worktop.
     */
    public float getX(){
        return bdefNew.position.x;
    }


    /**
     * Gets the y-coordinate of the worktop.
     * @return The y-coordinate of the worktop.
     */
    public float getY(){
        return bdefNew.position.y;
    }

}