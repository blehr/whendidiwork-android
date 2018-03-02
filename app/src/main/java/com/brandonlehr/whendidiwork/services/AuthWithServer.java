package com.brandonlehr.whendidiwork.services;

import com.brandonlehr.whendidiwork.models.CalendarList;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.FileList;
import com.brandonlehr.whendidiwork.models.TokenObject;
import com.brandonlehr.whendidiwork.models.UserResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by blehr on 2/7/2018.
 */

public interface AuthWithServer {
    @POST("/auth/mobilelogin")
    Call<UserResponse> sendToken(@Body TokenObject code);

    @GET("/api/getCalendarList")
    Call<CalendarList> getCalendarList();

    @GET("/api/getEvents/{calendarId}")
    Call<List<Event>> getEvents(@Path("calendarId") String calendarId);

    @GET("/api/getFiles")
    Call<FileList> getFiles();

    @GET("/api/getSheetMeta/{sheetId}")
    Call<ArrayList<String[]>> getSheetMeta(@Path("sheetId") String sheetId);

}
