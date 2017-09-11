package com.soccer.situations;

import com.badlogic.gdx.Gdx;
import com.soccer.FieldData;
import com.soccer.model.Model;
import com.soccer.pojo.Team;
import com.soccer.socevents.AimingFinished;
import com.soccer.socevents.BallPassedGoalLine;
import com.soccer.socevents.BallStopped;
import com.soccer.socevents.GoalLineSide;
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

public class GoalKickSituation implements Situation {
    private Model model;
    private State whatToDo;
    private Team teamToKick, otherTeam;
    private List<SocEvent> oldSocEvents;
    private List<SocEvent> socEvents;

    private List<State> todoList;
    private Integer todoListIndex;
    private Situation nextSituation;

    public GoalKickSituation(Model model, Team teamToKick, Team otherTeam, List<SocEvent> oldEvents) {
        Gdx.app.log("Situation", "GoalKick");
        this.model = model;
        this.teamToKick = teamToKick;
        this.otherTeam = otherTeam;
        this.oldSocEvents = new ArrayList<SocEvent>();
        oldSocEvents.addAll(oldEvents);

        todoList = new ArrayList<State>();
        todoListIndex = 0;
        todoList.add(new Placeing(teamToKick, teamToKick.getPlayers(),2));
        todoList.add(new Placeing(otherTeam, otherTeam.getPlayers(),2));
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
                    System.out.println("SOCEVENT here:" + socEvent.getClass().toString());
                    if ((socEvent instanceof BallPassedGoalLine) && (!found)) {
                        found = true;
                        BallPassedGoalLine ballPassedGoalLine = (BallPassedGoalLine) socEvent;

                        if (ballPassedGoalLine.getGoalLineSide() == GoalLineSide.EAST) {
                            model.setBallPosition(FieldData.GOALKICKSPOT_E_X/FieldData.PIXELS_TO_METERS,
                                    FieldData.GOALKICKSPOT_E_Y/FieldData.PIXELS_TO_METERS);
                        }

                        if (ballPassedGoalLine.getGoalLineSide() == GoalLineSide.WEST) {
                            model.setBallPosition(FieldData.GOALKICKSPOT_W_X/FieldData.PIXELS_TO_METERS,
                                    FieldData.GOALKICKSPOT_W_Y/FieldData.PIXELS_TO_METERS);

                        }
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
