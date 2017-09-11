package com.soccer.states;

import com.soccer.pojo.Player;
import com.soccer.pojo.Team;

import java.util.ArrayList;
import java.util.List;

public class AimingState implements State {
    private Team team;
    private List<Player> players; //ez a két  alábi lista összeadása, ezekkel lehet cálozni
    private List<Player> playersInSector; //ezek azok a játékosok, akik úgy mozoghatnak, hogy labdába is érhetnek
    private List<Player> onlyMovingPlayers; //ezek a játékosok mozoghatnak, de nem érhetnek a labdához
    private Boolean aimingTeamHasTheBall;



    public AimingState(Team team, List<Player> playersInSector, List<Player> onlyMovingPlayers) {
        this.team = team;
        this.playersInSector = playersInSector;
        this.onlyMovingPlayers = onlyMovingPlayers;

        players = new ArrayList<Player>();
        players.addAll(onlyMovingPlayers);
        players.addAll(playersInSector);
        aimingTeamHasTheBall = players.size()>0 ? true : false;
    }

    public AimingState(Team team, Player playerInSector, List<Player> onlyMovingPlayers) {
        this.team = team;

        playersInSector = new ArrayList<Player>();
        playersInSector.add(playerInSector);
        this.onlyMovingPlayers = onlyMovingPlayers;

        players = new ArrayList<Player>();
        players.addAll(onlyMovingPlayers);
        players.addAll(playersInSector);

        aimingTeamHasTheBall = true;
    }

    public AimingState(Team team, List<Player> playersInSector) {
        this.team = team;
        this.playersInSector = playersInSector;
        this.onlyMovingPlayers = new ArrayList<Player>();

        players = new ArrayList<Player>();
        players.addAll(onlyMovingPlayers);
        players.addAll(playersInSector);

        aimingTeamHasTheBall = true;
    }

    public AimingState(Team team, Player playerInSector) {
        this.team = team;
        playersInSector = new ArrayList<Player>();
        playersInSector.add(playerInSector);
        this.onlyMovingPlayers = new ArrayList<Player>();

        players = new ArrayList<Player>();
        players.addAll(onlyMovingPlayers);
        players.addAll(playersInSector);

        aimingTeamHasTheBall = true;

    }

    public List<Player> getPlayers() {
        return players;
    }

    public Team getTeam() {
        return team;
    }

    public List<Player> getOnlyMovingPlayers() {
        return onlyMovingPlayers;
    }

    public Boolean getAimingTeamHasTheBall() {
        return aimingTeamHasTheBall;
    }

    public List<Player> getPlayersInSector() {
        return playersInSector;
    }

    @Override
    public ClientState getClientState() {
        ClientState clientState = new ClientState();
        clientState.stateType = StateType.AIMING_STATE;
        clientState.teamSide = team.getTeamSide();

        clientState.playerNumbers = new ArrayList<Integer>();

        for (Player player : players)
            clientState.playerNumbers.add(player.getTeam().getPlayers().indexOf(player));

//        clientState.numberOfPlaceing = numberOfPlaceing;
//        clientState.placedPlayers = placedPlayers;

        return clientState;
    }

}
