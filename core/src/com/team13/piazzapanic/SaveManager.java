package com.team13.piazzapanic;

import Ingredients.Ingredient;
import Ingredients.IngredientManager;
import Recipe.Order;
import Recipe.Recipe;
import Recipe.RecipeManager;
import Sprites.Chef;
import Sprites.InteractiveTileObject;
import Sprites.Worktop;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public final class SaveManager {

    private static boolean endGameSaved = false;
    public static boolean newHighScore = false;

    public static void saveEndGameState(PlayScreen screen)
    {
        if(!endGameSaved)
        {
            // Update the high score preferences
            Preferences hsData = Gdx.app.getPreferences("piazza_panic_hs");
            String difficulty = screen.difficulty;
            System.out.println("DIFFICULTY: " + hsData.get().get(difficulty).toString());
            if(Integer.parseInt(hsData.get().get(difficulty).toString()) < screen.orderCounter)
            {
                hsData.putInteger(difficulty, screen.orderCounter);
                hsData.flush();
                newHighScore = true;
            }

            // Clear mid-game save data to prevent save hopping
            Preferences progressData = Gdx.app.getPreferences("piazza_panic_progress");
            progressData.clear();
            progressData.flush();
            Preferences chefData = Gdx.app.getPreferences("piazza_panic_chef");
            chefData.clear();
            chefData.flush();
            Preferences tileData = Gdx.app.getPreferences("piazza_panic_tile");
            tileData.clear();
            tileData.flush();
            Preferences orderData = Gdx.app.getPreferences("piazza_panic_order");
            orderData.clear();
            orderData.flush();

            endGameSaved = true;
        }
    }

    /**
     * Saves the current state of the game to the preferences file. Follows the structure dictated in
     * assets/save_structure.txt
     * @param screen The current instance of the PlayScreen
     * @param hud The current instance of the HUD
     */
    public static void saveMidGameState(PlayScreen screen, HUD hud)
    {
        /*
            Progress Data
         */
        Preferences progressData = Gdx.app.getPreferences("piazza_panic_progress");
        progressData.putInteger("score",               hud.getScore());
        progressData.putInteger("completed_orders",    screen.orderCounter);
        progressData.putInteger("hud_time",            hud.totalTimer);
        progressData.putInteger("rep_points",          screen.reputationPoints);
        progressData.putInteger("customer_counter",    screen.customerCounter);
        progressData.putInteger("customer_total",      screen.customerTotal);
        progressData.putString("difficulty",           screen.difficulty);
        progressData.putFloat("interval",              (float)screen.interval);
        progressData.putFloat("time_seconds",          screen.timeSeconds);
        progressData.putFloat("time_seconds_count",    (float)screen.timeSecondsCount);
        progressData.putFloat("order_time_gap",        (float)screen.orderTimeGap);

        /*
            Chef Data
         */
        Preferences chefData = Gdx.app.getPreferences("piazza_panic_chef");
        // Chef 1
        Chef chef1 = screen.chef1;
        chefData.putFloat("c1_pos_x",     chef1.b2body.getPosition().x);
        chefData.putFloat("c1_pos_y",     chef1.b2body.getPosition().y);
        chefData.putString("c1_stack",    stackToString(chef1.getInHandsStack()));
        // Chef 2
        Chef chef2 = screen.chef2;
        chefData.putBoolean("c2_locked",  chef2.isLocked());
        if(!chef2.isLocked())
        {
            chefData.putFloat("c2_pos_x",     chef2.b2body.getPosition().x);
            chefData.putFloat("c2_pos_y",     chef2.b2body.getPosition().y);
        }
        chefData.putString("c2_stack",    stackToString(chef2.getInHandsStack()));
        // Chef 3
        Chef chef3 = screen.chef3;
        chefData.putBoolean("c3_locked",  chef3.isLocked());
        if(!chef3.isLocked())
        {
            chefData.putFloat("c3_pos_x",     chef3.b2body.getPosition().x);
            chefData.putFloat("c3_pos_y",     chef3.b2body.getPosition().y);
        }
        chefData.putString("c3_stack",    stackToString(chef3.getInHandsStack()));

        /*
            Tile Data
         */
        Preferences tileData = Gdx.app.getPreferences("piazza_panic_tile");
        // Worktop Stations
        ArrayList<Worktop> worktopStations = screen.worktopStations;
        ArrayList<Sprite> heldItems = new ArrayList<>();
        for(Worktop w : worktopStations)
        {
            heldItems.add(w.getHeldItem());
        }
        tileData.putString("worktops", stackToString(heldItems));
        // Plate Station
        tileData.putString("plate_station", stackToString(screen.plateStation.getPlate()));
        tileData.putString("recipe_done", itemToString(screen.plateStation.getRecipeDone()));
        // Unlocked Stations
        ArrayList<InteractiveTileObject> preparationStations = screen.preparationStations;
        String unlockedStations = "";
        for(int u=0; u<preparationStations.size(); u++)
        {
            unlockedStations = unlockedStations + preparationStations.get(u).isLocked() + "," ;
        }
        tileData.putString("unlocked_stations", unlockedStations);

        /*
            Order Data
         */
        Preferences orderData = Gdx.app.getPreferences("piazza_panic_order");
        // Generate array of orders in HashMap format
        ArrayList<Order> ordersArray = screen.ordersArray;
        for(int r=0; r<ordersArray.size(); r++)
        {
            Order order = ordersArray.get(r);
            orderData.putInteger(r + "_id",               RecipeManager.getIndexOfCompleteRecipe(order.getRecipe()));
            orderData.putFloat(r + "_countdown_timer",    order.getCountdownTimer());
            orderData.putFloat(r + "_initial_timer",      order.getInitialTimer());
        }


        /*
            Commit save data to Prefs
         */
        progressData.flush();
        chefData.flush();
        tileData.flush();
        orderData.flush();

    }


    /**
     * Converts a given ingredient/recipe stack into a string format
     * @param stack the stack to be converted
     * @return a string, detailing the contents of the stack
     */
    static String stackToString(ArrayList<Sprite> stack)
    {
        String value = "";
        for(int i = 0; i < stack.size(); i++)
        {
            Sprite item = stack.get(i);
            value += itemToString(item);

            if(i != stack.size() - 1) value += '\n';
        }
        return value;
    }

    static String itemToString(Sprite item)
    {
        String value = "";
        if(item instanceof Recipe)
        {
            value += "recipe,";
            value += RecipeManager.getIndexOfRecipe((Recipe) item);
        }
        else if(item instanceof Ingredient)
        {
            Ingredient ing = (Ingredient) item;
            value += "ingredient,";
            value += IngredientManager.getIndexOfIngredient(ing) + ",";
            value += ing.isPrepared() + ",";
            value += ing.isCooked() + ",";
            value += ing.hasFailed();
        }
        else if(item == null)
        {
            value += "null";
        }
        return value;
    }

}
