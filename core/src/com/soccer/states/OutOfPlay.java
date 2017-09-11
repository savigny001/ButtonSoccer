package com.soccer.states;

public class OutOfPlay implements State {
    @Override
    public ClientState getClientState() {
        ClientState clientState = new ClientState();
        clientState.stateType = StateType.OUTOFPLAY;
        return clientState;
    }
}
