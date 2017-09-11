package com.user;

/**
 * Created by Zoli on 2017.05.10..
 */

public class MatchRequest {
    private Integer challengerUserId, chosenUserId, whosStadionId;
    private String dateString;
    private Integer id;
    private String requestText = "";

    public MatchRequest(Integer challengerUserId, Integer chosenUserId, Integer whosStadionId, String dateString, Integer id) {
        this.challengerUserId = challengerUserId;
        this.chosenUserId = chosenUserId;
        this.whosStadionId = whosStadionId;
        this.dateString = dateString;
        this.id=id;
    }

    public MatchRequest(Integer challengerUserId, Integer chosenUserId, Integer whosStadionId, String dateString) {
        this(challengerUserId, chosenUserId, whosStadionId, dateString, -1);
    }

    public Integer getChallengerUserId() {
        return challengerUserId;
    }

    public void setChallengerUserId(Integer challengerUserId) {
        this.challengerUserId = challengerUserId;
    }

    public Integer getChosenUserId() {
        return chosenUserId;
    }

    public void setChosenUserId(Integer chosenUserId) {
        this.chosenUserId = chosenUserId;
    }

    public Integer getWhosStadionId() {
        return whosStadionId;
    }

    public void setWhosStadionId(Integer whosStadionId) {
        this.whosStadionId = whosStadionId;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return requestText;
    }
}



