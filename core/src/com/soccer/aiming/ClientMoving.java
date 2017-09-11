package com.soccer.aiming;

import com.soccer.pojo.Position;

import java.util.ArrayList;

public class ClientMoving {
    public ArrayList<Position> teamApositions;
    public ArrayList<Position> teamBPositions;
    public Position ballPosition;

    public ClientMoving() {
        teamApositions = new ArrayList<Position>();
        teamBPositions = new ArrayList<Position>();
        ballPosition = new Position();
    }


}
