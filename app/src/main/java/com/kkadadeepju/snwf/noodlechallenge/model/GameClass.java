package com.kkadadeepju.snwf.noodlechallenge.model;

/**
 * Created by Junyu on 2017-04-22.
 */

public class GameClass {

    public GameClass(String gameId, int gameWaiting, boolean isGameStarted) {
        this.gameId = gameId;
        this.gameWaiting = gameWaiting;
        this.isGameStarted = isGameStarted;
    }

    public String gameId;
    public int gameWaiting;
    public boolean isGameStarted;

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }


    public GameClass() {
    }


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getgameWaiting() {
        return gameWaiting;
    }

    public void setgameWaiting(int gameWaiting) {
        this.gameWaiting = gameWaiting;
    }

}
