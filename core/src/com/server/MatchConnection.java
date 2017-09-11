package com.server;

import com.esotericsoftware.kryonet.Connection;
import com.user.UserRole;

public class MatchConnection extends Connection {
    public String match_id;
    public Integer user_id;
    public UserRole userRole;
    public Integer team_id;
}
