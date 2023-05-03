package com.team13.piazzapanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;

import java.awt.*;

public class MainGame extends Game {

	/**
	 * MainGame class is the central class of the game that creates and manages the two screens, PlayScreen and StartScreen.
	 *
	 * Class Members:
	 *     V_WIDTH (int): Width of the view.
	 *     V_HEIGHT (int): Height of the view.
	 *     TILE_SIZE (int): Size of the tile.
	 *     PPM (float): Pixels per meter.
	 *     batch (SpriteBatch): Instance of SpriteBatch.
	 *     isPlayScreen (bool): Flag indicating whether the PlayScreen is displayed or not.
	 *     playScreen (PlayScreen): Instance of PlayScreen.
	 *     startScreen (StartScreen): Instance of StartScreen.
	 *
	 * Methods:
	 *     __init__: Initializes the MainGame class.
	 *     create: Creates the instances of StartScreen and PlayScreen and initializes the SpriteBatch instance.
	 *     render: Renders the StartScreen or PlayScreen based on the value of isPlayScreen flag.
	 * 	   dispose: Releases resources used by the MainGame class.
	 */
	public static final int V_WIDTH = 160;
	public static final int V_HEIGHT = 160;
	public static final int TILE_SIZE = 16;

	public static final float PPM = 100;
	public SpriteBatch batch;
	public boolean isPlayScreen;
	private MenuScreen menuScreen;
	private PlayScreen playScreen;
	private StartScreen startScreen;
	public boolean isMenuScreen;
	private Preferences highScoreData;
	private Boolean loadPossible;

	public MainGame(){
		isPlayScreen = false;
		isMenuScreen = true;
	}

	public void setCustomerCount(int customers){
		playScreen.customerTotal = customers;
	}
	public void setDifficulty(String difficulty){
		if (difficulty.equals("Easy")){
			playScreen.difficultyModerator = 3.5f;
		} else if (difficulty.equals("Medium")){
			playScreen.difficultyModerator = 2.5f;
		} else if (difficulty.equals("Hard")){
			playScreen.difficultyModerator = 1.5f;
		}
		playScreen.difficulty = difficulty;
	}
	@Override
	public void create() {
		batch = new SpriteBatch();
		menuScreen = new MenuScreen(this);
		startScreen = new StartScreen(this);
		playScreen = new PlayScreen(this);

		loadPossible = LoadManager.initialise();
		if(!loadPossible) { menuScreen.showNoLoad(); }

		// Create high score data if none exists
		highScoreData = Gdx.app.getPreferences("piazza_panic_hs");
		if(highScoreData.get().size() != 3)
		{
			highScoreData.clear();
			highScoreData.putInteger("Easy", 0);
			highScoreData.putInteger("Medium", 0);
			highScoreData.putInteger("Hard", 0);
			highScoreData.flush();
		}
	}

	@Override
	public void render() {
		super.render();
		if (isMenuScreen) {
			setScreen(menuScreen);
			if(Gdx.input.isKeyJustPressed(Input.Keys.L) && loadPossible)
			{
				LoadManager.loadMidGameSave(playScreen, playScreen.hud);
				setDifficulty(LoadManager.getDifficulty());
				isMenuScreen = false;
				isPlayScreen = true;
				setScreen(startScreen);
			}
		} else{
			if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)){
				isPlayScreen = !isPlayScreen;
			}
			if (isPlayScreen) {
				setScreen(playScreen);
			} else {
				setScreen(startScreen);
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}
}