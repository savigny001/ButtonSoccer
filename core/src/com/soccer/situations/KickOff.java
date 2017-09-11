package com.soccer.situations;

import com.badlogic.gdx.Gdx;
import com.soccer.aiming.Aiming;
import com.soccer.model.Model;
import com.soccer.pojo.Team;
import com.soccer.socevents.AimingFinished;
import com.soccer.socevents.PlaceingFinished;
import com.soccer.socevents.SocEvent;
import com.soccer.socevents.SocEventList;
import com.soccer.states.Action;
import com.soccer.states.AimingState;
import com.soccer.states.Placeing;
import com.soccer.states.State;
import com.soccer.whattodo.WhatToDo;

import java.util.ArrayList;
import java.util.List;

public class KickOff implements Situation {
    private Model model;
    private State whatToDo, toDo;
    private Team teamToStart, otherTeam;
    private List<SocEvent> socEvents;
    private List<State> todoList;
    private Integer todoListIndex;
    private Situation nextSituation;

    public  KickOff (Model model, Team teamToStart, Team otherTeam, List<SocEvent> socEvents) {

        Gdx.app.log("Situation", "KickOff");
        this.model = model;
        this.teamToStart = teamToStart;
        this.otherTeam = otherTeam;
        this.socEvents = socEvents;

        todoList = new ArrayList<State>();
        todoListIndex = 0;
        todoList.add(new AimingState(teamToStart, teamToStart.getPlayers()));
    }

    @Override
    public State getWhatToDo(List<SocEvent> newSocEvents) {
        this.socEvents = newSocEvents;


        if (todoListIndex == 0) {
            whatToDo = todoList.get(todoListIndex);
//            todoListIndex++;
            todoListIndex+=3;
        }

        if ((todoListIndex == 1) && (SocEventList.isContains(socEvents, PlaceingFinished.class))) {
            whatToDo = todoList.get(todoListIndex);
            todoListIndex+=2;
        }

        if ((todoListIndex == 2) && (SocEventList.isContainsMoreTimes(socEvents, 2, PlaceingFinished.class))) {
            whatToDo = todoList.get(todoListIndex);
            todoListIndex++;
        }

        if ((todoListIndex == 3) && (SocEventList.isContains(socEvents, AimingFinished.class))) {


            nextSituation = new InPlaySituation(model, teamToStart, otherTeam, socEvents);
            whatToDo = new Action();
        }

        return whatToDo;
    }

    @Override
    public Situation getNextSituation() {
        return nextSituation;
    }
}
