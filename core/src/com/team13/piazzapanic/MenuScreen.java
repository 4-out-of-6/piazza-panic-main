package com.team13.piazzapanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * This class implements the `Screen` interface and represents the start screen of the game.
 */
public class MenuScreen implements Screen {
    private final MainGame game;
    private final Texture backgroundImage;
    private final Sprite backgroundSprite;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private BitmapFont font = new BitmapFont();
    private BitmapFont font2 = new BitmapFont();
    private int customerNumber = 5;
    private boolean difficultySelected = false;
    private String[] difficultyArray = {"Easy","Medium","Hard"};


    public MenuScreen(MainGame game) {
        this.game = game;
        backgroundImage = new Texture("levelSelectImage.png");
        backgroundSprite = new Sprite(backgroundImage);
        camera = new OrthographicCamera();
        viewport = new FitViewport(MainGame.V_WIDTH, MainGame.V_HEIGHT, camera);
        font.getData().setScale(1.2F, 1F);
        font2.getData().setScale(1.2F, 1F);
        font.setColor(Color.RED);
    }

    /**
     * Method called when the screen is shown.
     * Initializes the sprite and camera position.
     */
    @Override
    public void show() {
        backgroundSprite.setSize(MainGame.V_WIDTH, MainGame.V_HEIGHT);
        backgroundSprite.setPosition(0, 0);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }

    /**
     * Method to render the screen.
     * Clears the screen and draws the background sprite.
     * Waits for input to select game mode.
     *
     * @param delta the time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            game.setCustomerCount(customerNumber);
            game.setDifficulty(difficultyArray[0]);
            game.isMenuScreen = false;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            game.setCustomerCount(-1);
            game.setDifficulty(difficultyArray[0]);
            game.isMenuScreen = false;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (difficultySelected) {
                difficultyArray = rotateDifficulty(difficultyArray, true);
            } else if (customerNumber > 1) {
                customerNumber--;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (difficultySelected) {
                difficultyArray = rotateDifficulty(difficultyArray, false);
            } else if (customerNumber < 100) {
                customerNumber++;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && difficultySelected) {
            difficultySelected = false;
            font.setColor(Color.RED);
            font2.setColor(Color.WHITE);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && !difficultySelected) {
            difficultySelected = true;
            font2.setColor(Color.RED);
            font.setColor(Color.WHITE);
        }
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        backgroundSprite.draw(game.batch);
        font.draw(game.batch, String.valueOf(customerNumber), MainGame.V_WIDTH/2, MainGame.V_HEIGHT/3);
        font2.draw(game.batch, difficultyArray[0], MainGame.V_WIDTH/2, MainGame.V_HEIGHT/5);
        game.batch.end();
    }

    /**
     * Method called when the screen is resized.
     * Updates the viewport and camera position.
     *
     * @param width the new screen width.
     * @param height the new screen height.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        backgroundImage.dispose();
        font.dispose();
    }

    /**
     * Rotates an array or strings either left or right cyclically
     *
     * @param difficulties an array of difficulties
     * @param rotateLeft a boolean that's true to rotate left, false to rotate right
     * @return the rotated array of difficulties
     */
    public String[] rotateDifficulty(String[] difficulties, boolean rotateLeft){
        String[] temp = new String[difficulties.length];

        if (rotateLeft){
            for (int i=0; i < difficulties.length-1; i++) {
                temp[i+1] = difficulties[i];
            }
            temp[0] = difficulties[difficulties.length-1];
        } else {
            for (int i=1; i < difficulties.length; i++) {
                temp[i-1] = difficulties[i];
            }
            temp[difficulties.length-1] = difficulties[0];
        }
        return temp;
    }
}
