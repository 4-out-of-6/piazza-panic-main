package com.team13.piazzapanic;

import Ingredients.Ingredient;
import Ingredients.IngredientManager;
import Recipe.Recipe;
import Recipe.RecipeManager;
import Recipe.Order;
import Sprites.Chef;
import Sprites.Worktop;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public final class LoadManager {

    private static Preferences progressData;
    private static Preferences chefData;
    private static Preferences tileData;
    private static Preferences orderData;


    /**
     * Initialises and fetches the save data
     */
    public static boolean initialise() {
        progressData = Gdx.app.getPreferences("piazza_panic_progress");
        chefData = Gdx.app.getPreferences("piazza_panic_chef");
        tileData = Gdx.app.getPreferences("piazza_panic_tile");
        orderData = Gdx.app.getPreferences("piazza_panic_order");
        return validateLoad();
    }

    /**
     * Assigns the load data to the given screen, if applicable
     * @param screen the instance of PlayScreen to assign the values to
     */
    public static void loadMidGameSave(PlayScreen screen, HUD hud) {

        /*
            Progress Data
         */
        hud.setScore(getPrefInt(                progressData, "score"               ));
        screen.orderCounter = getPrefInt(       progressData, "completed_orders"    );
        hud.setTime(getPrefInt(                 progressData, "hud_time"            ));
        screen.reputationPoints = getPrefInt(   progressData, "rep_points"          );
        screen.customerCounter = getPrefInt(    progressData, "customer_counter"    );
        screen.customerTotal = getPrefInt(      progressData, "customer_total"      );
        screen.difficulty = getPrefString(      progressData, "difficulty"          );
        screen.interval = getPrefFloat(         progressData, "interval"            );
        screen.timeSeconds = getPrefFloat(      progressData, "time_seconds"        );
        screen.timeSecondsCount = getPrefFloat( progressData, "time_seconds_count"  );
        screen.orderTimeGap = getPrefFloat(     progressData, "order_time_gap"      );
        hud.setOrder(screen.orderCounter);

        /*
            Chef Data
         */
        // Chef 1
        Chef c1 = screen.chef1;
        c1.b2body.setTransform(getPrefFloat(chefData, "c1_pos_x"), getPrefFloat(chefData, "c1_pos_y"), c1.b2body.getAngle());
        c1.setY(getPrefFloat(chefData, "c1_pos_y"));
        c1.replaceStack(stringToStack(getPrefString(chefData, "c1_stack")));
        // Chef 2
        Chef c2 = screen.chef2;
        if(!getPrefBoolean(chefData, "c2_locked")) {
            c2.setUnlocked();
            c2.b2body.setTransform(getPrefFloat(chefData, "c2_pos_x"), getPrefFloat(chefData, "c2_pos_y"), c2.b2body.getAngle());
        }
        c2.replaceStack(stringToStack(getPrefString(chefData, "c2_stack")));
        // Chef 3
        Chef c3 = screen.chef3;
        if(!getPrefBoolean(chefData, "c3_locked")) {
            c3.setUnlocked();
            c3.b2body.setTransform(getPrefFloat(chefData, "c3_pos_x"), getPrefFloat(chefData, "c3_pos_y"), c3.b2body.getAngle());
        }
        c3.replaceStack(stringToStack(getPrefString(chefData, "c3_stack")));

        /*
            Tile Data
         */
        // Worktop Stations
        ArrayList<Sprite> worktopItems = stringToStack(getPrefString(tileData, "worktops"));
        for(int i=0; i < screen.worktopStations.size(); i++)
        {
            Worktop w = screen.worktopStations.get(i);
            w.setHeldItem(worktopItems.get(i));
        }
        // Plate Station
        Recipe recipeDone = (Recipe)stringToItem(getPrefString(tileData, "recipe_done"));
        if(recipeDone == null)
        {
            ArrayList<Sprite> plate = stringToStack(getPrefString(tileData, "plate_station"));
            screen.plateStation.setPlate(plate);
        }
        else
        {
            screen.plateStation.setRecipeDone(recipeDone);
        }
        // Unlocked Stations
        String usString = getPrefString(tileData, "unlocked_stations");
        String[] usStringArr = usString.split(",");
        System.out.println(usString);
        for(int i=0; i<screen.preparationStations.size(); i++)
        {
            System.out.println(Boolean.parseBoolean(usStringArr[i]));
            if(Boolean.parseBoolean(usStringArr[i]) == false)
            {
                screen.preparationStations.get(i).setUnlocked();
            }
        }

        /*
            Order Data
         */
        for(int i=0; i < 3; i++) {
            if(orderData.contains(i + "_id"))
            {
                int orderId = getPrefInt(orderData, (i + "_id"));
                float countdownTimer = getPrefFloat(orderData, (i + "_countdown_timer"));
                Order newOrder = new Order(RecipeManager.getCompleteRecipeAt(orderId), RecipeManager.getRecipeTextureAt(orderId), countdownTimer);
                newOrder.setInitialTimer(getPrefFloat(orderData, (i + "_initial_timer")));
                screen.ordersArray.add(newOrder);
            }
        }


    }

    static Integer getPrefInt(Preferences prefs, String key) { return Integer.parseInt(prefs.get().get(key).toString()); }
    static String getPrefString(Preferences prefs, String key) { return prefs.get().get(key).toString(); }
    static Float getPrefFloat(Preferences prefs, String key) { return Float.parseFloat(prefs.get().get(key).toString()); }
    static boolean getPrefBoolean(Preferences prefs, String key) { return Boolean.parseBoolean(prefs.get().get(key).toString()); }


    public static String getDifficulty() { return getPrefString(progressData, "difficulty"); }

    /**
     * Checks if there is a save game on file
     * @return true if a save game is available, false otherwise
     */
    static boolean validateLoad() {
        if(progressData.get().size() == 0) {
            System.out.println("No save file detected.");
            return false;
        }
        else {
            System.out.println("Active save file detected");
            return true;
        }
    }


    /**
     * Converts a given string into an ingredient/recipe format
     * @param string the string to be converted
     * @return a stack, containing the given ingredients / recipes
     */
    static ArrayList<Sprite> stringToStack(String string)
    {
        ArrayList<Sprite> stack = new ArrayList<>();

        String[] elements = string.split("\n");

        for(String element : elements)
        {
            if(!element.equals(""))
            {
                stack.add(stringToItem(element));
            }
        }

        return stack;
    }

    static Sprite stringToItem(String string)
    {
        String[] attributes = string.split(",");
        String type = attributes[0];

        if(type.equals("recipe"))
        {
            Recipe newRecipe = RecipeManager.getRecipeAt(Integer.valueOf(attributes[1]));
            return newRecipe;
        }
        else if(type.equals("ingredient"))
        {
            Ingredient newIngredient = IngredientManager.getIngredientAt(Integer.valueOf(attributes[1]));
            if(Boolean.parseBoolean(attributes[2])) { newIngredient.setPrepared(); }
            if(Boolean.parseBoolean(attributes[3])) { newIngredient.setCooked(); }
            if(Boolean.parseBoolean(attributes[4])) { newIngredient.setFailed(); }
            return newIngredient;
        }
        else
        {
            return null;
        }
    }

}
