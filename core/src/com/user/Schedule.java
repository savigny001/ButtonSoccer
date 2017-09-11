package com.user;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Schedule {

    private List<BookedMatchWrong> bookedMatches;

    public Schedule() {
        bookedMatches = new ArrayList<BookedMatchWrong>();
    }

    public void addMatch(String opponentId, Date date) {
        BookedMatchWrong bookedMatch = new BookedMatchWrong(opponentId, date);
        bookedMatches.add(bookedMatch);
    }

    public void removeMatch(BookedMatchWrong removedMatch) {
        bookedMatches.remove(removedMatch);
    }

}
