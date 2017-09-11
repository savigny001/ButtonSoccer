package com.soccer.socevents;

import com.badlogic.gdx.Gdx;
import com.soccer.pojo.Player;
import com.soccer.pojo.Team;
import com.soccer.pojo.TeamSide;

public class PlayerTouchedPlayer implements SocEvent {
    private Player faultPlayer;
    private Team faultTeam;
    public TeamSide teamSide;
    private float posX, posY;

    public PlayerTouchedPlayer(Player faultPlayer, float posX, float posY) {
        this.faultPlayer = faultPlayer;
        faultTeam = faultPlayer.getTeam();
        this.posX = posX;
        this.posY = posY;
        Gdx.app.log("SocEvent", "PlayerTouchedPlayer  -" + faultPlayer.getFirstName() + " (" + faultPlayer.getTeam().getName() + ")");

    }

    public Player getFaultPlayer() {
        return faultPlayer;
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
        clientSocEvent.teamSide = faultPlayer.getTeam().getTeamSide();
        clientSocEvent.playerNumber = faultPlayer.getTeam().getPlayers().indexOf(faultPlayer);
        clientSocEvent.x = getPosX();
        clientSocEvent.y = getPosY();
        return clientSocEvent;
    }

}
