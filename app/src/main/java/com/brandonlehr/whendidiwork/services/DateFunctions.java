package com.brandonlehr.whendidiwork.services;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Created by blehr on 2/18/2018.
 */

public class DateFunctions {
    private static final String TAG = "DateFunctions";

    public static String displayDateTime(String date) {
        DateTime dt = new DateTime( date ) ;

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("EEEEE MMMMM dd, yyyy hh:mma");
        return dtfOut.print(dt);
    }
    public static String displayDate(String date) {
        DateTime dt = new DateTime( date ) ;

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("EEEEE MMMMM dd, yyyy");
        return dtfOut.print(dt);
    }
}

