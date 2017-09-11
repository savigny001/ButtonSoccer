package com.soccer.socevents;

import com.badlogic.gdx.Gdx;

public class BallStopped implements SocEvent {

    public BallStopped() {
        Gdx.app.log("SocEvent", "BallStopped");
    }

    public ClientSocEvent getClientSocEvent() {
        ClientSocEvent clientSocEvent = new ClientSocEvent();
        clientSocEvent.type = SocEventType.BALL_STOPPED;
        return clientSocEvent;
    }

}
