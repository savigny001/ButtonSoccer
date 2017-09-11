package com.server;

import com.user.User;

import java.util.ArrayList;
import java.util.List;


public class Match {
    public String match_id;
    public MatchConnection userAConnection, userBConnection;
    public List<MatchConnection> spectatorConnnecions;

    public Match () {
        spectatorConnnecions = new ArrayList<MatchConnection>();
    }

    public List<Integer> getConnectedSpectatorsIds() {
        List<Integer> connectedSpectatorsIds = new ArrayList<Integer>();
        for (MatchConnection matchConnection : spectatorConnnecions) {
            connectedSpectatorsIds.add(matchConnection.user_id);
        }
        return connectedSpectatorsIds;
    }

    public Integer getConnectedUserAId() {
        if (userAConnection != null) return userAConnection.user_id;
        else return null;
    }

    public Integer getConnectedUserBId() {
        if (userBConnection != null) return userBConnection.user_id;
        else return null;
    }

    public Integer getUserATeamId() {
        if (userAConnection != null) return userAConnection.team_id;
        else return null;
    }

    public Integer getUserBTeamId() {
        if (userBConnection != null) return userBConnection.team_id;
        else return null;
    }


}
