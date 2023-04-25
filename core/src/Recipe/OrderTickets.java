package Recipe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team13.piazzapanic.MainGame;

import java.util.ArrayList;

public class OrderTickets extends Sprite {

    private static float x = 11;
    private static float y = 6;

    /**
     * Draws the ticket receipts at the top of the screen, and a green box around the one currently being viewed.
     * @param ordersArray The array of orders currently put in
     * @param viewedOrder The order that is currently being viewed
     * @param batch The batch to draw the sprites to
     */
    public static void create(ArrayList<Order> ordersArray, int viewedOrder, SpriteBatch batch) {

        for(int i=0; i<3; i++)
        {
            // Draw each ticket
            Sprite ticketSprite = new Sprite(new Texture("Order_Tickets/order_" + (i+1) + ".png"));
            float ticketX = 0.85f;
            float ticketY = 1.5f - (i * 0.11f);
            ticketSprite.setBounds(ticketX, ticketY, 16 / MainGame.PPM, 10 / MainGame.PPM);
            if(i >= ordersArray.size()) { ticketSprite.setAlpha(0.5f); }
            ticketSprite.draw(batch);

            // Draw the recipe required for each ticket
            if(i < ordersArray.size())
            {
                Sprite orderSprite = new Sprite(ordersArray.get(i).getRecipe().getCompletedImg());
                orderSprite.setBounds(ticketX + (6.5f / MainGame.PPM), ticketY + (1 / MainGame.PPM), 8 / MainGame.PPM, 8 / MainGame.PPM);
                orderSprite.draw(batch);

                // If the ticket is the one currently being viewed, draw the selected box
                if(viewedOrder == i)
                {
                    Sprite selectSprite = new Sprite(new Texture("Order_Tickets/selected_ticket.png"));
                    selectSprite.setBounds(ticketX, ticketY, 16 / MainGame.PPM, 10 / MainGame.PPM);
                    selectSprite.draw(batch);
                }

                // Decrement the counter by the time elapsed since the last frame
                ordersArray.get(i).decrementCounterBy(Gdx.graphics.getDeltaTime());

                // Draw timer background
                Sprite timerBackground = new Sprite(new Texture("Timer/timer_background.png"));
                float timerX = ticketX - (4 / MainGame.PPM);
                float timerY = ticketY + (1 / MainGame.PPM);
                timerBackground.setBounds(timerX, timerY, 1 / MainGame.PPM, 8 / MainGame.PPM);
                timerBackground.draw(batch);

                // Draw timer fill
                float percentageFull = Math.max(0, ordersArray.get(i).getCountdownTimer() / ordersArray.get(i).getInitialTimer());
                Texture timerFill;
                if(percentageFull < 0.25f) { timerFill = new Texture("Timer/timer_foreground_danger.png"); }
                else if(percentageFull < 0.5f) { timerFill = new Texture("Timer/timer_foreground_warning.png"); }
                else { timerFill = new Texture("Timer/timer_foreground_full.png"); }
                Sprite timerForeground = new Sprite(timerFill);
                timerForeground.setBounds(timerX, timerY, 1 / MainGame.PPM, (8 * percentageFull) / MainGame.PPM);
                timerForeground.draw(batch);
            }
        }

    }

}