package com.soccer.situations;

import com.soccer.socevents.SocEvent;
import com.soccer.states.State;
import com.soccer.whattodo.WhatToDo;

import java.util.List;

public interface Situation {
    public State getWhatToDo(List<SocEvent> socEvents);
    public Situation getNextSituation();

}
