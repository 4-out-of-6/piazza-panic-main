package Recipe;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team13.piazzapanic.MainGame;

public class OrderTickets extends Sprite {

    public void create(Order[] ordersArray, int viewingOrder, SpriteBatch batch) {

        int x = 110;
        int y = 60;

        for(int i = 1; i < 4; i++)
        {
            Sprite sprite = new Sprite(new Texture(("Order_Tickets/order_" + i + ".png")));
            float adjustedX = x - (8 / MainGame.PPM);
            float adjustedY = y + (7 / MainGame.PPM) - (10 * (i - 1));
            sprite.setBounds(adjustedX, adjustedY, 10 / MainGame.PPM, 17 / MainGame.PPM);
            sprite.draw(batch);

            if(i == viewingOrder) {
               Sprite selectedSprite = new Sprite(new Texture("Order_Tickets/selected_ticket.png"));
               float selectedX = sprite.getX() - 1;
               float selectedY = sprite.getY() - 1;
               selectedSprite.setBounds(selectedX, selectedY, 12 / MainGame.PPM, 19 / MainGame.PPM);
               selectedSprite.draw(batch);
            }
        }
    }

}
