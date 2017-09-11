package com.soccer.states;

import com.soccer.pojo.Player;
import com.soccer.pojo.Team;

import java.util.ArrayList;
import java.util.List;

public class Placeing implements State {
    private Team team;
    private List<Player> players;
    private Integer numberOfPlaceing;
    private Integer placedPlayers;

    public Placeing(Team team, List<Player> players, Integer numberOfPlaceing) {
        this.team = team;
        this.players = players;
        this.numberOfPlaceing = numberOfPlaceing;
        placedPlayers = 0;
    }

    public Placeing(Team team, List<Player> players) {
        this(team,players,2);
    }

    public void playerPlaced() {
        placedPlayers++;
    }

    public Boolean isAllPlayerPlaced() {

        if (placedPlayers<numberOfPlaceing) return false; else return true;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public ClientState getClientState() {
        ClientState clientState = new ClientState();
        clientState.stateType = StateType.PLACEING;
        clientState.teamSide = team.getTeamSide();

        clientState.playerNumbers = new ArrayList<Integer>();

        for (Player player : players)
            clientState.playerNumbers.add(player.getTeam().getPlayers().indexOf(player));

        clientState.numberOfPlaceing = numberOfPlaceing;
        clientState.placedPlayers = placedPlayers;

        return clientState;
    }
}
