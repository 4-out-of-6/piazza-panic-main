package com.team13.piazzapanic;

import Ingredients.Ingredient;
import Recipe.Recipe;
import Sprites.*;
import Recipe.Order;
import Recipe.RecipeManager;
import Recipe.OrderTickets;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;
import Recipe.ReputationPoints;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
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
    public final HUD hud;

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    private final World world;
    public final Chef chef1;
    public final Chef chef2;
    public final Chef chef3;
    private int chefsUnlocked;

    private Chef controlledChef;

    public ArrayList<Order> ordersArray;

    public PlateStation plateStation;
    public NewChefStation newChefStation;
    public ArrayList<Worktop> worktopStations = new ArrayList<>();
    public ArrayList<InteractiveTileObject> preparationStations = new ArrayList<>();


    public Boolean scenarioComplete = false;
    public Boolean scenarioFailed = false;
    public int customerTotal;
    public int orderCounter = 0;
    public int customerCounter = 0;

    public static float trayX;
    public static float trayY;

    public float timeSeconds = 0f;

    public double timeSecondsCount = 0f;

    private int orderViewed = 0;
    public double orderTimeGap = 0;

    public int reputationPoints = 3;
    public double interval = 30;
    public String difficulty;
    public float difficultyModerator;
    private float saveIconTimer;
    private Sprite saveIcon = new Sprite(new Texture("save_icon.png"));

    /**
     * PlayScreen constructor initializes the game instance, sets initial conditions for scenarioComplete and createdOrder,
     * creates and initializes game camera and viewport,
     * creates and initializes HUD and orders hud, loads and initializes the map,
     * creates and initializes world, creates and initializes chefs and sets them, sets contact listener for world, and initializes ordersArray.
     * @param game The MainGame instance that the PlayScreen will be a part of.
     */

    public PlayScreen(MainGame game){
        this.game = game;
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

        chef1 = new Chef(this.world, 31.5F,65, false);
        chef2 = new Chef(this.world, 71.75f,16, true);
        chef3 = new Chef(this.world, 71.75f, 16, true);
        chefsUnlocked = 1;
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

        // Check for save key pressed
        if(Gdx.input.isKeyJustPressed(Input.Keys.P))
        {
            try {
                SaveManager.saveMidGameState(this, hud);
                System.out.println("SAVE SUCCESSFUL");
                saveIconTimer = 2f;
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
            }
        }

        boolean c1Free = chef1.getUserControlChef();
        boolean c2Free = chef2.getUserControlChef();
        boolean c3Free = chef3.getUserControlChef();
        boolean c2Unlocked = !chef2.isLocked();
        boolean c3Unlocked = !chef3.isLocked();

        // If the R key is pressed, or when a chef is unavailable, switch to the next one
        if(Gdx.input.isKeyJustPressed(Input.Keys.R) || controlledChef.getUserControlChef() == false)
        {
            controlledChef.b2body.setLinearVelocity(0, 0);
            if(controlledChef.equals(chef1))
            {
                if(c2Free && c2Unlocked) { controlledChef = chef2; }
                else if(c3Free & c3Unlocked) { controlledChef = chef3; }
            }
            else if(controlledChef.equals(chef2))
            {
                if(c3Free & c3Unlocked) { controlledChef = chef3; }
                else if(c1Free) { controlledChef = chef1; }
            }
            else if(controlledChef.equals(chef3))
            {
                if(c1Free) { controlledChef = chef1; }
                else if(c2Free) { controlledChef = chef2; }
            }
        }

        if (controlledChef.getUserControlChef()) {
                float xVelocity = 0;
                float yVelocity = 0;

                if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    yVelocity += 0.5f * PowerUpManager.speedIncrease();
                }
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    xVelocity -= 0.5f * PowerUpManager.speedIncrease();
                }
                if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    yVelocity -= 0.5f * PowerUpManager.speedIncrease();
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    xVelocity += 0.5f * PowerUpManager.speedIncrease();
                }
                controlledChef.b2body.setLinearVelocity(xVelocity, yVelocity);

                // If the chef is being moved and is currently failing a preparation step, cancel the failure
                if(controlledChef.isFailingStep() && (xVelocity != 0 || yVelocity != 0))
                {
                    controlledChef.preventStepFailure();
                }

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
            if(ordersArray.size() > 1) { orderViewed = 1; }
            else { orderViewed = 0; }
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            if(ordersArray.size() > 2) { orderViewed = 2; }
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
                            if(tile.isLocked()) { tryUnlock(tile); }
                            else if (controlledChef.getInHandsIng() != null) {
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
                            if(tile.isLocked()) { tryUnlock(tile); }
                            else if (controlledChef.getInHandsIng() != null) {
                                if (controlledChef.getInHandsIng().isPrepared() && controlledChef.getInHandsIng().cookTime > 0 && controlledChef.getInHandsIng().cookInOven == false) {
                                    controlledChef.setUserControlChef(false);
                                }
                            }
                            break;

                        case "Sprites.Oven":
                            if(tile.isLocked()) { tryUnlock(tile); }
                            else if (controlledChef.getInHandsIng() != null) {
                                if(controlledChef.getInHandsIng().isPrepared() && controlledChef.getInHandsIng().cookTime > 0 && controlledChef.getInHandsIng().cookInOven)
                                {
                                    controlledChef.setUserControlChef(false);
                                }
                            }
                            break;

                        case "Sprites.NewChefStation":
                            if(chefsUnlocked == 1)
                            {
                                if(hud.getScore() >= 200 && !scenarioFailed && !scenarioComplete)
                                {
                                    hud.updateScore(-200);
                                    chefsUnlocked++;
                                    controlledChef.setY(controlledChef.getY() + (16 / MainGame.PPM));
                                    chef2.setUnlocked();
                                }
                            }
                            else if(chefsUnlocked == 2)
                            {
                                if(hud.getScore() >= 250 && !scenarioFailed && !scenarioComplete)
                                {
                                    hud.updateScore(-250);
                                    chefsUnlocked++;
                                    chef3.setUnlocked();
                                }
                            }
                            break;


                        case "Sprites.CompletedDishStation":
                            if (controlledChef.getInHandsRecipe() != null) {
                                for(int i = 0; i < Math.min(3, ordersArray.size()); i++) {
                                    System.out.println("Completed dish station checking " + controlledChef.getInHandsRecipe().getClass() + " against " + ordersArray.get(i).recipe.getClass());
                                    if (controlledChef.getInHandsRecipe().getClass().equals(ordersArray.get(i).recipe.getClass())) {
                                        controlledChef.dropItemOn(tile);
                                        ordersArray.get(i).orderComplete = true;
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
        PowerUpManager.update(dt, controlledChef, this);
        world.step(1/60f, 6, 2);

    }

    /**
     * Creates the orders randomly and adds to an array, updates the HUD.
     */
    public void createOrder() {

            int recipeCount = RecipeManager.getCompleteRecipes().length;
            int randomNum = ThreadLocalRandom.current().nextInt(0, recipeCount);
            Order order;

            order = new Order(
                    RecipeManager.getCompleteRecipeAt(randomNum),
                    RecipeManager.getRecipeTextureAt(randomNum),
                    RecipeManager.getMinRecipeCounterAt(randomNum) * difficultyModerator
            );
            ordersArray.add(order);
            hud.updateOrder(scenarioComplete, orderCounter);
    }

    /**
     * Updates the orders as they are completed, or if the game scenario has been completed.
     */
    public void updateOrder(){
        if(scenarioFailed == true) {
            hud.createFailState(customerTotal == -1);
            SaveManager.saveEndGameState(this);
            return;
        }
        else if(scenarioComplete==Boolean.TRUE && customerTotal != 0) {
            hud.updateOrder(Boolean.TRUE, 0);
            SaveManager.saveEndGameState(this);
            return;
        }
        if(ordersArray.size() != 0)
        {
            for(int i = 0; i < ordersArray.size(); i++)
            {
                if(ordersArray.get(i).orderFailed) {
                    System.out.println("ORDER " + i + " FAILED");
                    ordersArray.remove(i);
                    orderViewed = Math.max(0, orderViewed - 1);
                    reputationPoints--;
                    if(reputationPoints == 0)
                    {
                        System.out.println("SCENARIO FAILED");
                        scenarioFailed = true;
                        if(orderCounter == -1) { hud.setScore(0); }
                        ordersArray.clear();
                        orderViewed = 0;
                        return;
                    }
                    else
                    {
                        orderCounter++;
                        customerTotal++;
                    }
                }
                else if (ordersArray.get(i).orderComplete) {
                    float timeLeft = ordersArray.get(i).getCountdownTimer() / ordersArray.get(i).getInitialTimer();
                    hud.updateScore(Boolean.FALSE, 150, timeLeft);
                    ordersArray.remove(i);
                    orderCounter++;
                    orderViewed = Math.max(0, orderViewed - 1);
                    hud.updateOrder(Boolean.FALSE, orderCounter);
                    // We can break here, as only one order will be completed in any one frame
                    break;
                }
            }
            if(ordersArray.size() > 0) { ordersArray.get(orderViewed).create(Gdx.graphics.getDeltaTime(), trayX, trayY, game.batch); }
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

        //Adds an order every 30 seconds (decreasing) and if the order list isn't full
        if(orderTimeGap <= timeSecondsCount && customerCounter != customerTotal && ordersArray.size() <= 3 && !scenarioComplete && !scenarioFailed){
            createOrder();
            customerCounter++;
            //1 in 10 chance to have 2 orders arrive
            if (ThreadLocalRandom.current().nextInt(10) == 1 && ordersArray.size() <= 2 && customerCounter != customerTotal) {
                createOrder();
                customerCounter++;
            }
            orderTimeGap = timeSecondsCount + interval;
            interval = interval * 0.9;
        }

        //Check win condition
        if(orderCounter == customerTotal){
            scenarioComplete = Boolean.TRUE;
        }

        if((scenarioComplete || scenarioFailed) && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            Gdx.app.exit();
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

        // Render locked icon on locked stations
        for(InteractiveTileObject s : preparationStations)
        {
            if(s.isLocked()) {
                LockedState.create(s.getX(), s.getY(), controlledChef, game.batch);
            }
        }
        // Render the chef buying options
        if(chefsUnlocked < 3)
        {
            BuyChefState.create(newChefStation.getX(), newChefStation.getY(), controlledChef, chefsUnlocked, game.batch);
        }

        updateOrder();
        ReputationPoints.create(reputationPoints, game.batch);
        PowerUpManager.draw(game.batch);
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

        if (!chef1.getUserControlChef() || chef1.isFailingStep()) {
            if (chef1.getTouchingTile() != null && chef1.getInHandsIng() != null){
                if (chef1.getTouchingTile().getUserData() instanceof InteractiveTileObject){
                    chef1.displayIngStatic(game.batch);
                }
            }
        }
        if (!chef2.getUserControlChef() || chef2.isFailingStep()) {
            if (chef2.getTouchingTile() != null && chef2.getInHandsIng() != null) {
                if (chef2.getTouchingTile().getUserData() instanceof InteractiveTileObject) {
                    chef2.displayIngStatic(game.batch);
                }
            }
        }
        if (!chef3.getUserControlChef() || chef3.isFailingStep()) {
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

        // Render save icon
        if(saveIconTimer > 0)
        {
            saveIconTimer -= delta;
            saveIcon.setBounds(144 / MainGame.PPM, 0, 16 / MainGame.PPM, 16 / MainGame.PPM);
            saveIcon.setAlpha((float)(0.75f + (Math.sin(saveIconTimer * 10) / 4f)));
            saveIcon.draw(game.batch);
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


    /**
     * Tries to unlock the given tile
     * @param tile the InteractiveTileObject to try unlocking.
     */
    public static void tryUnlock(HUD hud, InteractiveTileObject tile, Boolean scenarioComplete, Boolean scenarioFailed)
    {
        if(hud.getScore() >= 100 && !scenarioFailed && !scenarioComplete)
        {
            hud.updateScore(-100);
            tile.setUnlocked();
        }
    }

    void tryUnlock(InteractiveTileObject tile)
    {
        tryUnlock(hud, tile, scenarioComplete, scenarioFailed);
    }
}
