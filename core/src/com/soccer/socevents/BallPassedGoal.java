package com.soccer.socevents;

import com.badlogic.gdx.Gdx;
import com.soccer.pojo.Player;
import com.soccer.pojo.Team;

public class BallPassedGoal implements SocEvent {
    private Player goalScorerPlayer;
    private Team goalScorerTeam;

    public BallPassedGoal(Player goalScorerPlayer, Team goalScorerTeam) {
        this.goalScorerPlayer = goalScorerPlayer;
        this.goalScorerTeam = goalScorerTeam;
        Gdx.app.log("SocEvent", "BallPassedGoal  - " + goalScorerPlayer.getFirstName() + " ("
                + goalScorerTeam.getName() + ")");
    }

    public Player getGoalScorerPlayer() {
        return goalScorerPlayer;
    }

    public Team getGoalScorerTeam() {
        return goalScorerTeam;
    }

    @Override
    public ClientSocEvent getClientSocEvent() {
        return null;
    }
}
