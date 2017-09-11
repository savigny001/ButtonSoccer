package com.soccer.situations;

import com.badlogic.gdx.Gdx;
import com.soccer.model.Model;
import com.soccer.pojo.Team;
import com.soccer.socevents.AimingFinished;
import com.soccer.socevents.BallPassedTouchLine;
import com.soccer.socevents.BallStopped;
import com.soccer.socevents.PlaceingFinished;
import com.soccer.socevents.PlayerTouchedBall;
import com.soccer.socevents.PlayersStopped;
import com.soccer.socevents.SocEvent;
import com.soccer.socevents.SocEventList;
import com.soccer.states.Action;
import com.soccer.states.AimingState;
import com.soccer.states.Placeing;
import com.soccer.states.State;

import java.util.ArrayList;
import java.util.List;

public class ThrowInSituation implements Situation {
    private Model model;
    private State whatToDo;
    private Team teamToThrow, otherTeam;
    private List<SocEvent> oldSocEvents;
    private List<SocEvent> socEvents;

    private List<State> todoList;
    private Integer todoListIndex;
    private Situation nextSituation;

    public ThrowInSituation(Model model, Team teamToThrow, Team otherTeam, List<SocEvent> oldEvents) {
        Gdx.app.log("Situation", "ThrowIn");
        this.model = model;
        this.teamToThrow = teamToThrow;
        this.otherTeam = otherTeam;
        this.oldSocEvents = new ArrayList<SocEvent>();
        oldSocEvents.addAll(oldEvents);

        todoList = new ArrayList<State>();
        todoListIndex = 0;
        todoList.add(new Placeing(teamToThrow, teamToThrow.getPlayers(),1));
        todoList.add(new Placeing(otherTeam, otherTeam.getPlayers(),1));
        todoList.add(new AimingState(teamToThrow, teamToThrow.getPlayers()));

    }

    @Override
    public State getWhatToDo(List<SocEvent> newSocEvents) {
        socEvents = new ArrayList<SocEvent>();

        socEvents.addAll(newSocEvents);


        if (todoListIndex == 0) {

            if (model.everythingStopped()) {

                //csak az első találat érdekel, mert másodszor is áthaladhat a labda a vonalon visszafele
                //az oldSocEvents-ben nézzük, mivel még a megkapott listában van a passedtouchlineevent
                Boolean found = false;
                for (SocEvent socEvent : oldSocEvents) {
                    if ((socEvent instanceof BallPassedTouchLine) && (!found)) {
                        found = true;
                        BallPassedTouchLine ballPassedTouchLine = (BallPassedTouchLine) socEvent;
                        model.setBallPosition(ballPassedTouchLine.getPosX(), ballPassedTouchLine.getPosY());
                    }
                }

                whatToDo = todoList.get(todoListIndex);
                todoListIndex++;
            }
        }

        if ((todoListIndex == 1) && (SocEventList.isContains(socEvents, PlaceingFinished.class))) {
            whatToDo = todoList.get(todoListIndex);
            todoListIndex++;
        }

        if ((todoListIndex == 2) && (SocEventList.isContainsMoreTimes(socEvents, 2, PlaceingFinished.class))) {
            whatToDo = todoList.get(todoListIndex);
            todoListIndex++;
        }

        if ((todoListIndex == 3) && (SocEventList.isContains(socEvents, AimingFinished.class))) {
            nextSituation = new InPlaySituation(model, teamToThrow, otherTeam, socEvents);
            whatToDo = new Action();
        }


        return whatToDo;
    }

    @Override
    public Situation getNextSituation() {
        return nextSituation;
    }
}
