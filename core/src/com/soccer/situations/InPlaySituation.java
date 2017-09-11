package com.soccer.situations;

import com.badlogic.gdx.Gdx;
import com.soccer.FieldData;
import com.soccer.model.Model;
import com.soccer.pojo.Player;
import com.soccer.pojo.Team;
import com.soccer.pojo.TeamSide;
import com.soccer.socevents.AimingFinished;
import com.soccer.socevents.BallPassedGoal;
import com.soccer.socevents.BallPassedGoalLine;
import com.soccer.socevents.BallPassedTouchLine;
import com.soccer.socevents.GoalLineSide;
import com.soccer.socevents.PlayerTouchedBall;
import com.soccer.socevents.PlayerTouchedPlayer;
import com.soccer.socevents.SocEvent;
import com.soccer.socevents.SocEventList;
import com.soccer.states.Action;
import com.soccer.states.AimingState;
import com.soccer.states.OutOfPlay;
import com.soccer.states.State;

import java.util.ArrayList;
import java.util.List;

/*

Van egy labda nélküli mozgás, és szektorokkal lehet továbbmenni egy érintéssel
a védekező csapat nem mozoghat

 */
public class InPlaySituation implements Situation {
    private Boolean playerMoved;
    private Integer aimingFinishedNum = 0;
    private Model model;
    private State whatToDo;
    private RoundState roundState;
    private Team teamToStart, otherTeam;
    private List<SocEvent> socEvents, oldSocEvents, currentSocEvents;
    private Integer currentSocEventIndex;
    private List<State> todoList;
    private Integer todoListIndex;
    private Situation nextSituation;
    private Boolean wasSector, wasNoTouchStep, originalWasNoTouchStep, originalWasSector;
    private List<Player> playersInSector;
    private Boolean opponentIsMoving, opponentFinishedMove;

    public InPlaySituation(Model model, Team teamToStart, Team otherTeam, List <Player> playersToAim,
                           List<SocEvent> socEvents, Boolean wasNoTouchStep, Boolean wasSector, List<Player> playersInSector) {

        Gdx.app.log("Situation", "InPLay");

        playerMoved = false;
        this.model = model;
        this.teamToStart = teamToStart;
        this.otherTeam = otherTeam;
        this.socEvents = socEvents;
        this.playersInSector = playersInSector;
        opponentIsMoving = false;
        opponentFinishedMove = false;
        currentSocEventIndex = 0;

        roundState = RoundState.FIRST_STOP;
        Gdx.app.log("in Situation", "InPLay" + " RoundState:" + roundState.toString());

        this.originalWasSector = wasSector.booleanValue();

        wasSector = false;
        this.wasNoTouchStep = wasNoTouchStep;
        this.originalWasNoTouchStep = wasNoTouchStep.booleanValue();

        this.wasSector = wasSector;


        oldSocEvents = new ArrayList<SocEvent>();
        oldSocEvents.addAll(socEvents);

        currentSocEvents = new ArrayList<SocEvent>();

        socEvents.clear();
        todoList = new ArrayList<State>();
        todoListIndex = 0;

        this.wasNoTouchStep = false;

    }


    public InPlaySituation(Model model, Team teamToStart, Team otherTeam, List<SocEvent> socEvents, Boolean wasNoTouchStep,
                           Boolean wasSector, List<Player> playersInSector) {
        this(model,teamToStart,otherTeam, teamToStart.getPlayers(), socEvents, wasNoTouchStep, wasSector, playersInSector);
    }

    public InPlaySituation(Model model, Team teamToStart, Team otherTeam, List<SocEvent> socEvents) {
        this(model,teamToStart,otherTeam, teamToStart.getPlayers(), socEvents, false, false, new ArrayList<Player>());
    }

