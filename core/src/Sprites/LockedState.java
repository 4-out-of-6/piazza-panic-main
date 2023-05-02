package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.team13.piazzapanic.MainGame;

/**
 * A visual represenation that a station is locked and needs unlocking.
 */
public class LockedState {


    private static Texture unselectedBackTexture = new Texture("Locked_State/locked_state_background.png");
    private static Texture selectedBackTexture = new Texture("Locked_State/locked_state_background_selected.png");
    private static Texture unselectedLockTexture = new Texture("Locked_State/locked_state_padlock.png");
    private static Texture selectedLockTexture = new Texture("Station_Prices/station_price_100.png");


    /**
     * Returns the relative grid tile for the given position
     * @param a The position in the game world
     * @return The grid position of the given location.
     */
    private static int getGridPos(float a) { return (int)(a / (16 / MainGame.PPM)); }


    public static void create(float x, float y, Chef chef, Batch batch) {

        float adjustedX =  x - (8/MainGame.PPM);
        float adjustedY =  y - (8 / MainGame.PPM);

        float adjustedChefX = chef.getX() + (8 / MainGame.PPM);
        float adjustedChefY = chef.getY() + (8 / MainGame.PPM);

        double xDist = Math.abs(getGridPos(adjustedChefX) - getGridPos(x));
        double yDist = Math.abs(getGridPos(adjustedChefY) - getGridPos(y));
        boolean inRange = (xDist == 1 && yDist == 0) || (yDist == 1 && xDist == 0);
        Texture backgroundTex;
        Texture lockTex;

        if(inRange)
        {
            backgroundTex = selectedBackTexture;
            lockTex = selectedLockTexture;
        }
        else
        {
            backgroundTex = unselectedBackTexture;
            lockTex = unselectedLockTexture;
        }

        Sprite backgroundSprite = new Sprite(backgroundTex);
        Sprite lockSprite = new Sprite(lockTex);

        backgroundSprite.setBounds(adjustedX, adjustedY, 16 / MainGame.PPM, 16 / MainGame.PPM);
        backgroundSprite.setAlpha(0.75f);
        backgroundSprite.draw(batch);

        lockSprite.setBounds(adjustedX, adjustedY, 16 / MainGame.PPM, 16 / MainGame.PPM);
        lockSprite.draw(batch);
    }

}
