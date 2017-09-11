package com.user;

public class MatchReport {
    private BookedMatch bookedMatch;
    private Integer userAScore, userBScore;

    public MatchReport(BookedMatch bookedMatch, Integer userAScore, Integer userBScore) {
        this.bookedMatch = bookedMatch;
        this.userAScore = userAScore;
        this.userBScore = userBScore;
    }

    public BookedMatch getBookedMatch() {
        return bookedMatch;
    }

    public void setBookedMatch(BookedMatch bookedMatch) {
        this.bookedMatch = bookedMatch;
    }

    public Integer getUserAScore() {
        return userAScore;
    }

    public void setUserAScore(Integer userAScore) {
        this.userAScore = userAScore;
    }

    public Integer getUserBScore() {
        return userBScore;
    }

    public void setUserBScore(Integer userBScore) {
        this.userBScore = userBScore;
    }
}
