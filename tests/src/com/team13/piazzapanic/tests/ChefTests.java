package com.team13.piazzapanic.tests;

import Sprites.Chef;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.Test;
import org.junit.runner.RunWith;
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
}