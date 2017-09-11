package com.soccer.controller;



import com.badlogic.gdx.Gdx;
import com.soccer.aiming.Aiming;
import com.soccer.model.Model;
import com.soccer.pojo.Player;
import com.soccer.pojo.Team;
import com.soccer.situations.Situation;
import com.soccer.socevents.AimingFinished;
import com.soccer.socevents.BallStopped;
import com.soccer.socevents.PlaceingFinished;
import com.soccer.socevents.PlayerTouchedBall;
import com.soccer.socevents.PlayersStopped;
import com.soccer.socevents.SocEvent;
import com.soccer.states.Action;
import com.soccer.states.AimingState;
import com.soccer.states.Placeing;
import com.soccer.states.State;

import java.util.List;

public class Controller {

    private Model model;
    private Team activeTeam, otherTeam;
    private Team teamA, teamB;
    private Situation situation;

    public Controller(Team activeTeam, Team otherTeam, Situation startSituation) {
        teamA = activeTeam;
        teamB = otherTeam;
        this.activeTeam = teamA;
        this.situation = startSituation;
    }

    public State getState(List<SocEvent> socEvents) {
        State toDo = situation.getWhatToDo(socEvents);

        if (toDo instanceof Action) {
            situation = situation.getNextSituation();

            socEvents.clear();
            toDo = situation.getWhatToDo(socEvents);
        }

        return toDo;

    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
