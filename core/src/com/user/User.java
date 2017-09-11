package com.user;

import com.soccer.pojo.Team;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Integer id;
    private String name;
    private float eloPoint;
    private List<ButtonTeam> teams;
    private Schedule schedule;

    public User() {
    }

    public User(Integer id, String name, Integer eloPoint) {
        this.id = id;
        this.name = name;
        this.eloPoint = eloPoint;
        teams = new ArrayList<ButtonTeam>();
        schedule = new Schedule();
    }

    public User(Integer id, String name) {
        this(id, name, 900);
    }


    public void addTeam (ButtonTeam team) {
        teams.add(team);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getEloPoint() {
        return eloPoint;
    }

    public List<ButtonTeam> getTeams() {
        return teams;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setEloPoint(float eloPoint) {
        this.eloPoint = eloPoint;
    }

    @Override
    public String toString() {
        return name;
    }
}
