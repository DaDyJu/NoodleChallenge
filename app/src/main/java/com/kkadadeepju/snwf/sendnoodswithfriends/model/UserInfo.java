package com.kkadadeepju.snwf.sendnoodswithfriends.model;

/**
 * Created by Junyu on 2017-04-22.
 */

public class UserInfo {

    public UserInfo() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPowerUp() {
        return powerUp;
    }

    public void setPowerUp(int powerUp) {
        this.powerUp = powerUp;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserInfo(String name, String userId, int score, int powerUp) {
        this.name = name;
        this.userId = userId;
        this.score = score;
        this.powerUp = powerUp;
    }

    public String name;
    public String userId;
    public int score;
    public int powerUp;

}
