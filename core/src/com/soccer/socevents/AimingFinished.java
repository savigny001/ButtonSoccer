package com.soccer.socevents;

import com.badlogic.gdx.Gdx;

public class AimingFinished implements SocEvent {

    public AimingFinished() {
        Gdx.app.log("SocEvent", "AimingFinished");
    }

    public ClientSocEvent getClientSocEvent() {
        ClientSocEvent clientSocEvent = new ClientSocEvent();
        clientSocEvent.type = SocEventType.AIMING_FINISHED;
        return clientSocEvent;
    }

}
