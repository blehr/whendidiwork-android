package com.brandonlehr.whendidiwork;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.CurrentUser;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.viewModels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class FirstScreenFragment extends Fragment {
    private static final String TAG = "FirstScreenFragment";

    private Spinner calendarSelect;
    private Spinner sheetSelect;
    private List<Calendar> mCalendars;
    private List<Sheet> mSheets;
    private List<Event> mEvents;
    private Calendar mCalendar = null;
    private Sheet mSheet = null;
    private String sheetId;
    private String calendarId;
    private RecyclerView mRecyclerView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    MainActivityViewModel model;
    SwipeController swipeController;
    CalendarEventListRecyclerViewAdapter mAdapter;

    private OnFragmentInteractionListener mListener;
    private OnListFragmentInteractionListener mListListener;

    public FirstScreenFragment() {
        // Required empty public constructor
    }

    public static FirstScreenFragment newInstance(String calendarId, String sheetId) {
        FirstScreenFragment fragment = new FirstScreenFragment();
        Bundle args = new Bundle();
        args.putString("calendarId", calendarId);
        args.putString("sheetId", sheetId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle args = getArguments();
            calendarId = args.getString("calendarId", null);
            sheetId = args.getString("sheetId", null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_screen, container, false);

        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.calendarEventList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((Whendidiwork) getActivity().getApplication()).getGoogleClientComponent().inject(this);
        model = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);

        model.getCalendar().observe(FirstScreenFragment.this, this::setCalendarInUse);
        model.getSheet().observe(FirstScreenFragment.this, this::setSheetInUse);
        model.getCalendarList().observe(FirstScreenFragment.this, this::handleCalendarList);
        model.getFiles().observe(FirstScreenFragment.this, this::getFiles);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.calendarEventList);
        calendarSelect = view.findViewById(R.id.calendarSelect);
        sheetSelect = view.findViewById(R.id.sheetSelect);


        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                mEvents.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position) {
//                super.onLeftClicked(position);
                Log.d(TAG, "onLeftClicked: position " + position);
                Event eventToEdit = mEvents.get(position);
            }
        }, getActivity().getApplication());
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }






    public void handleCalendarList(ArrayList<Calendar> calendars) {
        mCalendars = calendars;

        setTimzoneInPrefrences(calendars);

        String lastUsedCalendar = CurrentUser.getUserResponse().getLastUsed().getCalendar();

        if (calendarId != null) {
            Calendar calendar = getCalendarById(mCalendars, calendarId);
            int index = mCalendars.indexOf(calendar);
            if (index != 0) {
                mCalendars.remove(calendar);
                mCalendars.add(0, calendar);
            }
        } else if (!lastUsedCalendar.equals("") && mCalendar == null) {
            Log.d(TAG, "handleCalendarList: lastused amd no mCalendar");
            Calendar calendar = getCalendarById(mCalendars, lastUsedCalendar);
            int index = mCalendars.indexOf(calendar);
            if (index != 0) {
                mCalendars.remove(calendar);
                mCalendars.add(0, calendar);
            }
        } else if (mCalendar != null) {
            Log.d(TAG, "handleCalendarList: mCalendar is not null" + mCalendar.toString());
            int index = mCalendars.indexOf(mCalendar);
            if (index != 0) {
                mCalendars.remove(mCalendar);
                mCalendars.add(0, mCalendar);
            }
        }




        CalendarListAdapter adapter = new CalendarListAdapter(getContext(), R.layout.spinner_calendar_layout, mCalendars);


        calendarSelect.setAdapter(adapter);

        calendarSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar cal = (Calendar) calendars.get(position);
                model.setCalendar(cal);
                Toast.makeText(getContext(), cal.getSummary(), Toast.LENGTH_LONG).show();
                model.getCalendarEvents(cal.getId()).observe(FirstScreenFragment.this, events -> handleCalendarEvents(events));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void getFiles(ArrayList<Sheet> sheets) {
        mSheets = sheets;

        String lastUsedSheet = CurrentUser.getUserResponse().getLastUsed().getSheet();

        if (sheetId != null) {
            Sheet sheet = getSheetById(mSheets, sheetId);
            int index = mSheets.indexOf(sheet);
            if (index != 0) {
                mSheets.remove(sheet);
                mSheets.add(0, sheet);
            }
        } else if (!lastUsedSheet.equals("") && mSheet == null) {
            Sheet sheet = getSheetById(mSheets, lastUsedSheet);
            int index = mSheets.indexOf(sheet);
            if (index != 0) {
                mSheets.remove(sheet);
                mSheets.add(0, sheet);
            }
        } else if (mSheet != null) {
            Log.d(TAG, "handleCalendarList: mSheet is not null" + mSheet.toString());
            int index = mSheets.indexOf(mSheet);
            if (index != 0) {
                mSheets.remove(mSheet);
                mSheets.add(0, mSheet);
            }
        }

        FileListAdapter adapter = new FileListAdapter(getContext(), R.layout.spinner_calendar_layout, mSheets);
        sheetSelect.setAdapter(adapter);

        sheetSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sheet sheet = (Sheet) mSheets.get(position);
                model.setSheet(sheet);
                Toast.makeText(getContext(), sheet.getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void mListListener() {

    }

    public void handleCalendarEvents(ArrayList<Event> events) {
        mEvents = events;

        mRecyclerView.setAdapter(new CalendarEventListRecyclerViewAdapter(mEvents, mListListener));
        mAdapter = (CalendarEventListRecyclerViewAdapter) mRecyclerView.getAdapter();
    }

    public void setCalendarInUse(Calendar calendar) {
        Log.d(TAG, "setCalendarInUse: cal = " + calendar.toString());
        mCalendar = calendar;
    }
    public void setSheetInUse(Sheet sheet) {
        Log.d(TAG, "setSheetInUse: sheet = " + sheet.toString());
        mSheet = sheet;
    }
    public Calendar getCalendarById(List<Calendar> calendars, String id) {
        Calendar selectedCalendar = null;
        for (Calendar calendar : calendars) {
            if (calendar.getId().equals(id)) {
                selectedCalendar = calendar;
            }
        }
        return selectedCalendar;
    }
    public Sheet getSheetById(List<Sheet> sheets, String id) {
        Sheet selectedSheet = null;
        for (Sheet sheet : sheets) {
            if (sheet.getId().equals(id)) {
                selectedSheet = sheet;
            }
        }
        return selectedSheet;
    }

    private void setTimzoneInPrefrences(List<Calendar> calendars) {
        String primaryTimezone = "";
        for (Calendar cal : calendars) {
            if (cal.getPrimary() != null && cal.getPrimary() == true) {
                primaryTimezone = cal.getTimeZone();
            }
        }
        Log.d(TAG, "setTimzoneInPrefrences: TIMEZONE=================" + primaryTimezone);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.storedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("timeZone", primaryTimezone);
        editor.apply();
    }


    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mListListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Event item);
    }
}
