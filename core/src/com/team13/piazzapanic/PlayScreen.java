package com.team13.piazzapanic;

import Ingredients.Ingredient;
import Recipe.Recipe;
import Sprites.*;
import Recipe.Order;
import Recipe.RecipeManager;
import Recipe.OrderTickets;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The PlayScreen class is responsible for displaying the game to the user and handling the user's interactions.
 * The PlayScreen class implements the Screen interface which is part of the LibGDX framework.
 *
 * The PlayScreen class contains several important fields, including the game instance, stage instance, viewport instance,
 * and several other helper classes and variables. The game instance is used to access the global game configuration and
 * to switch between screens. The stage instance is used to display the graphics and handle user interactions, while the
 * viewport instance is used to manage the scaling and resizing of the game window.
 *
 * The PlayScreen class also contains several methods for initializing and updating the game state, including the
 * constructor, show(), render(), update(), and dispose() methods. The constructor sets up the stage, viewport, and
 * other helper classes and variables. The show() method is called when the PlayScreen becomes the active screen. The
 * render() method is called repeatedly to render the game graphics and update the game state. The update() method is
 * called to update the game state and handle user inputs. The dispose() method is called when the PlayScreen is no longer
 * needed and is used to clean up resources and prevent memory leaks.
 */


public class PlayScreen implements Screen {

    private final MainGame game;
    private final OrthographicCamera gamecam;
    private final Viewport gameport;
    private final HUD hud;

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    private final World world;
    private final Chef chef1;
    private final Chef chef2;
    private final Chef chef3;

    private Chef controlledChef;

    public ArrayList<Order> ordersArray;

    public PlateStation plateStation;
    public ArrayList<Worktop> worktopStations = new ArrayList<>();


    public Boolean scenarioComplete;
    public int customerTotal;
    private int customerCounter = 0;
    private int orderCounter = 0;

    public static float trayX;
    public static float trayY;

    private float timeSeconds = 0f;

    private float timeSecondsCount = 0f;

    private int orderViewed = 0;
    private float orderTimeGap = 0;

    /**
     * PlayScreen constructor initializes the game instance, sets initial conditions for scenarioComplete and createdOrder,
     * creates and initializes game camera and viewport,
     * creates and initializes HUD and orders hud, loads and initializes the map,
     * creates and initializes world, creates and initializes chefs and sets them, sets contact listener for world, and initializes ordersArray.
     * @param game The MainGame instance that the PlayScreen will be a part of.
     */

    public PlayScreen(MainGame game){
        this.game = game;
        scenarioComplete = Boolean.FALSE;
        RecipeManager.initialise();
        gamecam = new OrthographicCamera();
        // FitViewport to maintain aspect ratio whilst scaling to screen size
        gameport = new FitViewport(MainGame.V_WIDTH / MainGame.PPM, MainGame.V_HEIGHT / MainGame.PPM, gamecam);
        // create HUD for score & time
        hud = new HUD(game.batch);
        // create orders hud
        Orders orders = new Orders(game.batch);
        // create map
        TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        map = mapLoader.load("Kitchen.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MainGame.PPM);
        gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0,0), true);
        new B2WorldCreator(world, map, this);

        chef1 = new Chef(this.world, 31.5F,65);
        chef2 = new Chef(this.world, 128,65);
        chef3 = new Chef(this.world, 79.75F, 65);
        controlledChef = chef1;
        world.setContactListener(new WorldContactListener());
        controlledChef.notificationSetBounds("Down");

