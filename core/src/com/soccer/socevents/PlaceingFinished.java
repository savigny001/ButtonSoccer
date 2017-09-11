package com.soccer.socevents;

import com.badlogic.gdx.Gdx;

public class PlaceingFinished implements SocEvent {
    public PlaceingFinished() {
        Gdx.app.log("SocEvent", "PlaceingFinished");
    }

    public ClientSocEvent getClientSocEvent() {
        ClientSocEvent clientSocEvent = new ClientSocEvent();
        clientSocEvent.type = SocEventType.PLACEING_FINISHED;
        return clientSocEvent;
    }


}
