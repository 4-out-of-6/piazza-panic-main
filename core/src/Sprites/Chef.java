package Sprites;

import Ingredients.*;
import Recipe.Recipe;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.team13.piazzapanic.MainGame;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Chef class extends {@link Sprite} and represents a chef in the game.
 * It has fields for the world it exists in, a Box2D body, the initial X and Y
 * positions, a wait timer, collision flag, various textures for different skins,
 * state (UP, DOWN, LEFT, RIGHT), skin needed, fixture of what it is touching, ingredient
 * and recipe in hand, control flag, circle sprite, chef notification X, Y, width and height,
 * and completed dish station.
 */

public class Chef extends Sprite {
    public World world;
    public Body b2body;

    private final float initialX;
    private final float initialY;


    public Vector2 startVector;
    private float waitTimer;


    private float putDownWaitTimer;
    public boolean chefOnChefCollision;
    private final Texture normalChef;

    /**
     * Updated from old code.
     * Old implementation required a different chef texture for each ingredient the chef
     * could be holding. Ingredients are now instead rendered on top of a base chef texture using
     * an overriden 'draw' method (extended from the Sprite class).
     */
    private final Texture ingredientChef;



    public enum State {UP, DOWN, LEFT, RIGHT}

    public State currentState;
    private TextureRegion currentSkin;

    private Texture skinNeeded;

    private Fixture whatTouching;

    private ArrayList<Sprite> inHandsStack;

    private Boolean userControlChef;

    private final Sprite circleSprite;

    private float notificationX;
    private float notificationY;
    private float notificationWidth;
    private float notificationHeight;

    private CompletedDishStation completedStation;

    public int nextOrderAppearTime;
    public Recipe previousInHandRecipe;

    private boolean locked;
    private boolean unlocking;
    private float fadeTime;
    private boolean failingStep;

    /**
     * Chef class constructor that initializes all the fields
     * @param world the world the chef exists in
     * @param startX starting X position
     * @param startY starting Y position
     */

    public Chef(World world, float startX, float startY, boolean locked) {
        initialX = startX / MainGame.PPM;
        initialY = startY / MainGame.PPM;

        normalChef = new Texture("Chef/Chef_normal.png");
        ingredientChef = new Texture("Chef/Chef_holding_ingredient.png");


        skinNeeded = normalChef;

        this.world = world;
        currentState = State.DOWN;

        this.locked = locked;
        unlocking = false;

        if(!locked) { defineChef(); }

        float chefWidth = 13 / MainGame.PPM;
        float chefHeight = 20 / MainGame.PPM;
        setBounds(0, 0, chefWidth, chefHeight);
        chefOnChefCollision = false;
        waitTimer = 0;
        putDownWaitTimer = 0;
        startVector = new Vector2(0, 0);
        whatTouching = null;
        inHandsStack = new ArrayList<>();
        userControlChef = true;
        Texture circleTexture = new Texture("Chef/chefIdentifier.png");
        circleSprite = new Sprite(circleTexture);
        nextOrderAppearTime = 3;
        completedStation = null;

        failingStep = false;
    }


    /**
     * Overriden method (extended from Sprite class).
     * Draws the chef texture to the given batch. Draws their
     * held ingredient on top.
     * @param batch The sprite batch to be drawn to.
     */
    @Override
    public void draw (Batch batch) {

        if(unlocking)
        {
            fadeTime -= Gdx.graphics.getDeltaTime();
            if(fadeTime <= 0)
            {
                locked = false;
                setAlpha(1);
            }
            else
            {
                setAlpha(1 - fadeTime);
            }
        }
        else if(isLocked()) return;

        // Replication of static final values in Sprite class
        int vertexSize = 2 + 1 + 2;
        int spriteSize = 4 * vertexSize;

        float ingX = getX() + 0.07f;
        float ingY = getY() + 0.175f;

        // Draw chef
        batch.draw(getTexture(), getVertices(), 0, spriteSize);

        // Draw chef ingredient / recipe
        if(skinNeeded != normalChef)
        {
            if(getInHandsIng() != null)
            {
                getInHandsIng().create(ingX, ingY, (SpriteBatch) batch);
            }
            else if(getInHandsRecipe() != null)
            {
                getInHandsRecipe().create(ingX, ingY, (SpriteBatch) batch);
            }
        }
    }