        ordersArray = new ArrayList<>();

    }

    @Override
    public void show(){

    }


    /**
     * The handleInput method is responsible for handling the input events of the game such as movement and interaction with objects.
     *
     * It checks if the 'R' key is just pressed and both chefs have the user control, if so,
     * it switches the control between the two chefs.
     *
     * If the controlled chef does not have the user control,
     * the method checks if chef1, chef2, or chef3 has the user control and sets the control to that chef.
     *
     * If the controlled chef has the user control,
     * it checks if the 'W', 'A', 'S', or 'D' keys are pressed and sets the velocity of the chef accordingly.
     *
     * If the 'E' key is just pressed and the chef is touching a tile,
     * it checks the type of tile and sets the chef's in-hands ingredient accordingly.
     *
     * The method also sets the direction of the chef based on its linear velocity.
     *
     * @param dt is the time delta between the current and previous frame.
     */

    public void handleInput(float dt){
        // If all three chefs can move, switch to the next one
        if ((Gdx.input.isKeyJustPressed(Input.Keys.R) &&
                chef1.getUserControlChef() &&
                chef2.getUserControlChef() &&
                chef3.getUserControlChef())) {
            controlledChef.b2body.setLinearVelocity(0, 0);
            if (controlledChef.equals(chef1)) {
                controlledChef = chef2;
            } else if(controlledChef.equals(chef2)) {
                controlledChef = chef3;
            } else {
                controlledChef = chef1;
            }
        }
        // If the controlled chef cannot move, switch to the next available one
        if (!controlledChef.getUserControlChef()){
            controlledChef.b2body.setLinearVelocity(0, 0);
            if(controlledChef.equals(chef1))
            {
                if(chef2.getUserControlChef()) { controlledChef = chef2; }
                else if(chef3.getUserControlChef()) { controlledChef = chef3; }
            }
            else if(controlledChef.equals(chef2))
            {
                if(chef3.getUserControlChef()) { controlledChef = chef3; }
                else if(chef1.getUserControlChef()) { controlledChef = chef1; }
            }
            else
            {
                if(chef1.getUserControlChef()) { controlledChef = chef1; }
                else if(chef2.getUserControlChef()) { controlledChef = chef2; }
            }
        }
        if (controlledChef.getUserControlChef()) {
                float xVelocity = 0;
                float yVelocity = 0;

                if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    yVelocity += 0.5f;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    xVelocity -= 0.5f;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    yVelocity -= 0.5f;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    xVelocity += 0.5f;
                }
                controlledChef.b2body.setLinearVelocity(xVelocity, yVelocity);
            }
            else {
                controlledChef.b2body.setLinearVelocity(0, 0);
            }
        if (controlledChef.b2body.getLinearVelocity().x > 0){
            controlledChef.notificationSetBounds("Right");
        }
        if (controlledChef.b2body.getLinearVelocity().x < 0){
            controlledChef.notificationSetBounds("Left");
        }
        if (controlledChef.b2body.getLinearVelocity().y > 0){
            controlledChef.notificationSetBounds("Up");
        }
        if (controlledChef.b2body.getLinearVelocity().y < 0){
            controlledChef.notificationSetBounds("Down");
        }

        // Check for a change in the order being viewed
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            orderViewed = 0;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if(ordersArray.size() > 0) { orderViewed = 1; }
            else { orderViewed = 0; }
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            if(ordersArray.size() > 1) { orderViewed = 2; }
            else { orderViewed = Math.max(0, ordersArray.size() - 1); }
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
                if(controlledChef.getTouchingTile() != null){
                    InteractiveTileObject tile = (InteractiveTileObject) controlledChef.getTouchingTile().getUserData();
                    String tileName = tile.getClass().getName();

                    switch (tileName) {

                        case "Sprites.TomatoStation":
                            TomatoStation tomatoTile = (TomatoStation) tile;
                            controlledChef.pushToStack(tomatoTile.getIngredient());
                            break;

                        case "Sprites.BunsStation":
                            BunsStation bunTile = (BunsStation) tile;
                            controlledChef.pushToStack(bunTile.getIngredient());
                            break;

                        case "Sprites.OnionStation":
                            OnionStation onionTile = (OnionStation) tile;
                            controlledChef.pushToStack(onionTile.getIngredient());
                            break;

                        case "Sprites.SteakStation":
                            SteakStation steakTile = (SteakStation) tile;
                            controlledChef.pushToStack(steakTile.getIngredient());
                            break;

                        case "Sprites.LettuceStation":
                            LettuceStation lettuceTile = (LettuceStation) tile;
                            controlledChef.pushToStack(lettuceTile.getIngredient());
                            break;

                        case "Sprites.CheeseStation":
                            CheeseStation cheeseTile = (CheeseStation) tile;
                            controlledChef.pushToStack(cheeseTile.getIngredient());
                            break;

                        case "Sprites.DoughStation":
                            DoughStation doughTile = (DoughStation) tile;
                            controlledChef.pushToStack(doughTile.getIngredient());
                            break;

                        case "Sprites.PotatoStation":
                            PotatoStation potatoTile = (PotatoStation) tile;
                            controlledChef.pushToStack(potatoTile.getIngredient());
                            break;

                        case "Sprites.Worktop":
                            Worktop worktopTile = (Worktop) tile;
                            if(worktopTile.getHeldItem() != null)
                            {
                                controlledChef.pushToStack(worktopTile.getHeldItem());
                                worktopTile.setHeldItem(null);
                            }
                            else if (controlledChef.peekInHandsStack() != null) {
                                worktopTile.setHeldItem((Sprite) controlledChef.peekInHandsStack());
                                controlledChef.pushToStack(null);
                            }
                            break;

                        case "Sprites.Bin":
                            controlledChef.pushToStack(null);
                            break;

                        case "Sprites.ChoppingBoard":
                            if (controlledChef.getInHandsIng() != null) {
                                if (controlledChef.getInHandsIng().prepareTime > 0) {
                                    controlledChef.setUserControlChef(false);
                                }
                            }
                            break;

                        /**
                         * PlateStation interaction logic:
                         *
                         * If the plate holds a completed recipe,
                         * or if the user has just pressed the 'shift' key,
                         * or if the user is not carrying any ingredients,
                         * pick up the top item on the plate.
                         *
                         * Else, if the user is carrying an ingredient,
                         * drop the top item onto the plate.
                         */
                        case "Sprites.PlateStation":
                            if(plateStation.getRecipeDone() != null ||
                                    (plateStation.getPlate().size() > 0 &&
                                        (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                                        Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ||
                                        controlledChef.getInHandsStackSize() == 0)))
                            {
                                controlledChef.pickUpItemFrom(tile);
                            }
                            else if (controlledChef.getInHandsIng() != null) {
                                controlledChef.dropItemOn(tile, controlledChef.getInHandsIng());
                            }
                            break;

                        case "Sprites.Pan":
                            if (controlledChef.getInHandsIng() != null) {
                                if (controlledChef.getInHandsIng().isPrepared() && controlledChef.getInHandsIng().cookTime > 0 && controlledChef.getInHandsIng().cookInOven == false) {
                                    controlledChef.setUserControlChef(false);
                                }
                            }
                            break;

                        case "Sprites.Oven":
                            if (controlledChef.getInHandsIng() != null) {
                                if(controlledChef.getInHandsIng().isPrepared() && controlledChef.getInHandsIng().cookTime > 0 && controlledChef.getInHandsIng().cookInOven)
                                {
                                    controlledChef.setUserControlChef(false);
                                }
                            }


                        case "Sprites.CompletedDishStation":
                            if (controlledChef.getInHandsRecipe() != null) {
                                for(int i = 0; i < Math.min(3, ordersArray.size()); i++) {
                                    if (controlledChef.getInHandsRecipe().getClass().equals(ordersArray.get(i).recipe.getClass())) {
                                        controlledChef.dropItemOn(tile);
                                        ordersArray.get(i).orderComplete = true;
                                        if (ordersArray.size() == 1) {
                                            scenarioComplete = Boolean.TRUE;
                                        }
                                        break;
                                    }
                                }
                            }
                            break;

                    }


                }
            }
        }

    /**
     * The update method updates the game elements, such as camera and characters,
     * based on a specified time interval "dt".
     * @param dt time interval for the update
    */
    public void update(float dt){
        handleInput(dt);

        gamecam.update();
        renderer.setView(gamecam);
        chef1.update(dt);
        chef2.update(dt);
        chef3.update(dt);
        world.step(1/60f, 6, 2);

    }

    /**
     * Creates the orders randomly and adds to an array, updates the HUD.
     */
    public void createOrder() {

        int recipeCount = RecipeManager.getCompleteRecipes().length;
        int randomNum = ThreadLocalRandom.current().nextInt(0, recipeCount);
        Order order;
        System.out.println("Creating order for index " + randomNum);

        order = new Order(
                RecipeManager.getCompleteRecipeAt(randomNum),
                RecipeManager.getRecipeTextureAt(randomNum),
                RecipeManager.getMinRecipeCounterAt(randomNum) * 2
        );
        ordersArray.add(order);
        hud.updateOrder(Boolean.FALSE, 1);
    }

    /**
     * Updates the orders as they are completed, or if the game scenario has been completed.
     */
    public void updateOrder(){
        if(scenarioComplete==Boolean.TRUE) {
            hud.updateScore(Boolean.TRUE, (6 - ordersArray.size()) * 35);
            hud.updateOrder(Boolean.TRUE, 0);
            return;
        }
        if(ordersArray.size() != 0) {
            for(int i = 0; i < ordersArray.size(); i++)
            {
                if (ordersArray.get(i).orderComplete) {
                    hud.updateScore(Boolean.FALSE, (6 - ordersArray.size()) * 35);
                    ordersArray.remove(i);
                    orderViewed = Math.max(0, orderViewed - 1);
                    hud.updateOrder(Boolean.FALSE, 6 - ordersArray.size());
                    // We can break here, as only one order will be completed in any one frame
                    break;
                }
            }
            ordersArray.get(orderViewed).create(Gdx.graphics.getDeltaTime(), trayX, trayY, game.batch);
        }
        OrderTickets.create(ordersArray, orderViewed, game.batch);
    }

    /**

     The render method updates the screen by calling the update method with the given delta time, and rendering the graphics of the game.

     It updates the HUD time, clears the screen, and renders the renderer and the hud.

     Additionally, it checks the state of the game and draws the ingredients, completed recipes, and notifications on the screen.

     @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta){
        update(delta);

        //Execute handleEvent each 1 second
        timeSeconds +=Gdx.graphics.getRawDeltaTime();
        timeSecondsCount += Gdx.graphics.getDeltaTime();

        //Adds an order every 15 seconds and if the order list isn't full
        if(orderTimeGap <= timeSecondsCount && orderCounter != customerTotal && ordersArray.size() <= 3){
            createOrder();
            orderCounter++;
            orderTimeGap = timeSecondsCount + 15;
        }

        //Check win condition
        if(customerCounter == customerTotal) {
            scenarioComplete = Boolean.TRUE;
        }

        float period = 1f;
        if(timeSeconds > period) {
            timeSeconds -= period;
            hud.updateTime(scenarioComplete);
        }

        Gdx.gl.glClear(1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        updateOrder();
        chef1.draw(game.batch);
        chef2.draw(game.batch);
        chef3.draw(game.batch);
        controlledChef.drawNotification(game.batch);

        // Render ingredients on the plate
        if (plateStation.getPlate().size() > 0){
            for(Object ing : plateStation.getPlate()){
                Ingredient ingNew = (Ingredient) ing;
                ingNew.create(plateStation.getX(), plateStation.getY(),game.batch);
            }
        } else if (plateStation.getRecipeDone() != null){
            Recipe recipeNew = plateStation.getRecipeDone();
            recipeNew.create(plateStation.getX(), plateStation.getY(), game.batch);
        }

        // Render ingredients on the worktops
        for(Worktop w : worktopStations)
        {
            Sprite item = w.getHeldItem();
            if(item != null)
            {
                if(item instanceof Ingredient) {
                    ((Ingredient)item).create(w.getX(), w.getY(), game.batch);
                }
                else if(item instanceof Recipe) {
                    ((Recipe)item).create(w.getX(), w.getY(), game.batch);
                }
            }
        }

        if (!chef1.getUserControlChef()) {
            if (chef1.getTouchingTile() != null && chef1.getInHandsIng() != null){
                if (chef1.getTouchingTile().getUserData() instanceof InteractiveTileObject){
                    chef1.displayIngStatic(game.batch);
                }
            }
        }
        if (!chef2.getUserControlChef()) {
            if (chef2.getTouchingTile() != null && chef2.getInHandsIng() != null) {
                if (chef2.getTouchingTile().getUserData() instanceof InteractiveTileObject) {
                    chef2.displayIngStatic(game.batch);
                }
            }
        }
        if (!chef3.getUserControlChef()) {
            if (chef3.getTouchingTile() != null && chef3.getInHandsIng() != null) {
                if (chef3.getTouchingTile().getUserData() instanceof InteractiveTileObject) {
                    chef3.displayIngStatic(game.batch);
                }
            }
        }
        if (chef1.previousInHandRecipe != null){
            chef1.displayIngDynamic(game.batch);
        }
        if (chef2.previousInHandRecipe != null){
            chef2.displayIngDynamic(game.batch);
        }
        if (chef3.previousInHandRecipe != null){
            chef3.displayIngDynamic(game.batch);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height){
        gameport.update(width, height);
    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){
        
    }

    @Override
    public void hide(){

    }

    @Override
    public void dispose(){
        map.dispose();
        renderer.dispose();
        world.dispose();
        hud.dispose();
    }
}
