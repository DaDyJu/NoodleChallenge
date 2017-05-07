package com.kkadadeepju.snwf.noodlechallenge.model;

/**
 * Created by Junyu on 2017-04-22.
 */

public class PlayerInfo {
    public PlayerInfo(String name, String score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    private String name;
    private String score;
}
