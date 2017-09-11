package com.soccer.states;


import com.soccer.pojo.TeamSide;

import java.util.List;

public class ClientState {
    public StateType stateType;
    public List<Integer> playerNumbers;
    public TeamSide teamSide;
    public Integer numberOfPlaceing, placedPlayers;

    public ClientState() {
    }
}
