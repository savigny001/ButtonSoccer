package com.soccer.socevents;

import com.badlogic.gdx.Gdx;
import com.soccer.pojo.Player;

public class BallPassedGoalLine implements SocEvent {
    private Player player;
    private GoalLineSide goalLineSide;

    public BallPassedGoalLine(Player player, GoalLineSide goalLineSide) {
        this.player = player;
        this.goalLineSide = goalLineSide;
        Gdx.app.log("SocEvent", "BallPassedGoalLine  - " + player.getFirstName() + " - " + goalLineSide);
    }

    public Player getPlayer() {
        return player;
    }

    public GoalLineSide getGoalLineSide() {
        return goalLineSide;
    }

    @Override
    public ClientSocEvent getClientSocEvent() {
        return null;
    }
}