    /**
     * Update the position and region of the chef and set the notification position based on the chef's current state.
     *
     * @param dt The delta time.
     */
    public void update(float dt) {
        if(isLocked() && !unlocking) return;
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        currentSkin = getSkin(dt);
        setRegion(currentSkin);

        Ingredient inHandsIng = getInHandsIng();

        switch (currentState) {
            case UP:
                if (getInHandsStackSize() == 0) {
                    notificationX = b2body.getPosition().x - (1.75f / MainGame.PPM);
                    notificationY = b2body.getPosition().y - (7.7f / MainGame.PPM);
                } else {
                    notificationX = b2body.getPosition().x - (0.67f / MainGame.PPM);
                    notificationY = b2body.getPosition().y - (7.2f / MainGame.PPM);
                }
                break;
            case DOWN:
                if (getInHandsStackSize() == 0) {
                    notificationX = b2body.getPosition().x + (0.95f / MainGame.PPM);
                    notificationY = b2body.getPosition().y - (5.015f / MainGame.PPM);
                } else {
                    notificationX = b2body.getPosition().x + (0.55f / MainGame.PPM);
                    notificationY = b2body.getPosition().y - (5.3f / MainGame.PPM);
                }
                break;
            case LEFT:
                if (getInHandsStackSize() == 0) {
                    notificationX = b2body.getPosition().x;
                    notificationY = b2body.getPosition().y - (5.015f / MainGame.PPM);
                } else {
                    notificationX = b2body.getPosition().x - (1.92f / MainGame.PPM);
                    notificationY = b2body.getPosition().y - (4.6f / MainGame.PPM);
                }
                break;
            case RIGHT:
                if (getInHandsStackSize() == 0) {
                    notificationX = b2body.getPosition().x + (0.5f / MainGame.PPM);
                    notificationY = b2body.getPosition().y - (5.015f / MainGame.PPM);
                } else {
                    notificationX = b2body.getPosition().x + (0.17f / MainGame.PPM);
                    notificationY = b2body.getPosition().y - (4.63f / MainGame.PPM);
                }
                break;
        }


        if (!userControlChef && chefOnChefCollision) {
            waitTimer += dt;
            b2body.setLinearVelocity(new Vector2(startVector.x * -1, startVector.y * -1));
            if (waitTimer > 0.3f) {
                b2body.setLinearVelocity(new Vector2(0, 0));
                chefOnChefCollision = false;
                userControlChef = true;
                waitTimer = 0;
                if (inHandsIng != null) {
                    setChefSkin(inHandsIng);
                }
            }
        } else if (!userControlChef && getInHandsIng().prepareTime > 0) {
            waitTimer += dt;
            if (waitTimer > inHandsIng.prepareTime) {
                inHandsIng.prepareTime = 0;
                returnIngToChef(false);
            }
        } else if (!userControlChef && !chefOnChefCollision && getInHandsIng().isPrepared() && inHandsIng.cookTime > 0) {
            waitTimer += dt;
            if (waitTimer > inHandsIng.cookTime) {
                inHandsIng.setCooked();
                failingStep = true;
                userControlChef = true;
            }
        } else if(failingStep) {
            if(inHandsIng.hasFailed() == false)
            {
                inHandsIng.failTimer += dt;
                if(inHandsIng.failTimer > 3f) inHandsIng.setFailed();
            }
            else
            {
                inHandsIng.cookTime = 0;
                returnIngToChef(true);
            }
        }
    }

