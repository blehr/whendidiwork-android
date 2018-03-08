package com.brandonlehr.whendidiwork;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.brandonlehr.whendidiwork.models.CreateEventPostBody;
import com.brandonlehr.whendidiwork.viewModels.CreateEventViewModel;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.HashMap;

import javax.inject.Inject;

public class CreateEventActivity extends AppCompatActivity {
    private static final String TAG = "CreateEventActivity";

    private EditText startDateEditText;
    private EditText startTimeEditText;
    private EditText endDateEditText;
    private EditText endTimeEditText;
    private EditText summaryEditText;
    private Button submitButton;
    String sheetName;
    String sheetId;
    String calendarId;
    String summaryPrefix;
    final Calendar c = Calendar.getInstance();
    final int year = c.get(Calendar.YEAR);
    final int month = c.get(Calendar.MONTH);
    final int day = c.get(Calendar.DAY_OF_MONTH);
    String format = "EEE MMM dd, yyyy";
    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    String startTime;
    String endTime;
    String startDate;
    String endDate;
    String note;
    String timeZone;


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    CreateEventViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Event");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent initiatingIntent = getIntent();
        if (initiatingIntent.getExtras() != null) {
            if (initiatingIntent.hasExtra("sheet_name")) {
                sheetName = initiatingIntent.getStringExtra("sheet_name");
                summaryPrefix = sheetName.substring(12).concat(": ");
            }
            if (initiatingIntent.hasExtra("sheet_id")) {
                sheetId = initiatingIntent.getStringExtra("sheet_id");
            }
            if (initiatingIntent.hasExtra("calendar_id")) {
                calendarId = initiatingIntent.getStringExtra("calendar_id");
            }
        }

        ((Whendidiwork) getApplication()).getGoogleClientComponent().inject(this);
        model = ViewModelProviders.of(this, viewModelFactory).get(CreateEventViewModel.class);

        startDateEditText = findViewById(R.id.start_date_edit_text);
        startTimeEditText = findViewById(R.id.start_time_edit_text);
        endDateEditText = findViewById(R.id.end_date_edit_text);
        endTimeEditText = findViewById(R.id.end_time_edit_text);
        summaryEditText = findViewById(R.id.summary_edit_text);
        submitButton = findViewById(R.id.submit_event);
        summaryEditText.setText(summaryPrefix);
        summaryEditText.setSelection(summaryPrefix.length());
        startDateEditText.setOnClickListener(view -> handleStartDateClick());
        startTimeEditText.setOnClickListener(view -> handleStartTimeClick());
        endDateEditText.setOnClickListener(view -> handleEndDateClick());
        endTimeEditText.setOnClickListener(view -> handleEndTimeClick());
        submitButton.setOnClickListener(view -> handleSubmit());
        summaryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < summaryPrefix.length()) {
                    s.replace(0, s.length(), summaryPrefix);
                }
            }
        });

    }

    private void handleStartDateClick() {
        hideKeyboard(this);
        new DatePickerDialog(CreateEventActivity.this, mStartDateListener, year, month, day).show();
    }

    private DatePickerDialog.OnDateSetListener mStartDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            c.set(Calendar.YEAR, i);
            c.set(Calendar.MONTH, i1);
            c.set(Calendar.DAY_OF_MONTH, i2);
            DateTime dateTime = new DateTime(c);
            startDate = fmt.print(dateTime);
            Log.d(TAG, "onDateSet: startDate ========= " + startDate);
            startDateEditText.setText(dateTime.toString(format));
        }
    };

    private void handleEndDateClick() {
        hideKeyboard(this);
        new DatePickerDialog(CreateEventActivity.this, mEndDateListener, year, month, day).show();
    }

    private DatePickerDialog.OnDateSetListener mEndDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            c.set(Calendar.YEAR, i);
            c.set(Calendar.MONTH, i1);
            c.set(Calendar.DAY_OF_MONTH, i2);
            DateTime dateTime = new DateTime(c);
            endDate = fmt.print(dateTime);
            endDateEditText.setText(dateTime.toString(format));
        }
    };

    private void handleStartTimeClick() {
        hideKeyboard(this);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        new TimePickerDialog(CreateEventActivity.this, mStartTimeListener, hour, minute, false).show();
    }

    private TimePickerDialog.OnTimeSetListener mStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            c.set(Calendar.HOUR, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            DateTime dateTime = new DateTime(c);
            startTime = fmt.print(dateTime);
            Log.d(TAG, "onTimeSet: starttime ================= " + startTime);
            String time = convertTo12HourFormat(hourOfDay, minute);
            startTimeEditText.setText(time);
        }
    };

    private void handleEndTimeClick() {
        hideKeyboard(this);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        new TimePickerDialog(CreateEventActivity.this, mEndTimeListener, hour, minute, false).show();
    }

    private TimePickerDialog.OnTimeSetListener mEndTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            c.set(Calendar.HOUR, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            DateTime dateTime = new DateTime(c);
            endTime = fmt.print(dateTime);
            String time = convertTo12HourFormat(hourOfDay, minute);
            endTimeEditText.setText(time);
        }
    };

    private String getNote() {
        return summaryEditText.getText().toString();
    }

    private void handleSubmit() {
        if (startDate == null || endDate == null || startTime == null || endTime == null) {
            Log.d(TAG, "handleSubmit: ALL is NULL =====================");
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storedPrefs), Context.MODE_PRIVATE);
        String userTimeZone = sharedPreferences.getString("timeZone", "");
        note = getNote();
        CreateEventPostBody event = new CreateEventPostBody(endDate, startDate, endTime, startTime, userTimeZone, note);

        HashMap<String, CreateEventPostBody> postBody = new HashMap<>();
        postBody.put("event", event);

        model.makeEvent(calendarId, sheetId, postBody).observe(this, returnEvent -> {
            Toast.makeText(this, returnEvent.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    // To convert in AM PM format
    private String convertTo12HourFormat(int hours, int mins) {
        String am_pm = "";
        if (hours > 12) {
            hours -= 12;
            am_pm = "PM";
        } else if (hours == 0) {
            hours += 12;
            am_pm = "AM";
        } else if (hours == 12)
            am_pm = "PM";
        else
            am_pm = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder

        return String.valueOf(hours) + ':' +
                minutes + " " + am_pm;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
