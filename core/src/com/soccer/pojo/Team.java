package com.soccer.pojo;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Player> players;
    private Integer score;
    private TeamSide teamSide;
    private Kit homeGoalkeeperKit, homeKit, awayGoalkeeperKit, awayKit;

    public Team () {

        players = new ArrayList<Player>();
        score = 0;
        name = "noname";

    }

    public void setOriginalPositions() {
        for (Player player : players) {
            player.resetOriginalPosition();
        }
    }

    public void setPositions(List<Position> positions) {
        int i=0;
        for (Player player : players) {
            player.setPosition(positions.get(i));
            i++;
        }
    }


    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamSide getTeamSide() {
        return teamSide;
    }

    public void setTeamSide(TeamSide teamSide) {
        this.teamSide = teamSide;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void addGoal() {
        ++this.score;
    }

    public Kit getHomeGoalkeeperKit() {
        return homeGoalkeeperKit;
    }

    public void setHomeGoalkeeperKit(Kit homeGoalkeeperKit) {
        this.homeGoalkeeperKit = homeGoalkeeperKit;
    }

    public Kit getHomeKit() {
        return homeKit;
    }

    public void setHomeKit(Kit homeKit) {
        this.homeKit = homeKit;
    }

    public Kit getAwayGoalkeeperKit() {
        return awayGoalkeeperKit;
    }

    public void setAwayGoalkeeperKit(Kit awayGoalkeeperKit) {
        this.awayGoalkeeperKit = awayGoalkeeperKit;
    }

    public Kit getAwayKit() {
        return awayKit;
    }

    public void setAwayKit(Kit awayKit) {
        this.awayKit = awayKit;
    }
}