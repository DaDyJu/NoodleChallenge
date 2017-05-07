package com.kkadadeepju.snwf.noodlechallenge.model;

/**
 * Created by Junyu on 2017-04-22.
 */

public class PowerUp {
    public PowerUp() {

    }

    public PowerUp(String userId, int powerUpType) {
        this.userId = userId;
        this.powerUpType = powerUpType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPowerUpType() {
        return powerUpType;
    }

    public void setPowerUpType(int powerUpType) {
        this.powerUpType = powerUpType;
    }

    public String userId;
    public int powerUpType;
}
