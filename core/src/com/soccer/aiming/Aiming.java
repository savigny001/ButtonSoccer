package com.soccer.aiming;

import com.badlogic.gdx.physics.box2d.Body;

public class Aiming {
    private AimingType aimingType;
    private boolean isAimingInProgress;
    private float playerOrigoX, PlayerOrigoY;
    private float mouseX, mouseY;
    private Body playerBody;

    public AimingType getAimingType() {
        return aimingType;
    }

    public void setAimingType(AimingType aimingType) {
        this.aimingType = aimingType;
    }

    public boolean isAimingInProgress() {
        return isAimingInProgress;
    }

    public void setAimingInProgress(boolean aimingInProgress) {
        isAimingInProgress = aimingInProgress;
    }

    public float getPlayerOrigoX() {
        return playerOrigoX;
    }

    public void setPlayerOrigoX(float playerOrigoX) {
        this.playerOrigoX = playerOrigoX;
    }

    public float getPlayerOrigoY() {
        return PlayerOrigoY;
    }

    public void setPlayerOrigoY(float playerOrigoY) {
        PlayerOrigoY = playerOrigoY;
    }

    public float getMouseX() {
        return mouseX;
    }

    public void setMouseX(float mouseX) {
        this.mouseX = mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    public void setMouseY(float mouseY) {
        this.mouseY = mouseY;
    }

    public Body getPlayerBody() {
        return playerBody;
    }

    public void setPlayerBody(Body playerBody) {
        this.playerBody = playerBody;
    }
}
