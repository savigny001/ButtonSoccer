package com.user;

import com.badlogic.gdx.utils.Json;
import com.soccer.pojo.Player;
import com.soccer.pojo.Position;
import com.soccer.pojo.Post;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserLoader {

    private static String username, password;
    private static String connect = "jdbc:sqlite:C:/Users/Zoli/soccer/soccer.sqlite";
    private static Connection c;

    private static List<String> users;
    private static User user;
    private static Integer actualUserId;

    private static void openDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(connect);
            c.setAutoCommit(false);
            System.out.println("Database has opened successfully.");
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
            System.out.println("Database hasnt opened successfully.");
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("Database hasnt opened successfully.");
        }
    }

    public static List<String> loadAllUsers() {

        openDatabase();

        ResultSet rs = null;
        Statement stmt = null;

        users = new ArrayList<String>();

        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT id, username, elo_point, photopath\n" +
                    "FROM users, users_data\n" +
                    "WHERE user_id = id");

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("username");
                Integer eloPoint = rs.getInt("elo_point");

                System.out.println(name + " " + id);
                User user = new User(id, name, eloPoint);
                Json json = new Json();

                users.add(json.toJson(user));

            }
            rs.close();
            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static String loadActualUser(Integer userId) {
        actualUserId = userId;
        openDatabase();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT id, username, elo_point, photopath\n" +
                    "FROM users, users_data\n" +
                    "WHERE id =" + userId );

            while ( rs.next() ) {
                Integer id = rs.getInt("id");
                String name = rs.getString("username");
                System.out.println(name + " " + id);
                user = new User(id, name);
                user.setEloPoint(rs.getInt("elo_point"));

            }
            rs.close();

            rs = stmt.executeQuery("SELECT * FROM teams\n" +
                    "WHERE user_id = " + actualUserId);

