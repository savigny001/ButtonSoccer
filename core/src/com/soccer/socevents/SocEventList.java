package com.soccer.socevents;

import com.soccer.states.State;

import java.util.List;

public class SocEventList {

    public static Boolean isContains (List<SocEvent> socEvents, Class... classes) {
        Boolean isContainsAll = true;

        for (Class thisClass : classes) {
            Boolean isContainsThis = false;
            for (SocEvent socEvent : socEvents) {
                if (thisClass.isInstance(socEvent)) isContainsThis = true;
            }
            if (!isContainsThis) isContainsAll = false;
        }
        return isContainsAll;
    }

    public static Boolean isContainsMoreTimes (List<SocEvent> socEvents, Integer occurancyTime, Class... classes) {
        Boolean isContainsAll = true;

        for (Class thisClass : classes) {
            Boolean isContainsThis = false;
            Integer howMany = 0;
            for (SocEvent socEvent : socEvents) {
                if (thisClass.isInstance(socEvent)) howMany++;
            }
            if (howMany == occurancyTime) isContainsThis = true;
            if (!isContainsThis) isContainsAll = false;
        }
        return isContainsAll;
    }

    public static Integer howManyTimesContains (List<SocEvent> socEvents, Class classe) {
            Integer howMany = 0;
            for (SocEvent socEvent : socEvents) {
                if (classe.isInstance(socEvent)) howMany++;
            }
        return howMany;
    }

    public static Boolean isContainsMinimumMoreTimes (List<SocEvent> socEvents, Integer occurancyTime, Class... classes) {
        Boolean isContainsAll = true;

        for (Class thisClass : classes) {
            Boolean isContainsThis = false;
            Integer howMany = 0;
            for (SocEvent socEvent : socEvents) {
                if (thisClass.isInstance(socEvent)) howMany++;
            }
            if (howMany >= occurancyTime) isContainsThis = true;
            if (!isContainsThis) isContainsAll = false;
        }
        return isContainsAll;
    }

}
