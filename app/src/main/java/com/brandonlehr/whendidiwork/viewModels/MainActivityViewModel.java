package com.brandonlehr.whendidiwork.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.SigninTime;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.repository.CalendarRepository;
import com.brandonlehr.whendidiwork.repository.EventRepository;
import com.brandonlehr.whendidiwork.repository.SheetRepository;
import com.brandonlehr.whendidiwork.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by blehr on 2/17/2018.
 */

public class MainActivityViewModel extends ViewModel {
    private static final String TAG = "MainActivityViewModel";

    private LiveData<List<Calendar>> mCalendars;
    private LiveData<List<Sheet>> mSheets;
    private LiveData<List<Event>> mEvents;
    private LiveData<Calendar> mSelectedCalendar;
    private LiveData<Sheet> mSelectedSheet;
    private LiveData<UserResponse> mUser;
    private LiveData<TimeZone> mTimeZone;
    private LiveData<SigninTime> mSigninTime;

    CalendarRepository mCalendarRepository;
    SheetRepository mSheetRepository;
    EventRepository mEventRepository;
    UserRepository mUserRepository;

    @Inject
    public MainActivityViewModel(CalendarRepository calendarRepository, SheetRepository sheetRepository, EventRepository eventRepository, UserRepository userRepository) {
        mCalendarRepository = calendarRepository;
        mSheetRepository = sheetRepository;
        mEventRepository = eventRepository;
        mUserRepository = userRepository;
        mCalendars = calendarRepository.getCalendars();
        mSelectedCalendar = calendarRepository.getSelectedCalendar();
        mSheets = mSheetRepository.getSheets();
        mSelectedSheet = mSheetRepository.getSelectedSheet();
        mUser = mUserRepository.getUser();
        mEvents = mEventRepository.subscribeToEvents();
        mTimeZone = mCalendarRepository.getTimeZone();
        mSigninTime = mUserRepository.getSigninTime();
    }

    public void InitialDataLoad() {
        mCalendarRepository.fetchCalendarList();
        mSheetRepository.fetchFiles();
    }

    public LiveData<SigninTime> getSigninTime() {
        return mSigninTime;
    }

    public void insertSigninTime(SigninTime timestamp) {
        mUserRepository.insertSigninTime(timestamp);
    }

    public void deleteSigninTime() {
        mUserRepository.deleteSigninTime();
    }

    public LiveData<List<Calendar>> retrieveCalendars() {
        return mCalendars;
    }

    public LiveData<Calendar> retrieveSelectedCalendar() {
        return mSelectedCalendar;
    }

    public void updateSelectedCalendar(Calendar calendar) {
        mCalendarRepository.updateSelectedCalendar(calendar);
    }

    public LiveData<List<Sheet>> retrieveSheets() {
        return mSheets;
    }

    public LiveData<Sheet> retrieveSelectedSheet() {
        return mSelectedSheet;
    }

    public void updateSelectedSheet(Sheet sheet) {
        mSheetRepository.updateSelectedSheet(sheet);
    }

    public void retrieveEvents(String id) {
        mEventRepository.fetchEvents(id);
    }

    public LiveData<List<Event>> subscribeToEvents() {
        return mEvents;
    }

    public LiveData<UserResponse> getUser() {
        return mUser;
    }

    public LiveData<TimeZone> getTimeZone() {
        return mTimeZone;
    }
    public void deleteEvent(String calendarId, String eventId) {
        mEventRepository.deleteEvent(calendarId, eventId);
    }

}
