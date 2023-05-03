package com.team13.piazzapanic.tests;

import Sprites.Chef;
import Sprites.Oven;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.team13.piazzapanic.HUD;
import com.team13.piazzapanic.PlayScreen;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertTrue;


@RunWith(GdxTestRunner.class)
public class ScoreTests {

    /**
     * Test for FR_MONEY
     */
    @Test
    public void testScoreAddition() {
        HUD hud = new HUD(null);

        // Update score with 3/4 the time remaining
        hud.updateScore(Boolean.FALSE, 100, 0.75f);
        int score1 = hud.getScore();

        hud.setScore(0);
        // Update score with 1/2 the time remaining
        hud.updateScore(Boolean.FALSE, 100, 0.5f);
        int score2 = hud.getScore();

        assertTrue(score1 > score2);
    }

    /**
     * Test for FR_SPEND_MONEY
     */
    @Test
    public void testScoreSpending()
    {
        World world = new World(new Vector2(0, 0), true);
        HUD hud = new HUD(null);
        Oven oven = new Oven(world, null, new BodyDef(), new Rectangle(0, 0, 1, 1));

        // Lock the oven
        oven.setLocked();
        assertTrue(oven.isLocked());

        // Attempt to unlock the oven
        PlayScreen.tryUnlock(hud, oven, false, false);
        assertTrue(oven.isLocked());

        // Update HUD score, then try again
        hud.setScore(100);
        PlayScreen.tryUnlock(hud, oven, false, false);
        assertTrue(oven.isLocked() == false);
    }

}