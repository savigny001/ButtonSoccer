package com.user;

import com.badlogic.gdx.utils.Json;

import java.awt.print.Book;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zoli on 2017.05.09..
 */

public class MessageCenter {

    private static String connectMessages = "jdbc:sqlite:C:/users/zoli/soccer/messages.sqlite";
    private static String connectSoccer = "jdbc:sqlite:C:/users/zoli/soccer/soccer.sqlite";

    private static Connection c;
    private static MatchRequest matchRequest;


    private static void openDatabase(String connect) {
        try {
//            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(connect);
//            c.setAutoCommit(false);
            System.out.println("Database has opened successfully.");
//        } catch (ClassNotFoundException e) {
////            e.printStackTrace();
//            System.out.println("Database hasnt opened successfully.");
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Database hasnt opened successfully.");
        }
    }

    public static void sendMatchRequest(MatchRequest matchRequestToSend) {
        matchRequest = matchRequestToSend;
        openDatabase(connectMessages);
        saveMessage();
    }

    public static void refuseRequest(Integer requestId) {
        openDatabase(connectMessages);
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("DELETE FROM match_requests\n" +
                    "WHERE request_id="+ requestId+";");
//            stmt.setInt(1, requestId);
//            stmt.setInt(2, matchRequest.getTeamBId());
//            stmt.setString(3, matchRequest.getDateString());
//            stmt.setInt(5, matchRequest.getWhosStadionId());
            stmt.executeUpdate();

            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static List<MatchRequest> getMatchRequests(User user) {

        openDatabase(connectMessages);
        ResultSet rs = null;
        Statement stmt = null;

        List<MatchRequest> matchRequests = new ArrayList<MatchRequest>();

        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM match_requests\n" +
                    "WHERE teamB_user_id=" + user.getId());

            while (rs.next()) {
                Integer id = rs.getInt("request_id");
                Integer challangerId = rs.getInt("teamA_user_id");
                Integer whosStadionId = rs.getInt("whos_stadion");
                String dateString = rs.getString("date");
                matchRequests.add(new MatchRequest(challangerId, user.getId(), whosStadionId, dateString, id));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchRequests;
    }



    public static void saveMessage() {

        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT INTO Match_requests VALUES (?,?,?,?,?)");
            stmt.setInt(1, matchRequest.getChallengerUserId());
            stmt.setInt(2, matchRequest.getChosenUserId());
            stmt.setString(3, matchRequest.getDateString());
            stmt.setInt(5, matchRequest.getWhosStadionId());
            stmt.executeUpdate();

            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void acceptRequest(MatchRequest matchRequest) {

        openDatabase(connectSoccer);
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT INTO Booked_matches VALUES (?,?,?,?,?)");
            stmt.setString(2, matchRequest.getDateString());
            stmt.setInt(3, matchRequest.getChallengerUserId());
            stmt.setInt(4, matchRequest.getChosenUserId());
            stmt.setInt(5, matchRequest.getWhosStadionId());
            stmt.executeUpdate();

            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<BookedMatch> getBookedMatches(User user) {

        openDatabase(connectSoccer);
        ResultSet rs = null;
        Statement stmt = null;

        List<BookedMatch> bookedMatches = new ArrayList<BookedMatch>();

        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM booked_matches\n" +
                    "WHERE userA_id=" + user.getId() +" OR userB_id=" + user.getId());

            while (rs.next()) {
                Integer id = rs.getInt("booked_match_id");
                Integer challangerId = rs.getInt("userA_id");
                Integer chosenId = rs.getInt("userB_id");
                Integer whosStadionId = rs.getInt("whos_stadion");
                String dateString = rs.getString("date");
                bookedMatches.add(new BookedMatch(challangerId, chosenId, whosStadionId, dateString, id));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookedMatches;
    }

    public static List<String> getAllBookedMatches() {

        openDatabase(connectSoccer);
        ResultSet rs = null;
        Statement stmt = null;

        List<String> bookedMatches = new ArrayList();

        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM booked_matches");

            while (rs.next()) {
                Integer id = rs.getInt("booked_match_id");
                Integer challangerId = rs.getInt("userA_id");
                Integer chosenId = rs.getInt("userB_id");
                Integer whosStadionId = rs.getInt("whos_stadion");
                String dateString = rs.getString("date");

                BookedMatch bookedMatch = new BookedMatch(challangerId, chosenId, whosStadionId, dateString, id);
                Json json = new Json();
                bookedMatches.add(json.toJson(bookedMatch));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookedMatches;
    }



}

/*

INSERT INTO main.Match_requests VALUES (0,1,2,3)

DELETE FROM match_requests
WHERE request_id=3;

SELECT * FROM match_requests
WHERE teamB_user_id=0

SELECT * FROM booked_matches
WHERE userA_id=0 OR userB_id=0

 */