    @Override
    public State getWhatToDo(List<SocEvent> newSocEvents) {
        this.socEvents = new ArrayList<SocEvent>();
        socEvents.addAll(newSocEvents);

        refreshCurrentSocEvents();

        if (!model.everythingStopped()) {
            playerMoved = true;

            Gdx.app.log("in Situation", "InPLay" + " model.everythingStopped = false");
            Gdx.app.log("in Situation", "InPLay" + " playerMoved = true");
        }


        System.out.println("Playermoved:" + playerMoved);

        if (model.everythingStopped() && (playerMoved)) {

            Gdx.app.log("in Situation", "InPLay" + " model.everythingStopped = true");
            Gdx.app.log("in Situation", "InPLay" + " playerMoved = true");


//            Gdx.app.log("event", "EverythingStopped");

            if (!specialEventHappened()) {

                //amikor először megáll minden, akkor meg kell nézni, hogy van-e szektor, ha nincs
                //a másik csapat jön célzással, majd miután befejezte a célzást, az ő köre következik
                // vagyis létrehozunk számára egy új inplaysituation-t

                if ((roundState == RoundState.SECOND_MOVE) &&
                        (SocEventList.isContainsMoreTimes(socEvents, aimingFinishedNum+1, AimingFinished.class))) {
                    playerMoved = false;
                    currentSocEventIndex = socEvents.size();
                    roundState = RoundState.FIRST_STOP;
                    whatToDo = new OutOfPlay();
                } else if ((roundState == RoundState.OPPONENT_MOVE) &&
                        (SocEventList.isContainsMoreTimes(socEvents, aimingFinishedNum+1, AimingFinished.class))) {
                    playerMoved = false;
                    currentSocEventIndex = socEvents.size();
                    roundState = RoundState.OPPONENT_MOVE_FINISHED;
                } else if ((roundState == RoundState.FIRST_MOVE_AFTER_SECTOR) &&
                        (SocEventList.isContainsMoreTimes(socEvents, aimingFinishedNum+1, AimingFinished.class))) {
                    playerMoved = false;
                    currentSocEventIndex = socEvents.size();
//                    if (true) {
                    roundState = RoundState.MOVE_WITHOUT_BALL_FINISHED;
//                    } else
//                        roundState = RoundState.FIRST_STOP;
                } else if ((roundState == RoundState.FIRST_STOP)) {

                    Gdx.app.log("in Situation", "InPLay" + " RoundState:" + roundState.toString());

                    if (isSector()) {
                        Gdx.app.log("in Situation", "InPLay" + " isSector");
                        wasNoTouchStep = true; //azzal, hogy szektort adtunk, ellőttük a noTouchStepet
                        whatToDo = new AimingState(teamToStart,playersInSector);
//                        whatToDo = new AimingState(otherTeam, new ArrayList<Player>(), otherTeam.getPlayers());
//                        roundState = RoundState.OPPONENT_MOVE;
                        roundState = RoundState.SECOND_MOVE;
                        Gdx.app.log("in Situation", "InPLay" + " RoundState:" + roundState.toString());
                        aimingFinishedNum = SocEventList.howManyTimesContains(socEvents, AimingFinished.class);
                    } else if ((!isTouchBall()) && (!wasNoTouchStep)) {
                        Gdx.app.log("in Situation", "InPLay" + " moveWithoutBallTouch");
                        wasNoTouchStep = true;
                        whatToDo = new AimingState(teamToStart, teamToStart.getPlayers());
                        roundState = RoundState.SECOND_MOVE;
                        Gdx.app.log("Situation", "InPLay" + " RoundState:" + roundState.toString());
                        aimingFinishedNum = SocEventList.howManyTimesContains(socEvents, AimingFinished.class);
                    } else {
                        Gdx.app.log("in Situation", "InPLay" + " opponentWillCome");
                        whatToDo = new AimingState(otherTeam,otherTeam.getPlayers());
                        roundState = RoundState.OPPONENT_COMES;
                        Gdx.app.log("in Situation", "InPLay" + " RoundState:" + roundState.toString());
                        aimingFinishedNum = SocEventList.howManyTimesContains(socEvents, AimingFinished.class);
                    }
                } else if ((roundState == RoundState.OPPONENT_MOVE_FINISHED)) {
                    whatToDo = new AimingState(teamToStart, playersInSector, teamToStart.getPlayers());
                    roundState = RoundState.FIRST_MOVE_AFTER_SECTOR;
                    aimingFinishedNum = SocEventList.howManyTimesContains(socEvents, AimingFinished.class);
                } else if ((roundState == RoundState.MOVE_WITHOUT_BALL_FINISHED)) {
                    if (!isTouchBall()) {
                        wasNoTouchStep = true;
                        whatToDo = new AimingState(teamToStart, playersInSector, teamToStart.getPlayers());
                        roundState = RoundState.SECOND_MOVE;
                        aimingFinishedNum = SocEventList.howManyTimesContains(socEvents, AimingFinished.class);

                    } else {
                        roundState = RoundState.FIRST_STOP;
                    }

                }

                if ((roundState == RoundState.OPPONENT_COMES) &&
                        (SocEventList.isContainsMoreTimes(socEvents, aimingFinishedNum+1, AimingFinished.class))) {
                    Gdx.app.log("in Situation", "InPLay" + " make new inPlay Situation for opponent");
                    whatToDo = new Action();
                    nextSituation = new InPlaySituation(model, otherTeam, teamToStart, socEvents);
                }

            }
        }  else whatToDo = new OutOfPlay();

        return whatToDo;
    }

    @Override
    public Situation getNextSituation() {

        return nextSituation;
    }



