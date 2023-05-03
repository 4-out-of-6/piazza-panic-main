package com.team13.piazzapanic;

import Sprites.Chef;
import Sprites.InteractiveTileObject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public final class PowerUpManager {

    private static boolean powerUpActive = false;
    private static boolean powerUpSpawned = false;
    private static Sprite powerUpSprite;
    private static Sprite powerUpUsedSprite;
    private static float powerUpAlpha;
    private static int powerUpId;
    private static float powerUpX;
    private static float powerUpY;
    private static boolean powerUpUsed = false;
    private static float powerUpUsedTimer = 0f;
    private static final float POWER_UP_INTERVAL = 25f;
    private static final float POWER_UP_SPAWN_TIME = 10f;
    private static final float POWER_UP_DURATION = 15f;

    private static Texture powerUpDisplayBase = new Texture("Power_Ups/display_base.png");
    private static Texture powerUpTimer = new Texture("Power_Ups/display_timer.png");
    private static Texture timerFill = new Texture("Timer/timer_foreground_full.png");
    private static Texture powerUpUsedTex = new Texture("Power_Ups/power_up_used.png");

    private static Texture[] powerUpTextures = new Texture[]{
            new Texture("Power_Ups/0.png"),
            new Texture("Power_Ups/1.png"),
            new Texture("Power_Ups/2.png"),
            new Texture("Power_Ups/3.png"),
            new Texture("Power_Ups/4.png")
    };

    private static Vector2[] spawnPoints = new Vector2[] {
            new Vector2(1,1),
            new Vector2(3,1),
            new Vector2(5,1),
            new Vector2(7,1),

            new Vector2(2,2),
            new Vector2(4,2),
            new Vector2(6,2),
            new Vector2(8,2),

            new Vector2(1,3),
            new Vector2(7,3),

            new Vector2(2,4),
            new Vector2(8,4),

            new Vector2(1,5),
            new Vector2(3,5),
            new Vector2(5,5),
            new Vector2(7,5),

            new Vector2(2,6),
            new Vector2(4,6),
            new Vector2(6,6),
            new Vector2(8,6),
    };

    private static float timer = POWER_UP_INTERVAL;


    public static void update(float dt, Chef controlledChef, PlayScreen screen) {
        timer -= dt;
        // Condition: Power-Up is not currently active and has not been spawned in
        if (powerUpSpawned == false && powerUpActive == false && timer <= 0) {
            powerUpId = ThreadLocalRandom.current().nextInt(0, powerUpTextures.length);
            Vector2 spawnPoint = spawnPoints[ThreadLocalRandom.current().nextInt(0, spawnPoints.length)];
            powerUpX = spawnPoint.x * (16 / MainGame.PPM);
            powerUpY = spawnPoint.y * (16 / MainGame.PPM);

            powerUpSprite = new Sprite(powerUpTextures[powerUpId]);
            timer = POWER_UP_SPAWN_TIME;
            powerUpAlpha = 0f;
            powerUpSpawned = true;
        }
        // Condition: a power-up is currently spawned in the world
        else if(powerUpSpawned)
        {
            double distance = Math.sqrt(
                    Math.pow(controlledChef.getX() - powerUpX, 2) +
                    Math.pow(controlledChef.getY() - powerUpY, 2)
            );

            if(distance <= (8 / MainGame.PPM))
            {
                powerUpSpawned = false;
                powerUpActive = true;
                powerUpSprite.setAlpha(1f);
                if(powerUpId == 4) {
                    gambler(screen);
                    timer = 2f;
                    usedPowerUp();
                }
                else {
                    timer = POWER_UP_DURATION;
                }
            }
            else if(timer <= 0)
            {
                powerUpSpawned = false;
                timer = POWER_UP_INTERVAL;
            }
            else if(timer <= 1) { powerUpAlpha -= dt; }
            else { powerUpAlpha = Math.min(1f, powerUpAlpha += dt); }
        }
        // Condition: power-up is currently active
        else if(powerUpActive)
        {
            if(timer <= 0)
            {
                powerUpActive = false;
                timer = POWER_UP_INTERVAL;
            }
        }

        if(powerUpUsed)
        {
            powerUpUsedTimer -= dt;
            if(powerUpUsedTimer <= 0)
            {
                powerUpUsed = false;
            }
        }
    }


    public static void draw(Batch batch)
    {
        if(powerUpSpawned)
        {
            powerUpSprite.setBounds(powerUpX, powerUpY, 16 / MainGame.PPM, 16 / MainGame.PPM);
            powerUpSprite.setAlpha(powerUpAlpha);
            powerUpSprite.draw(batch);
        }
        else if(powerUpActive)
        {
            Sprite displayBase = new Sprite(powerUpDisplayBase);
            Sprite displayTimer = new Sprite(powerUpTimer);
            Sprite timerBar = new Sprite(timerFill);

            float timerBarWidth = (14 * (timer / POWER_UP_DURATION)) / MainGame.PPM;
            displayBase.setBounds(0, 0, 16 / MainGame.PPM, 16 / MainGame.PPM);
            if(!powerUpUsed)
            {
                powerUpSprite.setBounds(0, 0, 16 / MainGame.PPM, 16 / MainGame.PPM);
            }
            else
            {
                powerUpUsedSprite = new Sprite(powerUpUsedTex);
                powerUpUsedSprite.setBounds(0, 0, 16 / MainGame.PPM, 16 / MainGame.PPM);
            }
            displayTimer.setBounds(0, 0, 16 / MainGame.PPM, 16 / MainGame.PPM);
            timerBar.setBounds(1 / MainGame.PPM, 1 / MainGame.PPM, timerBarWidth, 2 / MainGame.PPM);

            displayBase.draw(batch);
            if(powerUpUsed) { powerUpUsedSprite.draw(batch); }
            else { powerUpSprite.draw(batch); }
            displayTimer.draw(batch);
            timerBar.draw(batch);
        }
    }

    /**
     * Activates temporary 'used' state in the power up indicator
     */
    public static void usedPowerUp()
    {
        powerUpUsed = true;
        powerUpUsedTimer = 0.1f;
    }


    /**
     * Returns the multiplier for the Speed Increase power-up
     * @return A >1 multiplier if Speed Increase is active, 1 otherwise
     */
    public static float speedIncrease()
    {
        if(powerUpActive && powerUpId == 0) return 1.25f;
        return 1f;
    }

    /**
     * Returns the multiplier for the Time Slow power-up
     * @return A <1 multiplier if Time Slow is active, 1 otherwise
     */
    public static float timeSlow()
    {
        if(powerUpActive && powerUpId == 1) return 0.5f;
        return 1;
    }

    /**
     * Returns a flag indicating if the Instant Prepper power-up is active
     * @return True if active, false otherwise
     */
    public static boolean instantPrepper()
    {
        if(powerUpActive && powerUpId == 2) return true;
        return false;
    }

    /**
     * Returns the multiplier for the Cash Cow power-up
     * @return A >1 multiplier if Cash Cow is active, 1 otherwise
     */
    public static float cashCow()
    {
        if(powerUpActive && powerUpId == 3) return 2f;
        return 1f;
    }


    /**
     * Used the gambler power-up, which unlocks one of the locked stations
     */
    public static void gambler(PlayScreen screen)
    {
        if(powerUpActive && powerUpId == 4)
        {
            ArrayList<InteractiveTileObject> lockedStations = new ArrayList<>();
            for(InteractiveTileObject i : screen.preparationStations)
            {
                if(i.isLocked()) lockedStations.add(i);
            }

            if(lockedStations.size() > 0)
            {
                int randomNum = ThreadLocalRandom.current().nextInt(0, lockedStations.size());
                lockedStations.get(randomNum).setUnlocked();
            }
        }
    }

}
