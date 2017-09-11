package com.soccer.model;


import com.user.BookedMatch;
import com.user.User;

import java.util.List;

public class MainModel {
    private List<User> users;
    private User user;
    private BookedMatch match;
    private String match_id;

    public List<User> getUsers() {
        return users;
    }

    public User getUserById(Integer id) {

        User foundUser = null;
        for (User actUser : users) {
            if (actUser.getId() == id) foundUser = actUser;
        }
        return foundUser;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookedMatch getMatch() {
        return match;
    }

    public void setMatch(BookedMatch match) {
        this.match = match;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }
}
