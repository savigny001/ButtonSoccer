package com.user;


public class BookedMatch {
    private Integer teamAId, teamBId, whosStadionId;
    private String dateString;
    private Integer id;
    private String matchText = "";

    public BookedMatch() {
    }

    public BookedMatch(Integer teamAId, Integer teamBId, Integer whosStadionId, String dateString, Integer id) {
        this.teamAId = teamAId;
        this.teamBId = teamBId;
        this.whosStadionId = whosStadionId;
        this.dateString = dateString;
        this.id=id;
    }

    public Integer getTeamAId() {
        return teamAId;
    }

    public void setTeamAId(Integer teamAId) {
        this.teamAId = teamAId;
    }

    public Integer getTeamBId() {
        return teamBId;
    }

    public void setTeamBId(Integer teamBId) {
        this.teamBId = teamBId;
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

    public String getMatchText() {
        return matchText;
    }

    public void setMatchText(String matchText) {
        this.matchText = matchText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return matchText;
    }
}



