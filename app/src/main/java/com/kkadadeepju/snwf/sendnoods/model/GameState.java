package com.kkadadeepju.snwf.sendnoods.model;

/**
 * Created by Junyu on 2017-04-24.
 */

public class GameState {

    public GameState() {

    }

    public boolean getIsGameOver() {
        return isGameOver;
    }

    public void setIsGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public GameState(boolean isGameOver, int finalScore, String userName) {
        this.isGameOver = isGameOver;
        this.finalScore = finalScore;
        this.userName = userName;
    }

    public boolean isGameOver;
    public int finalScore;
    public String userName;

}
