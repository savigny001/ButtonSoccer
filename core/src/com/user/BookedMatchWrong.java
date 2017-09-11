package com.user;

import java.util.Date;

public class BookedMatchWrong {
    private String opponentId;
    private Date matchDate;

    public BookedMatchWrong(String opponentId, Date matchDate) {
        this.opponentId = opponentId;
        this.matchDate = matchDate;
    }

    public String getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }
}
