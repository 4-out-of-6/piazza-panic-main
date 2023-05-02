package com.team13.piazzapanic.tests;

import Sprites.Chef;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertTrue;


@RunWith(GdxTestRunner.class)
public class ChefTests {

    @Test
    public void testChefMove() {
        World world = new World(new Vector2(0, 0), true);
        Chef chef = new Chef(world, 31.5F, 65, false);

        //Change velocity of chef to move
        float initialY = chef.getY();
        chef.b2body.setLinearVelocity(0f, -0.5f);
        chef.update(0.0001f);
        //Check pos of chef if lower down
        assertTrue(initialY < chef.getY());
    }

    @Test
    public void testChefCollision() {
        World world = new World(new Vector2(0, 0), true);
        Chef chef1 = new Chef(world, 31.5F, 65, false);
        Chef chef2 = new Chef(world, 31.5F, 65, false);
        chef1.chefsColliding();
        chef2.chefsColliding();
        //Check game knows chefs are colliding when the function is called
        assertTrue(chef1.chefOnChefCollision && chef2.chefOnChefCollision);
    }

    @Test
    public void testChefAssets() {
        assertTrue(Gdx.files.internal("Chef/Chef_holding_ingredient.png").exists());
        assertTrue(Gdx.files.internal("Chef/Chef_normal.png").exists());
        assertTrue(Gdx.files.internal("Chef/chefIdentifier.png").exists());
    }


    @Test
    public void testChefSwitch() {
        Robot robot =  new Robot();
        World world = new World(new Vector2(0, 0), true);
        Chef chef1 = new Chef(world, 0, 10, false);
        Chef chef2 = new Chef(world, 10, 0, false);
        boolean chef1Free = chef1.getUserControlChef();
        boolean chef2Free = chef2.getUserControlChef();


        controlledChef = chef1;
        robot.keyPress(KeyEvent.VK_R);   // simulated R press and release
        robot.keyRelease(KeyEvent.VK_R);
        assertTrue(controlledChef.equals(chef2));  // checking the chef has switched

        controlledChef.setUserControlChef(false); //locking controlled chef (chef2)
        assertTrue(controlledChef.equals(chef1)); //checking control has switched back to chef1
    }

    @Test
    public void testUserInteract() { //trying to test that if a chef is standing close enough to a unit, pressing interact will start an action in the correct circumstance
        Robot robot = new Robot();
        World world = new World(new Vector2(0, 0), true);
        Chef chef1 = new Chef(world, 0, 10, false);
        //put chef next to interactable station which locks the controlled chef

        robot.keyPress(KeyEvent.VK_E);
        assertTrue(chef1.isLocked());  //interact with station that locks chef, check chef is locked
    }
}