    /**
     * Returns the chef's held ingredient from a station back into the stack.
     * @param cookedNotCut determines whether the ingredient is being returned from a pan/oven (true) or chopping board (false)
     */
    void returnIngToChef(boolean cookedNotCut)
    {
        if(getInHandsIng().getRecipeOverride() != null && getInHandsIng().hasFailed() == false)
        {
            Recipe temp = getInHandsIng().getRecipeOverride();
            pushToStack(null);
            pushToStack(temp);
        }
        else
        {
            if(cookedNotCut) getInHandsIng().cookTime = 0;
            else getInHandsIng().setPrepared();
        }
        userControlChef = true;
        failingStep = false;
        setChefSkin(peekInHandsStack());
        waitTimer = 0;
    }

    /**
     * This method sets the bounds for the notification based on the given direction.
     * @param direction - A string representing the direction of the notification.
     *                   Can be "Left", "Right", "Up", or "Down".
     */

    public void notificationSetBounds(String direction) {
        switch (direction) {
            case "Left":
            case "Right":
                notificationWidth = 1.5f / MainGame.PPM;
                notificationHeight = 1.5f / MainGame.PPM;
                break;
            case "Up":
                notificationWidth = 4 / MainGame.PPM;
                notificationHeight = 4 / MainGame.PPM;
                break;
            case "Down":
                notificationWidth = 2 / MainGame.PPM;
                notificationHeight = 2 / MainGame.PPM;
                break;
        }
    }

    /**
     Draws a notification to help the user understand what chef they are controlling.
     The notification is a sprite that looks like at "C" on the controlled chef.
     @param batch The sprite batch that the notification should be drawn with.
     */
    public void drawNotification(SpriteBatch batch) {
        if (this.getUserControlChef()) {
            circleSprite.setBounds(notificationX, notificationY, notificationWidth, notificationHeight);
            circleSprite.draw(batch);
        }
    }

    /**
     * Get the texture region for the current state of the player.
     *
     * @param dt the time difference between this and the last frame
     * @return the texture region for the player's current state
     */

