package com.user;


import java.text.DateFormat;
import java.util.Date;

public class DateHandler {

    public static String convertDateToStringForSQL(Date date) {
        String dateString = "";

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dateString = dateFormat.format(date);

        dateString = dateString.replaceAll("\\.","-");
        dateString = dateString.concat(":00");

        StringBuffer dateStringBuffer = new StringBuffer(dateString);
        dateStringBuffer.deleteCharAt(10);
        dateString = dateStringBuffer.toString();

        return dateString;
    }

//    public static void main(String args[]) {
//        System.out.println(convertDateToStringForSQL(new Date()));
//    }
}
