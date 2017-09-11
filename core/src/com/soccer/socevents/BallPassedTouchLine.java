package com.soccer.socevents;

import com.badlogic.gdx.Gdx;
import com.soccer.pojo.Player;
import com.soccer.pojo.Team;
import com.soccer.pojo.TeamSide;

public class BallPassedTouchLine implements SocEvent {
    private Player lastTouchplayer;
    private Team lastTouchTeam;
    private float posX, posY;
    public TeamSide teamSide;

    public BallPassedTouchLine(Player lastTouchplayer, float posX, float posY) {
        this.lastTouchplayer = lastTouchplayer;
        lastTouchTeam = lastTouchplayer.getTeam();
        this.posX = posX;
        this.posY = posY;
        Gdx.app.log("SocEvent", "BallPassedGoalLine  - " + lastTouchplayer.getFirstName() + " x:" + posX + ", y:" + posY);
    }

    public Player getLastTouchplayer() {
        return lastTouchplayer;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public ClientSocEvent getClientSocEvent() {
        ClientSocEvent clientSocEvent = new ClientSocEvent();
        clientSocEvent.type = SocEventType.PLAYER_TOUCHED_PLAYER;
        clientSocEvent.teamSide = teamSide;
        clientSocEvent.playerNumber = lastTouchplayer.getTeam().getPlayers().indexOf(lastTouchplayer);
        clientSocEvent.x = getPosX();
        clientSocEvent.y = getPosY();
        return clientSocEvent;
    }
}
