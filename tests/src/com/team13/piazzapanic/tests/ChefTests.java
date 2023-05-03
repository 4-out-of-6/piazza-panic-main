package com.team13.piazzapanic.tests;

import Ingredients.Bun;
import Ingredients.Cheese;
import Recipe.CookedPizzaRecipe;
import Sprites.Chef;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;


@RunWith(GdxTestRunner.class)
public class ChefTests {

    /**
     * Test for FR_MOVEMENT
     */
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

    /**
     * Test for UR_COLLISION
     */
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

    /**
     * Test for FR_INGREDIENT_STACK
     */
    @Test
    public void testChefStack() {
        World world = new World(new Vector2(0, 0), true);
        Chef chef1 = new Chef(world, 0F, 0, false);

        // Assert that the initial stack is empty
        assertTrue(chef1.getInHandsStack().size() == 0);

        // Add an element to the stack and test
        chef1.pushToStack(new Bun(0, 0));
        assertTrue(chef1.getInHandsStackSize() == 1);
        assertTrue(chef1.getInHandsIng().getClass().getName().equals("Ingredients.Bun"));

        // Add a second element to the stack and test stack implementation
        chef1.pushToStack(new CookedPizzaRecipe());
        assertTrue(chef1.getInHandsStackSize() == 2);
        assertTrue(chef1.getInHandsRecipe().getClass().getName().equals("Recipe.CookedPizzaRecipe"));
    }

}