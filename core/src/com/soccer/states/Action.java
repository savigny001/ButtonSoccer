package com.soccer.states;

public class Action implements State {
    @Override
    public ClientState getClientState() {
        ClientState clientState = new ClientState();
        clientState.stateType = StateType.ACTION;
        return clientState;
    }
}
