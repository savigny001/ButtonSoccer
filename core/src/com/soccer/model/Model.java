package com.soccer.model;

import com.soccer.pojo.Ball;
import com.soccer.pojo.Position;
import com.soccer.pojo.Team;

import java.util.List;
import java.util.Observable;

public class Model extends Observable {
    private Team teamA, teamB;
    private Ball ball;
    private Boolean arePlayersMoving = false;

    public Model(Team teamA, Team teamB, Ball ball) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.ball = ball;
    }

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team teamA) {
        this.teamA = teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team teamB) {
        this.teamB = teamB;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public void setTeamsOriginalPositions() {
        teamA.setOriginalPositions();
        teamB.setOriginalPositions();
        setChanged();
        notifyObservers(teamA);
    }

    public void setTeamsPositions(List<Position> teamAPositions, List<Position> teamBPositions) {

        teamA.setPositions(teamAPositions);
        teamB.setPositions(teamBPositions);

        setChanged();
        notifyObservers(teamA);
    }

    public void setTeamsPositions(List<Position> teamAPositions, List<Position> teamBPositions, Position ballPosition) {

        teamA.setPositions(teamAPositions);
        teamB.setPositions(teamBPositions);
        setBallPosition(ballPosition.x, ballPosition.y);

        setChanged();
        notifyObservers(teamA);
    }




    public void setBallPosition(float posX, float posY) {
        ball.setPosX(posX);
        ball.setPosY(posY);

        setChanged();
        notifyObservers(ball);
    }

    public Boolean isPlayersMoving() {
        return arePlayersMoving;
    }

    public void setPlayersMoving(Boolean arePlayersMoving) {
        this.arePlayersMoving = arePlayersMoving;
    }

    public Boolean everythingStopped() {
        if ((!getBall().isMoving()) &&
                (!isPlayersMoving())) {
            return true;
        } else return false;
    }


}
