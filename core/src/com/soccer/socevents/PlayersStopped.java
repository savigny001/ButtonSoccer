package com.soccer.socevents;

import com.badlogic.gdx.Gdx;

public class PlayersStopped implements SocEvent {
    public PlayersStopped() {
        Gdx.app.log("SocEvent", "PlayersStopped");
    }

    public ClientSocEvent getClientSocEvent() {
        ClientSocEvent clientSocEvent = new ClientSocEvent();
        clientSocEvent.type = SocEventType.PLAYERS_STOPPED;
        return clientSocEvent;
    }
}
