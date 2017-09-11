package com.user;

import com.soccer.pojo.Player;

import java.util.ArrayList;
import java.util.List;

public class ButtonTeam {

    private String name;
    private Integer id;
    private Integer user_id;
    private ButtonKit homeF, homeGK, awayF, awayGK;
    private List<Player> players;

    public ButtonTeam() {
        players = new ArrayList<Player>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public ButtonKit getHomeF() {
        return homeF;
    }

    public void setHomeF(ButtonKit homeF) {
        this.homeF = homeF;
    }

    public ButtonKit getHomeGK() {
        return homeGK;
    }

    public void setHomeGK(ButtonKit homeGK) {
        this.homeGK = homeGK;
    }

    public ButtonKit getAwayF() {
        return awayF;
    }

    public void setAwayF(ButtonKit awayF) {
        this.awayF = awayF;
    }

    public ButtonKit getAwayGK() {
        return awayGK;
    }

    public void setAwayGK(ButtonKit awayGK) {
        this.awayGK = awayGK;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return getName();
    }
}
