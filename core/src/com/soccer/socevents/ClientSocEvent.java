package com.soccer.socevents;

import com.soccer.pojo.TeamSide;

public class ClientSocEvent {
    public SocEventType type;
    public TeamSide teamSide;
    public Integer playerNumber;
    public float x, y;
    public GoalLineSide goalLineSide;

    public ClientSocEvent() {
    }
}
