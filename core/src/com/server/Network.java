package com.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.soccer.aiming.ClientAiming;
import com.soccer.aiming.ClientMoving;
import com.soccer.aiming.ClientPlaceing;
import com.soccer.pojo.Position;
import com.soccer.pojo.TeamSide;
import com.soccer.socevents.ClientSocEvent;
import com.soccer.socevents.GoalLineSide;
import com.soccer.socevents.SocEventType;
import com.soccer.states.ClientState;
import com.soccer.states.StateType;
import com.user.MessageType;
import com.user.UserRole;

import java.util.ArrayList;
import java.util.List;

public class Network {
    static public final int port = 54555;

    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Message.class);
        kryo.register(RegisterUser.class);
        kryo.register(UserRole.class);
        kryo.register(ClientAiming.class);
        kryo.register(ClientPlaceing.class);
        kryo.register(Position.class);
        kryo.register(ArrayList.class);
        kryo.register(LoginRequest.class);
        kryo.register(LoginResponse.class);
        kryo.register(String.class);
        kryo.register(Integer.class);
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(UserLoaderRequest.class);
        kryo.register(UserLoaderResponse.class);

        kryo.register(TeamLoaderRequest.class);
        kryo.register(TeamLoaderResponse.class);

        kryo.register(MessageResponse.class);
        kryo.register(MessageRequest.class);
        kryo.register(MessageType.class);
        kryo.register(ClientMoving.class);

        kryo.register(ClientSocEvent.class);
        kryo.register(SocEventType.class);
        kryo.register(TeamSide.class);
        kryo.register(GoalLineSide.class);

        kryo.register(ClientState.class);
        kryo.register(ClientState.class);
        kryo.register(StateType.class);

        kryo.register(ConnectedUsers.class);

    }
}