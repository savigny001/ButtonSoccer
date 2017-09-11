package com.server;


import java.util.ArrayList;
import java.util.List;

public class ConnectedUsers {

    public List<Integer> connectedSpectatorsIds;
    public Integer connectedUserAId, connectedUserBId;
    public Integer teamAId, teamBId;


    public ConnectedUsers() {
        connectedSpectatorsIds = new ArrayList<Integer>();
    }
}