    private TextureRegion getSkin(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case UP:
                region = new TextureRegion(skinNeeded, 0, 0, 33, 46);
                break;
            case DOWN:
                region = new TextureRegion(skinNeeded, 33, 0, 33, 46);
                break;
            case LEFT:
                region = new TextureRegion(skinNeeded, 64, 0, 33, 46);
                break;
            case RIGHT:
                region = new TextureRegion(skinNeeded, 96, 0, 33, 46);
                break;
            default:
                region = currentSkin;
        }
        return region;
    }


    /**
     Returns the current state of the player based on the controlled chefs velocity.
     @return current state of the player - UP, DOWN, LEFT, or RIGHT
     */
    public State getState() {
        if (b2body.getLinearVelocity().y > 0)
            return State.UP;
        if (b2body.getLinearVelocity().y < 0)
            return State.DOWN;
        if (b2body.getLinearVelocity().x > 0)
            return State.RIGHT;
        if (b2body.getLinearVelocity().x < 0)
            return State.LEFT;
        else
            return currentState;
    }

    /**
     * Define the body and fixture of the chef object.
     *
     * This method creates a dynamic body definition and sets its position with the `initialX` and `initialY`
     * variables, then creates the body in the physics world. A fixture definition is also created and a
     * circle shape is set with a radius of `4.5f / MainGame.PPM` and a position shifted by `(0.5f / MainGame.PPM)`
     * in the x-axis and `-(5.5f / MainGame.PPM)` in the y-axis. The created fixture is then set as the user data
     * of the chef object.
     */

    public void defineChef() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(initialX, initialY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(4.5f / MainGame.PPM);
        shape.setPosition(new Vector2(shape.getPosition().x + (0.5f / MainGame.PPM), shape.getPosition().y - (5.5f / MainGame.PPM)));


        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }


    /**
     * Method to set the skin of the chef character based on the item the chef is holding.
     *
     * @param item the item that chef is holding
     *
     * The skin is set based on the following cases:
     * - if item is null, then the skin is set to normalChef
     * - else then the skin is set to ingredientChef
     */

    public void setChefSkin(Object item) {
        if (item == null) {
            skinNeeded = normalChef;
        }
        else
        {
            skinNeeded = ingredientChef;
        }
    }

    /**
     * Method to display the ingredient on the specific interactive tile objects (ChoppingBoard/Pan/Worktop/Oven)
     * @param batch the SpriteBatch used to render the texture.
     */

    public void displayIngStatic(SpriteBatch batch) {
        Gdx.app.log("", getInHandsIng().toString());
        if ((whatTouching != null && !chefOnChefCollision)) {
            InteractiveTileObject tile = (InteractiveTileObject) whatTouching.getUserData();
            if (tile instanceof ChoppingBoard) {
                ChoppingBoard tileNew = (ChoppingBoard) tile;
                getInHandsIng().create(tileNew.getX() - (0.5f / MainGame.PPM), tileNew.getY() - (0.2f / MainGame.PPM), batch);
                setChefSkin(null);
            } else if (tile instanceof Pan) {
                Pan tileNew = (Pan) tile;
                getInHandsIng().create(tileNew.getX(), tileNew.getY() - (0.01f / MainGame.PPM), batch);
                setChefSkin(null);
            }
            else if(tile instanceof Oven) {
                Oven tileNew = (Oven) tile;
                getInHandsIng().create(tileNew.getX(), tileNew.getY() - (0.01f / MainGame.PPM), batch);
                setChefSkin(null);
            }
        }
    }

    /**
     * The method creates an instance of the recipe and sets its position on the completed station coordinates.
     * The method also implements a timer for each ingredient which gets removed from the screen after a certain amount of time.
     *
     * @param batch The batch used for drawing the sprite on the screen
     */

    public void displayIngDynamic(SpriteBatch batch){
        putDownWaitTimer += 1/60f;
        previousInHandRecipe.create(completedStation.getX(), completedStation.getY() - (0.01f / MainGame.PPM), batch);
        if (putDownWaitTimer > nextOrderAppearTime) {
            previousInHandRecipe = null;
            putDownWaitTimer = 0;
        }
    }

    /**

      * This method updates the state of the chef when it is in a collision with another chef.
      * The method sets the userControlChef to false, meaning the user cannot control the chef while it's in collision.
      * It also sets the chefOnChefCollision to true, indicating that the chef is in collision with another chef.
      * Finally, it calls the setStartVector method to update the position of the chef.
     */
        public void chefsColliding () {
            userControlChef = false;
            chefOnChefCollision = true;
            setStartVector();
        }

    /**
     * Set the starting velocity vector of the chef
     * when the chef collides with another chef
     *
     */
    public void setStartVector () {
        startVector = new Vector2(b2body.getLinearVelocity().x, b2body.getLinearVelocity().y);
    }

    /**
     * Set the touching tile fixture
     *
     * @param obj fixture that the chef is touching
     */
    public void setTouchingTile (Fixture obj){
        this.whatTouching = obj;
    }

    /**
     * Get the fixture that the chef is touching
     *
     * @return the fixture that the chef is touching
     */
    public Fixture getTouchingTile () {
        if (whatTouching == null) {
            return null;
        } else {
            return whatTouching;
        }
    }


    /**
     * Get the size of the in-hand stack
     *
     * @return the size of the in-hand stack
     */
    public int getInHandsStackSize () {
        return inHandsStack.size();
    }

    /**
     * Returns the chef's stack
     * @return an ArrayList of recipe/ingredients.
     */
    public ArrayList<Sprite> getInHandsStack() {
        return inHandsStack;
    }


    /**
     * Returns the top item in the chef's stack
     * @return the top element of the stack, in Object form.
     */
    public Object peekInHandsStack() {
        if(getInHandsStackSize() > 0)
        {
            return inHandsStack.get(0);
        }
        return null;
    }


    /**
     * Get the ingredient that the chef is holding
     *
     * @return the ingredient that the chef is holding
     */
    public Ingredient getInHandsIng () {
        Object stackTop = peekInHandsStack();
        if(stackTop instanceof Ingredient)
        {
            return (Ingredient)(stackTop);
        }
        return null;
    }

    /**
     * Get the recipe that the chef is holding
     *
     * @return the recipe that the chef is holding
     */
    public Recipe getInHandsRecipe () {
        Object stackTop = peekInHandsStack();
        if(stackTop instanceof Recipe)
        {
            return (Recipe)(stackTop);
        }
        return null;
    }

    /**
     * Handles the addition/removal of items to the stack.
     * @param item the element to add to the stack. If null,
     * the top item is discarded from the stack, and nothing is
     * added.
     */
    public void pushToStack(Sprite item)
    {
        System.out.println("Item " + item + " with stack size " + getInHandsStackSize());
        if(item == null && getInHandsStackSize() > 0)
        {
            System.out.println("Removing " + inHandsStack.get(0) + " from stack...");
            inHandsStack.remove(0);
        }
        else if(item != null)
        {
            System.out.println("Pushing " + item + " to stack...");
            inHandsStack.add(0, item);
        }
        setChefSkin(peekInHandsStack());
        System.out.println(inHandsStack.toString() + "\n");
    }

    /**
     * Set the chef's control by the user
     *
     * @param value whether the chef is controlled by the user
     */
    public void setUserControlChef ( boolean value){
        userControlChef = value;

    }

    /**

     * Returns a boolean value indicating whether the chef is under user control.
     * If not specified, returns false.
     *
     * @return userControlChef The boolean value indicating chef's control.
     */
    public boolean getUserControlChef () {
            return Objects.requireNonNullElse(userControlChef, false);
        }


    /**
      * Drops the given ingredient on a plate station.
      * @param station The plate station to drop the ingredient on.
      * @param ing The ingredient to be dropped.
     */

    public void dropItemOn (InteractiveTileObject station, Ingredient ing){
        if (station instanceof PlateStation) {
                ((PlateStation) station).dropItem(ing);
        }
        pushToStack(null);
    }

    /**
     * Drops the in-hand recipe on a completed dish station and saves the previous in-hand recipe.
     *
     * @param station The completed dish station to drop the recipe on.
     */
        public void dropItemOn (InteractiveTileObject station){
            if (station instanceof CompletedDishStation) {
                previousInHandRecipe = getInHandsRecipe();
                completedStation = (CompletedDishStation) station;
            }
            pushToStack(null);
        }

    /**
     * Picks up an item from a plate station and sets it as in-hand ingredient or recipe.
     *
     * @param station The plate station to pick up the item from.
     */
    public void pickUpItemFrom(InteractiveTileObject station){
        if (station instanceof PlateStation) {
            PlateStation pStation = (PlateStation) station;
            Object item = pStation.pickUpItem();
            if (item instanceof Ingredient) {
                pushToStack((Ingredient) item);
            } else if (item instanceof Recipe) {
                pushToStack(((Recipe) item));
            }
            setChefSkin(item);
        }
    }

    /**
     * Returns a flaf indicating whether the chef is currently locked
     * @return
     */
    public boolean isLocked() { return locked; }


    /**
     * Sets the chef to unlocked mode
     */
    public void setUnlocked() {
        defineChef();
        fadeTime = 1;
        unlocking = true;
    }

    /**
     * Cancel the failing of the current preparation step. Returns the ingredient to the chef's hand.
     */
    public void preventStepFailure()
    {
        failingStep = false;
        returnIngToChef(true);
    }

    /**
     * Returns the flag indicating whether the chef is currently failing a preparation step.
     * @return Boolean value determining whether the current step is being failed
     */
    public boolean isFailingStep() { return failingStep; }

    /**
     * Replaces the chef's current stack with the provided new one
     */
    public void replaceStack(ArrayList<Sprite> stack) {
        inHandsStack = (ArrayList<Sprite>)stack.clone();
        setChefSkin(peekInHandsStack());
    }
}



