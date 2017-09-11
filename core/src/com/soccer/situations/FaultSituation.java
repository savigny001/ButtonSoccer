package com.soccer.situations;

import com.badlogic.gdx.Gdx;
import com.soccer.model.Model;
import com.soccer.pojo.Team;
import com.soccer.socevents.AimingFinished;
import com.soccer.socevents.BallStopped;
import com.soccer.socevents.PlaceingFinished;
import com.soccer.socevents.PlayerTouchedBall;
import com.soccer.socevents.PlayerTouchedPlayer;
import com.soccer.socevents.PlayersStopped;
import com.soccer.socevents.SocEvent;
import com.soccer.socevents.SocEventList;
import com.soccer.states.Action;
import com.soccer.states.AimingState;
import com.soccer.states.Placeing;
import com.soccer.states.State;

import java.util.ArrayList;
import java.util.List;

public class FaultSituation implements Situation {
    private Model model;
    private State whatToDo;
    private Team teamToKick, otherTeam;
    private List<SocEvent> oldSocEvents;
    private List<SocEvent> socEvents;

    private List<State> todoList;
    private Integer todoListIndex;
    private Situation nextSituation;

    public FaultSituation(Model model, Team teamToKick, Team otherTeam, List<SocEvent> oldEvents) {
        Gdx.app.log("Situation", "Fault");
        this.model = model;
        this.teamToKick = teamToKick;
        this.otherTeam = otherTeam;
        this.oldSocEvents = new ArrayList<SocEvent>();
        oldSocEvents.addAll(oldEvents);

        todoList = new ArrayList<State>();
        todoListIndex = 0;
        todoList.add(new Placeing(teamToKick, teamToKick.getPlayers()));
        todoList.add(new Placeing(otherTeam, otherTeam.getPlayers()));
        todoList.add(new AimingState(teamToKick, teamToKick.getPlayers()));

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
//                    System.out.println("SOCEVENT here:" + socEvent.getClass().toString());
                    if ((socEvent instanceof PlayerTouchedPlayer) && (!found)) {
                        found = true;
                        PlayerTouchedPlayer playerTouchedPlayer = (PlayerTouchedPlayer) socEvent;

                        model.setBallPosition(playerTouchedPlayer.getPosX(),
                                playerTouchedPlayer.getPosY());


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
            nextSituation = new InPlaySituation(model, teamToKick, otherTeam, socEvents);
            whatToDo = new Action();
        }


        return whatToDo;
    }

    @Override
    public Situation getNextSituation() {
        return nextSituation;
    }
}
