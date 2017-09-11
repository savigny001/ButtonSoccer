package com.user;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PasswordCheck {

    private static String username, password;
    private static String connect = "jdbc:sqlite:C:/Users/Zoli/soccer/soccer.sqlite";
    private static Connection c;
    private static Integer userId = -1;


    public static Integer isCorrectPassword (String usernameParam, String passwordParam) {
        userId = -1;
        username = usernameParam;
        password = passwordParam;
        openDatabase();
        checkPassword();
        System.out.println("USERID_inPasswordCheck: " + userId);
        return userId;
    }

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

    private static void checkPassword () {

        Boolean isRight = false;

        Statement stmt = null;
        ResultSet rs = null;
        System.out.println("Check");


        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM Users_data;" );
            String actPassword = null;
            String actUsername = null;

            System.out.println("Check-2");
//
            while ( rs.next() ) {

                System.out.println("Check-3");

                actUsername = rs.getString("USERNAME");
                actPassword = rs.getString("PASSWORD");

                if ((actUsername.equals(username)) && (actPassword.equals(password))) {
                    userId = rs.getInt("ID");
                    System.out.println("Check-ok");
                }
            }



        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

}
