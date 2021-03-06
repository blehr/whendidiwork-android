package com.brandonlehr.whendidiwork.services;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.CalendarList;
import com.brandonlehr.whendidiwork.models.CreateCalendarPostBody;
import com.brandonlehr.whendidiwork.models.CreateEventPostBody;
import com.brandonlehr.whendidiwork.models.CreateSheetPostBody;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.FileList;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.SheetDeleteResponse;
import com.brandonlehr.whendidiwork.models.TokenObject;
import com.brandonlehr.whendidiwork.models.UserResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by blehr on 2/7/2018.
 */

public interface ApiCalls {
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

    @POST("/api/createCalendar")
    Call<Calendar> createCalendar(@Body CreateCalendarPostBody newCalendar);

    @POST("/api/createSheet")
    Call<Sheet> createSheet(@Body CreateSheetPostBody newSheet);

    @POST("/api/createEvent/{calendarId}/{sheetId}")
    Call<Event> createEvent(@Path("calendarId") String calendarId, @Path("sheetId") String sheetId, @Body HashMap<String, CreateEventPostBody> event);

    @DELETE("/api/deleteEvent/{calendarId}/{eventId}")
    Call<List<SheetDeleteResponse>> deleteEvent(@Path("calendarId") String calendarId, @Path("eventId") String eventId);

    @PUT("/api/updateEvent/{calendarId}/{eventId}")
    Call<Event> updateEvent(@Path("calendarId") String calendarId, @Path("eventId") String eventId,  @Body HashMap<String, CreateEventPostBody> event);
}
