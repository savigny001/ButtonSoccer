package com.soccer.socevents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.soccer.pojo.Player;
import com.soccer.pojo.Team;
import com.soccer.pojo.TeamSide;

public class AimingStarted implements SocEvent {
    private Player player;
    public TeamSide teamSide;

    public AimingStarted (Player player) {
        Gdx.app.log("SocEvent", "AimingStarted  - " + player.getFirstName() + " (" + player.getTeam().getName() + ")");
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ClientSocEvent getClientSocEvent() {
        ClientSocEvent clientSocEvent = new ClientSocEvent();
        clientSocEvent.type = SocEventType.AIMING_STARTED;
        clientSocEvent.teamSide = player.getTeam().getTeamSide();
        clientSocEvent.playerNumber = player.getTeam().getPlayers().indexOf(player);
        return clientSocEvent;
    }
}
