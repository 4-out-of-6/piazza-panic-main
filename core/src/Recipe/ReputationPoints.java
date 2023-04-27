package Recipe;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team13.piazzapanic.MainGame;


public class ReputationPoints extends Sprite {

    private static Texture activePoint = new Texture("Reputation_Points/reputation_point_on.png");
    private static Texture inactivePoint = new Texture("Reputation_Points/reputation_point_off.png");

    public static void create(int reputationPoints, SpriteBatch batch) {

        for(int i=1; i<4; i++)
        {
            Sprite repPoint;
            if(i > reputationPoints) { repPoint = new Sprite(inactivePoint); }
            else { repPoint = new Sprite(activePoint); }

            float x = 0.45f - ((i-1) * 0.05f);
            float y = 1.3f;
            repPoint.setBounds(x, y, 4 / MainGame.PPM, 4 / MainGame.PPM);
            repPoint.draw(batch);
        }

    }

}