//            List<ButtonTeam> teams = new ArrayList<ButtonTeam>();
            ButtonTeam team = null;
            while (rs.next()) {
                team = new ButtonTeam();

                team.setId(rs.getInt("team_id"));
                team.setName(rs.getString("name"));


                //players
                Statement stmt2 = c.createStatement();
                ResultSet rs2 = stmt2.executeQuery("SELECT * FROM players\n" +
                        "WHERE team_id=" + team.getId());
                Player player = null;
                while (rs2.next()) {

                    player = new Player();
                    player.setId(rs2.getInt("player_id"));
                    player.setFirstName(rs2.getString("first_name"));
                    player.setLastName(rs2.getString("last_name"));
                    player.setPhotoPath(rs2.getString("photo_path"));

                    if (rs2.getString("post").equals("GK")) player.setPost(Post.GOALKEEPER);
                        else player.setPost(Post.OUTFIELD_PLAYER);

                    Position pos = new Position(rs2.getFloat("pos_x"), rs2.getFloat("pos_y"));
                    player.setPosition(pos);

                    team.getPlayers().add(player);
                }
                rs2.close();
                stmt2.close();


                //kits

                rs2 = stmt2.executeQuery("SELECT * FROM kits\n" +
                        "WHERE team_id=" + team.getId());

                ButtonKit kit = null;
                while (rs2.next()) {
                    kit = new ButtonKit();
                    kit.setId(rs2.getInt("kit_id"));

                    KitColor col1 = new KitColor();
                    col1.r = rs2.getInt("col1_r");
                    col1.g = rs2.getInt("col1_g");
                    col1.b = rs2.getInt("col1_b");

                    KitColor col2 = new KitColor();
                    col2.r = rs2.getInt("col2_r");
                    col2.g = rs2.getInt("col2_g");
                    col2.b = rs2.getInt("col2_b");

                    KitColor col3 = new KitColor();
                    col3.r = rs2.getInt("col3_r");
                    col3.g = rs2.getInt("col3_g");
                    col3.b = rs2.getInt("col3_b");

                    KitColor col4 = new KitColor();
                    col4.r = rs2.getInt("col4_r");
                    col4.g = rs2.getInt("col4_g");
                    col4.b = rs2.getInt("col4_b");

                    KitColor [] kitColors = {col1, col2, col3, col4};
                    kit.setKitColors(kitColors);

                    if (rs2.getString("type").equals("H")) team.setHomeF(kit);
                    if (rs2.getString("type").equals("HGK")) team.setHomeGK(kit);
                    if (rs2.getString("type").equals("A")) team.setAwayF(kit);
                    if (rs2.getString("type").equals("AGK")) team.setAwayGK(kit);
                }
                rs2.close();
                stmt2.close();

                user.addTeam(team);
            }
            rs.close();

            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Json json = new Json();
        String jsonText = json.toJson(user);

        System.out.println("JSON:\n");
        System.out.println(json.prettyPrint(user));

        User user2 = json.fromJson(User.class, jsonText);

        return jsonText;

    }



    public static String loadTeam(Integer teamId) {
        openDatabase();
        Statement stmt = null;
        ResultSet rs = null;
        String jsonText = "";

        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM teams\n" +
                    "WHERE team_id = " + teamId);

            ButtonTeam team = null;
            while (rs.next()) {
                team = new ButtonTeam();

                team.setId(rs.getInt("team_id"));
                team.setName(rs.getString("name"));


                //players
                Statement stmt2 = c.createStatement();
                ResultSet rs2 = stmt2.executeQuery("SELECT * FROM players\n" +
                        "WHERE team_id=" + team.getId());
                Player player = null;
                while (rs2.next()) {

                    player = new Player();
                    player.setId(rs2.getInt("player_id"));
                    player.setFirstName(rs2.getString("first_name"));
                    player.setLastName(rs2.getString("last_name"));
                    player.setPhotoPath(rs2.getString("photo_path"));

                    if (rs2.getString("post").equals("GK")) player.setPost(Post.GOALKEEPER);
                    else player.setPost(Post.OUTFIELD_PLAYER);

                    Position pos = new Position(rs2.getFloat("pos_x"), rs2.getFloat("pos_y"));
                    player.setPosition(pos);

                    team.getPlayers().add(player);
                }
                rs2.close();
                stmt2.close();


                //kits

                rs2 = stmt2.executeQuery("SELECT * FROM kits\n" +
                        "WHERE team_id=" + team.getId());

                ButtonKit kit = null;
                while (rs2.next()) {
                    kit = new ButtonKit();

                    KitColor col1 = new KitColor();
                    col1.r = rs2.getInt("col1_r");
                    col1.g = rs2.getInt("col1_g");
                    col1.b = rs2.getInt("col1_b");

                    KitColor col2 = new KitColor();
                    col2.r = rs2.getInt("col2_r");
                    col2.g = rs2.getInt("col2_g");
                    col2.b = rs2.getInt("col2_b");

                    KitColor col3 = new KitColor();
                    col3.r = rs2.getInt("col3_r");
                    col3.g = rs2.getInt("col3_g");
                    col3.b = rs2.getInt("col3_b");

                    KitColor col4 = new KitColor();
                    col4.r = rs2.getInt("col4_r");
                    col4.g = rs2.getInt("col4_g");
                    col4.b = rs2.getInt("col4_b");

                    KitColor [] kitColors = {col1, col2, col3, col4};
                    kit.setKitColors(kitColors);

                    if (rs2.getString("type").equals("H")) team.setHomeF(kit);
                    if (rs2.getString("type").equals("HGK")) team.setHomeGK(kit);
                    if (rs2.getString("type").equals("A")) team.setAwayF(kit);
                    if (rs2.getString("type").equals("AGK")) team.setAwayGK(kit);
                }
                rs2.close();
                stmt2.close();


                Json json = new Json();
                jsonText = json.toJson(team);

                System.out.println("JSON:\n");
                System.out.println(json.prettyPrint(team));




            }
            rs.close();

            stmt.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }



        return jsonText;

    }







}

/*

SELECT id, username, elo_point, photopath
FROM users, users_data
WHERE user_id = id

SELECT * FROM teams
WHERE user_id = 0

SELECT * FROM kits
WHERE team_id=0


 */
