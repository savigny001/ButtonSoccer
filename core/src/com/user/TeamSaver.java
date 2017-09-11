package com.user;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Json;
import com.soccer.pojo.Post;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeamSaver {

    //    private static String connectMessages = "jdbc:sqlite:C:/users/zoli/soccer/messages.sqlite";
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

    public static void saveTeamName(Integer teamId, String newTeamName) {
        openDatabase(connectSoccer);
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("UPDATE Teams SET name=? WHERE team_id =?");
            stmt.setString(1, newTeamName);
            stmt.setInt(2, teamId);
            stmt.executeUpdate();
            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void savePlayerData(Integer playerId, String firstName, String lastName, Post post) {
        openDatabase(connectSoccer);
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("UPDATE Players SET first_name=?, last_name=? WHERE player_id = ?");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setInt(3, playerId);
            stmt.executeUpdate();
            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveKitData(Integer kitId, String kitColorsJsonText) {
        openDatabase(connectSoccer);
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("UPDATE Kits SET " +
                    "col1_r=?, col1_g=?, col1_b=?, " +
                    "col2_r=?, col2_g=?, col2_b=?, " +
                    "col3_r=?, col3_g=?, col3_b=?, " +
                    "col4_r=?, col4_g=?, col4_b=?" +
                    " WHERE kit_id =?");

            Json json = new Json();
            ButtonKit kit = json.fromJson(ButtonKit.class, kitColorsJsonText);
            Integer index = 1;
            for (int i=0; i<4; i++) {
                stmt.setInt(index++, kit.getKitColors()[i].r);
                stmt.setInt(index++, kit.getKitColors()[i].g);
                stmt.setInt(index++, kit.getKitColors()[i].b);
            }
            stmt.setInt(13, kit.getId());
            stmt.executeUpdate();
            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void buyTeam(Integer userId, String teamJsonText) {
        openDatabase(connectSoccer);
        PreparedStatement stmt = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
//            Json json = new Json();
//            ButtonTeam team = json.fromJson(ButtonTeam.class, teamJsonText);
            statement = c.createStatement();
            rs = statement.executeQuery("SELECT MAX(team_id) AS maxID FROM Teams;");
            Integer newTeamId = rs.getInt("maxID") + 1;
            rs.close();
            statement.close();


            stmt = c.prepareStatement("INSERT INTO Teams VALUES (?,?,?)");
            stmt.setInt(1, newTeamId);
            stmt.setInt(2, userId);
            stmt.setString(3, "Default team");
            stmt.executeUpdate();
            stmt.close();

            for (int i=1; i<=16; i++) {
                stmt = c.prepareStatement("INSERT INTO Players VALUES (?,?,?,?,?,?,?,?,?)");
//              stmt.setString(1, playerId);
                stmt.setString(2, "Name");
                stmt.setString(3, "No_" + i);

                if ((i==1) || (i==12)) stmt.setString(4, "GK");
                else stmt.setString(4, "F");

                stmt.setInt(6, newTeamId);
                stmt.setInt(7, 200);
                stmt.setInt(8, 200);

                //kezdő-e a játékos
                if (i<=11) stmt.setBoolean(9, true);
                else stmt.setBoolean(9, false);

                stmt.executeUpdate();
                stmt.close();

            }

            for (int o=0; o<4; o++) {
                stmt = c.prepareStatement("INSERT INTO Kits VALUES (?,?,?, ?,?,?, ?,?,?, ?,?,?, ?,?,?)");
                stmt.setInt(2, newTeamId);

                switch (o) {
                    case 0: stmt.setString(3, "H"); for (int i=4; i<=15; i++) stmt.setInt(i, 120); break;
                    case 1: stmt.setString(3, "HGK"); for (int i=4; i<=15; i++) stmt.setInt(i, 15); break;
                    case 2: stmt.setString(3, "A"); for (int i=4; i<=15; i++) stmt.setInt(i, 200); break;
                    case 3: stmt.setString(3, "AGK"); for (int i=4; i<=15; i++) stmt.setInt(i, 100); break;
                }

                stmt.executeUpdate();
                stmt.close();

            }


            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
/*

SELECT MAX(team_id) AS maxID FROM Teams;

UPDATE teams SET name = "GOLD" WHERE  team_id = 2

INSERT INTO main.Match_requests VALUES (0,1,2,3)

DELETE FROM match_requests
WHERE request_id=3;

SELECT * FROM match_requests
WHERE teamB_user_id=0

SELECT * FROM booked_matches
WHERE userA_id=0 OR userB_id=0

 */
