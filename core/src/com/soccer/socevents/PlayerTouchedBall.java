package com.soccer.socevents;

import com.badlogic.gdx.Gdx;
import com.soccer.pojo.Player;
import com.soccer.pojo.TeamSide;

public class PlayerTouchedBall implements SocEvent {
    private Player player;
    public TeamSide teamSide;

    public PlayerTouchedBall(Player player) {
        this.player = player;
        Gdx.app.log("SocEvent", "PlayerTouchedBall  -" + player.getFirstName() + " (" + player.getTeam().getName() + ")");
    }

    public Player getPlayer() {
        return player;
    }

    public ClientSocEvent getClientSocEvent() {
        ClientSocEvent clientSocEvent = new ClientSocEvent();
        clientSocEvent.type = SocEventType.PLAYER_TOUCHED_BALL;
        clientSocEvent.teamSide = player.getTeam().getTeamSide();
        clientSocEvent.playerNumber = player.getTeam().getPlayers().indexOf(player);
        return clientSocEvent;
    }
}
