package com.team13.piazzapanic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


public class HUD implements Disposable {
    public Stage stage;
    private Boolean scenarioComplete;
    private Boolean scenarioFailed;

    private Integer worldTimerM;
    private Integer worldTimerS;

    private Integer score;

    public String timeStr;

    public Table table;

    Label timeLabelT;
    Label timeLabel;

    Label scoreLabel;
    Label scoreLabelT;
    Label orderNumL;
    Label orderNumLT;

    public HUD(SpriteBatch sb){
        this.scenarioComplete = false;
        this.scenarioFailed = false;
        worldTimerM = 0;
        worldTimerS = 0;
        score = 0;
        timeStr = String.format("%d", worldTimerM) + " : " + String.format("%d", worldTimerS);
        float fontX = 0.5F;
        float fontY = 0.3F;

        BitmapFont font = new BitmapFont();
        font.getData().setScale(fontX, fontY);
        Viewport viewport = new FitViewport(MainGame.V_WIDTH, MainGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        table = new Table();
        table.left().top();
        table.setFillParent(true);

        timeLabel = new Label(String.format("%d", worldTimerM, ":", "%i", worldTimerS), new Label.LabelStyle(font, Color.WHITE));
        timeLabelT = new Label("TIME", new Label.LabelStyle(font, Color.BLACK));
        orderNumLT = new Label("ORDER", new Label.LabelStyle(font, Color.BLACK));
        orderNumL = new Label(String.format("%d", 0), new Label.LabelStyle(font, Color.WHITE));

        scoreLabel = new Label(String.format("%d", score), new Label.LabelStyle(font, Color.WHITE));
        scoreLabelT = new Label("MONEY", new Label.LabelStyle(font, Color.BLACK));


        table.add(timeLabelT).padTop(2).padLeft(2);
        table.add(scoreLabelT).padTop(2).padLeft(2);
        table.add(orderNumLT).padTop(2).padLeft(2);
        table.row();
        table.add(timeLabel).padTop(2).padLeft(2);
        table.add(scoreLabel).padTop(2).padLeft(2);
        table.add(orderNumL).padTop(2).padLeft(2);

        table.left().top();
        stage.addActor(table);
    }

    /**
     * Updates the time label.
     *
     * @param scenarioComplete Whether the game scenario has been completed.
     */
    public void updateTime(Boolean scenarioComplete){
        if(scenarioFailed) { return; }
        if(scenarioComplete){
            timeLabel.setColor(Color.GREEN);
            timeStr = String.format("%d", worldTimerM) + ":" + String.format("%d", worldTimerS);
            timeLabel.setText(String.format("TIME: " + timeStr + " MONEY: %d", score));
            timeLabelT.setText("SCENARIO COMPLETE \n PRESS ENTER TO EXIT");
            table.center().top();
            stage.addActor(table);
            return;
        }
        else {
            if (worldTimerS == 59) {
                worldTimerM += 1;
                worldTimerS = 0;
            } else {
                worldTimerS += 1;
            }
        }
        table.left().top();
        if(worldTimerS < 10){
            timeStr = String.format("%d", worldTimerM) + ":0" + String.format("%d", worldTimerS);
        }
        else {
            timeStr = String.format("%d", worldTimerM) + ":" + String.format("%d", worldTimerS);
        }
        timeLabel.setText(timeStr);
        stage.addActor(table);

    }

    /**
     * Calculates the user's score per order and updates the label.
     *
     * @param scenarioComplete Whether the game scenario has been completed.
     * @param expectedTime The expected time an order should be completed in.
     */
    public void updateScore(Boolean scenarioComplete, Integer expectedTime){
        int addScore;
        int currentTime;

        if(this.scenarioComplete == Boolean.FALSE){
            currentTime = (worldTimerM * 60) + worldTimerS;
            if (currentTime <= expectedTime) {
                addScore = 100;
            }
            else{
                addScore = 100 - (5 * (currentTime -expectedTime));
                if(addScore < 0){
                    addScore = 0;
                }
            }
            score += addScore;
        }

        if(scenarioComplete==Boolean.TRUE){
            scoreLabel.setColor(Color.GREEN);
            scoreLabel.setText("");
            scoreLabelT.setText("");
            scoreLabelT.remove();
            scoreLabel.remove();
            table.center().top();
            stage.addActor(table);
            this.scenarioComplete = Boolean.TRUE;
            return;
        }

        table.left().top();
        scoreLabel.setText(String.format("%d", score));
        stage.addActor(table);
    }

    public void updateScore(int amount) {
        score += amount;
        scoreLabel.setText(String.format("%d", score));
    }

    /**
     * Updates the order label.
     *
     * @param scenarioComplete Whether the game scenario has been completed.
     * @param orderNum The index number of the order.
     */
    public void updateOrder(Boolean scenarioComplete, Boolean scenarioFailed, Integer orderNum){
        if(scenarioComplete==Boolean.TRUE){
            orderNumL.remove();
            orderNumLT.remove();
            table.center().top();
            stage.addActor(table);
            return;
        }

        table.left().top();
        orderNumL.setText(String.format("%d", orderNum));
        orderNumLT.setText("ORDER");
        stage.addActor(table);

    }

    /**
     * Creates a fail state in the HUD
     */
    public void createFailState(boolean endlessMode)
    {
        scenarioFailed = true;

        // If the gamemode is endless (i.e. the score was not discarded), then show the time and orders survived
        if(endlessMode) {
            timeLabel.setText(String.format("TIME: " + timeStr));
        }
        else
        {
            timeLabel.remove();
        }

        scoreLabel.remove();
        scoreLabelT.remove();
        orderNumL.remove();
        orderNumLT.remove();

        timeLabelT.setText("SCENARIO FAILED \n PRESS ENTER TO EXIT");

        table.center().top();
        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }
}
