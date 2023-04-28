package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.team13.piazzapanic.MainGame;

/**
 * A visual represenation that more chefs can be purchased
 */
public class BuyChefState {

    private static Texture selectedBackgroundTexture = new Texture("Locked_State/locked_state_background_selected.png");
    private static Texture unselectedBackgroundTexture = new Texture("Locked_State/locked_state_background.png");
    private static Texture price200Texture = new Texture("Station_Prices/station_price_200.png");
    private static Texture price500Texture = new Texture("Station_Prices/station_price_500.png");
    private static Texture chefTexture = new Texture("Locked_State/locked_state_chef.png");


    /**
     * Returns the relative grid tile for the given position
     * @param a The position in the game world
     * @return The grid position of the given location.
     */
    private static int getGridPos(float a) { return (int)(a / (16 / MainGame.PPM)); }


    public static void create(float x, float y, Chef chef, int chefsUnlocked, Batch batch) {

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
            backgroundTex = selectedBackgroundTexture;
            if(chefsUnlocked == 1) { lockTex = price200Texture; }
            else { lockTex = price500Texture; }
            }
        else
        {
            backgroundTex = unselectedBackgroundTexture;
            lockTex = chefTexture;
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