    public Boolean specialEventHappened () {
        Boolean specialEvent = false;

        if (SocEventList.isContains(currentSocEvents, PlayerTouchedPlayer.class)) {
            whatToDo = new Action();
            nextSituation = new FaultSituation(model, otherTeam, teamToStart, socEvents);
            specialEvent = true;

        } else if (SocEventList.isContains(currentSocEvents, BallPassedGoal.class)) {
            whatToDo = new Action();
            nextSituation = new GoalSituation(model, otherTeam, teamToStart, socEvents);
            specialEvent = true;

            //TODO ha áthalad a labda a goalline-on és a touchlinee-on is, akkor a sorrendet is nézni kell!!!!
        } else if (SocEventList.isContains(currentSocEvents, BallPassedTouchLine.class)) {
            specialEvent = true;
            whatToDo = new Action();
            BallPassedTouchLine ballPassedTouchLine = null;

//                    System.out.println("INPLAY átadott SOCEVENT LIST");
            for (SocEvent socEvent : currentSocEvents) {
//                        System.out.println(socEvent.getClass().toString());
                if (socEvent instanceof BallPassedTouchLine)
                    ballPassedTouchLine = (BallPassedTouchLine) socEvent;
            }


            Team lastTouchTeam = ballPassedTouchLine.getLastTouchplayer().getTeam();
            Team nextTeam;
            if (lastTouchTeam.equals(teamToStart)) nextTeam = otherTeam; else nextTeam = teamToStart;

            nextSituation = new ThrowInSituation(model, nextTeam, lastTouchTeam, currentSocEvents);

        } else if (SocEventList.isContains(currentSocEvents, BallPassedGoalLine.class)) {
            specialEvent = true;
            whatToDo = new Action();

            Boolean found = false;
            for (SocEvent socEvent : currentSocEvents) {
                if ((socEvent instanceof BallPassedGoalLine) && (!found)) {
                    found = true;
                    BallPassedGoalLine ballPassedGoalLine = (BallPassedGoalLine) socEvent;

                    Team lastTouchTeam = ballPassedGoalLine.getPlayer().getTeam();
                    Team nextTeam;
                    if (lastTouchTeam.equals(teamToStart)) nextTeam = otherTeam; else nextTeam = teamToStart;

                    if (ballPassedGoalLine.getPlayer().getTeam().getTeamSide().equals(TeamSide.LEFT)) {
                        if (ballPassedGoalLine.getGoalLineSide().equals(GoalLineSide.WEST))
                            nextSituation = new CornerKickSituation(model, nextTeam, lastTouchTeam, currentSocEvents);
                        if (ballPassedGoalLine.getGoalLineSide().equals(GoalLineSide.EAST))
                            nextSituation = new GoalKickSituation(model, nextTeam, lastTouchTeam, currentSocEvents);
                    }

                    if (ballPassedGoalLine.getPlayer().getTeam().getTeamSide().equals(TeamSide.RIGHT)) {
                        if (ballPassedGoalLine.getGoalLineSide().equals(GoalLineSide.EAST))
                            nextSituation = new CornerKickSituation(model, nextTeam, lastTouchTeam, currentSocEvents);
                        if (ballPassedGoalLine.getGoalLineSide().equals(GoalLineSide.WEST))
                            nextSituation = new GoalKickSituation(model, nextTeam, lastTouchTeam, currentSocEvents);
                    }
                }
            }

        }

        return specialEvent;

    }

    public Boolean isTouchBall() {
        if (SocEventList.isContains(currentSocEvents,PlayerTouchedBall.class))
//                &&  (!SocEventList.isContains(socEvents,PlayerTouchedPlayer.class))
        {
            return true;
        } else return false;
    }


    public Boolean isSector() {

        List<Player> sectorPlayers = new ArrayList<Player>();
        playersInSector = new ArrayList<Player>();

        //csak akkor lehet szektor, hogyha történt labdaérintés
        //viszont maximum két labdaérintés történhet
        if ((SocEventList.isContainsMoreTimes(currentSocEvents, 1, PlayerTouchedBall.class)) ||
                (SocEventList.isContainsMoreTimes(currentSocEvents, 2, PlayerTouchedBall.class))) {

            Boolean opponentTouch = false;
            for (SocEvent socEvent : currentSocEvents) {
                if (socEvent instanceof PlayerTouchedBall) {
                    PlayerTouchedBall playerTouchedBall = (PlayerTouchedBall) socEvent;
                    if (playerTouchedBall.getPlayer().getTeam().equals(otherTeam)) opponentTouch = true;
                }
            }

            if (!opponentTouch) {
                for (Player player : teamToStart.getPlayers()) {
                    if ((player.getDistanceFromBall()) < (FieldData.SECTOR_RADIUS / FieldData.PIXELS_TO_METERS)) {

                        if ( (!player.isAimed()))
//                                   || ((player.isAimed()) && ((SocEventList.isContainsMoreTimes(socEvents, 2, PlayerTouchedBall.class)))  ))
                        {

                            sectorPlayers.add(player);
//                                    System.out.println("Sectorban: " + player.getFirstName() + player.getLastName() + " dist:" + player.getDistanceFromBall());
                        }

                    }
                }
            }
        }

        if (sectorPlayers.size()>0) {

            for (Player player : sectorPlayers) Gdx.app.log("sectorban", player.getFirstName() + " "
                    + " (" +player.getTeam().getName() + ") ");

            playersInSector = new ArrayList<Player>();
            playersInSector.addAll(sectorPlayers);

            return true;

        } else return false;


    }

    public void refreshCurrentSocEvents() {
        currentSocEvents = new ArrayList<SocEvent>();
        for (int i = currentSocEventIndex; i<socEvents.size(); i++) {
            currentSocEvents.add(socEvents.get(i));
//            Gdx.app.log("Current",socEvents.get(i).getClass().toString());
        }


    }

}
