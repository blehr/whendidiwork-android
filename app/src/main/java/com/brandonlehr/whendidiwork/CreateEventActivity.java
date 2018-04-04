package com.brandonlehr.whendidiwork;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.brandonlehr.whendidiwork.models.CreateEventPostBody;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.SigninTime;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.models.TokenObject;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.models.UserTimer;
import com.brandonlehr.whendidiwork.services.ApiCalls;
import com.brandonlehr.whendidiwork.viewModels.CreateEventViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateEventActivity extends AppCompatActivity implements Callback<UserResponse> {
    private static final String TAG = "CreateEventActivity";

    private EditText startDateEditText;
    private EditText startTimeEditText;
    private EditText endDateEditText;
    private EditText endTimeEditText;
    private EditText summaryEditText;
    private Button submitButton;
    private Button cancelButton;
    private ProgressBar mProgressBar;
    private TextView title;
    TextView bottomSheetTitle;
    TextView bottomSheetText;
    String sheetId;
    String calendarId;
    String summaryPrefix;
    Sheet mSelectedSheet;
    com.brandonlehr.whendidiwork.models.Calendar mSelectedCalendar;
    final Calendar c = Calendar.getInstance();
    final int year = c.get(Calendar.YEAR);
    final int month = c.get(Calendar.MONTH);
    final int day = c.get(Calendar.DAY_OF_MONTH);
    String format = "EEE MMM dd, yyyy";
    String timeFormat = "hh:mm a";
    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    String startTime;
    String endTime;
    String startDate;
    String endDate;
    String note;
    String eventToEditId;
    Event eventToEdit;
    TimeZone mTimeZone;
    boolean isAllDay;
    CreateEventPostBody event;
    private LinearLayout bottomSheet;
    private LinearLayout bottomSheetTitleLL;
    BottomSheetBehavior bottomSheetBehavior;
    UserTimer mUserTimer;
    private SigninTime mSigninTime;
    private ApiCalls client;


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    Retrofit mRetrofitClient;

    @Inject
    GoogleSignInClient mGoogleSignInClient;

    CreateEventViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Event");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ((Whendidiwork) getApplication()).getDIComponent().inject(this);
        model = ViewModelProviders.of(this, viewModelFactory).get(CreateEventViewModel.class);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        model.getSigninTime().observe(this, signinTime -> mSigninTime = signinTime);

        // if null send to login
        if (account == null) {
            Intent signInIntent = new Intent(this, LoginActivity.class);
            startActivity(signInIntent);
            return;
        }

        if (mSigninTime != null && System.currentTimeMillis() - mSigninTime.getTimestamp() > (50 * 60 * 1000)) {
            Log.d(TAG, "onCreate: Less than 50 minutes left on token ");
            attemptSilentLogin();
        }

        Intent initiatingIntent = getIntent();
        if (initiatingIntent.hasExtra("EVENT_ID_TO_EDIT")) {
            eventToEditId = initiatingIntent.getStringExtra("EVENT_ID_TO_EDIT");
        }
        if (Objects.equals(initiatingIntent.getAction(), "CREATE_EVENT_FROM_TIMER")) {
            model.getUserTimer().observe(this, userTimer -> {

                if (userTimer != null) {
                    mUserTimer = userTimer;

                    Log.d(TAG, "onCreate: =================== " + mUserTimer.toString());
                    prepareEventFromTimer(mUserTimer);
                }
            });
        }


        model.getTimeZone().observe(this, timeZone -> mTimeZone = timeZone);
        model.getSelectedCalendar().observe(this, calendar -> {
            if (calendar == null) return;
            mSelectedCalendar = calendar;
            calendarId = mSelectedCalendar.getId();
        });
        model.getSelectedSheet().observe(this, sheet -> {
            if (sheet == null) return;
            mSelectedSheet = sheet;
            sheetId = mSelectedSheet.getId();
            summaryPrefix = mSelectedSheet.getName().substring(12) + " ";
            if (eventToEditId == null) {
                summaryEditText.setText(summaryPrefix);
                summaryEditText.setSelection(summaryPrefix.length());
            }
        });

        bottomSheetTitle = findViewById(R.id.bottom_sheet_title);
        bottomSheetText = findViewById(R.id.bottom_sheet_text);
        bottomSheetTitleLL = findViewById(R.id.title_LL);

        bottomSheetTitleLL.setOnClickListener(view -> toggleBottomSheet());

        bottomSheetTitle.setText(R.string.help);
        bottomSheetText.setText(R.string.help_text);

        // bottom sheet
        bottomSheet = findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);

        model.getErrorResponse().observe(this, response -> {
            mProgressBar.setVisibility(View.GONE);
            Snackbar errorSnackbar = Snackbar.make(findViewById(R.id.coordinator), "Sorry an error has occurred", Snackbar.LENGTH_LONG);
            View view = errorSnackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.RED);
            tv.setTextSize(18f);
            errorSnackbar.show();
        });

        startDateEditText = findViewById(R.id.start_date_edit_text);
        startTimeEditText = findViewById(R.id.start_time_edit_text);
        endDateEditText = findViewById(R.id.end_date_edit_text);
        endTimeEditText = findViewById(R.id.end_time_edit_text);
        summaryEditText = findViewById(R.id.summary_edit_text);
        submitButton = findViewById(R.id.submit_event);
        cancelButton = findViewById(R.id.cancelButton);
        mProgressBar = findViewById(R.id.progressBar4);
        title = findViewById(R.id.create_event_instructions);

        mProgressBar.setVisibility(View.GONE);

        title.setText(R.string.create_an_event);

        if (eventToEditId != null) {
            title.setText(R.string.update_an_event);
            model.getEventById(eventToEditId).observe(this, event -> {
                eventToEdit = event;
                prepareEventToEdit(eventToEdit);
            });
        }

        startDateEditText.setOnClickListener(view -> handleStartDateClick());
        startTimeEditText.setOnClickListener(view -> handleStartTimeClick());
        endDateEditText.setOnClickListener(view -> handleEndDateClick());
        endTimeEditText.setOnClickListener(view -> handleEndTimeClick());
        submitButton.setOnClickListener(view -> handleSubmit());
        cancelButton.setOnClickListener(view -> cancelCreateEvent());
        if (summaryPrefix != null) {
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

    }

    private DateTime convertToDateTime(String dateString) {
        return new DateTime(dateString);
    }

    private void attemptSilentLogin() {
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        Log.d(TAG, "onComplete: Attempt to silent login ================= ");
                        handleSignInResult(task);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signOutGoToLogin();
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String authCode = account.getServerAuthCode();

            Call<UserResponse> call = client.sendToken(new TokenObject(authCode));
            call.enqueue(CreateEventActivity.this);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode() + ", " + e.getMessage());
        }
    }

    @Override
    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
        if (response.isSuccessful()) {
            model.insertSigninTime(new SigninTime(System.currentTimeMillis()));
        }
    }

    @Override
    public void onFailure(Call<UserResponse> call, Throwable t) {
        signOutGoToLogin();
    }

    private void signOutGoToLogin() {
        mGoogleSignInClient.signOut();
        model.deleteSigninTime();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void prepareEventToEdit(Event event) {
        if (event.getStart().getDateTime() == null) {
            isAllDay = true;
            startDateEditText.setText(convertToDateTime(event.getStart().getDate()).toString(format));
            endDateEditText.setText(convertToDateTime(event.getEnd().getDate()).toString(format));
        } else {
            startDateEditText.setText(convertToDateTime(event.getStart().getDateTime()).toString(format));
            startTimeEditText.setText(convertToDateTime(event.getStart().getDateTime()).toString(timeFormat));
            endDateEditText.setText(convertToDateTime(event.getEnd().getDateTime()).toString(format));
            endTimeEditText.setText(convertToDateTime(event.getEnd().getDateTime()).toString(timeFormat));
        }
        summaryEditText.setText(event.getSummary());
        summaryEditText.setSelection(event.getSummary().length());
    }

    private void prepareEventFromTimer(UserTimer userTimer) {
        DateTime start = new DateTime(userTimer.getStartTimeStamp());
        DateTime stop = new DateTime(userTimer.getEndTimeStamp());

        startDateEditText.setText(start.toString(format));
        startTimeEditText.setText(start.toString(timeFormat));
        endDateEditText.setText(stop.toString(format));
        endTimeEditText.setText(stop.toString(timeFormat));
    }

    private void handleStartDateClick() {
        hideKeyboard(this);
        if (mUserTimer != null) {
            DateTime dt = new DateTime(mUserTimer.getStartTimeStamp());
            new DatePickerDialog(CreateEventActivity.this, mStartDateListener, dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth()).show();
        } else if (eventToEdit != null) {
            DateTime dt = isAllDay ? convertToDateTime(eventToEdit.getStart().getDate()) : convertToDateTime(eventToEdit.getStart().getDateTime());
            new DatePickerDialog(CreateEventActivity.this, mStartDateListener, dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth()).show();
        } else {
            new DatePickerDialog(CreateEventActivity.this, mStartDateListener, year, month, day).show();
        }
    }

    private DatePickerDialog.OnDateSetListener mStartDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            c.set(Calendar.YEAR, i);
            c.set(Calendar.MONTH, i1);
            c.set(Calendar.DAY_OF_MONTH, i2);
            DateTime dateTime = new DateTime(c);
            startDate = fmt.print(dateTime);
            startDateEditText.setText(dateTime.toString(format));
        }
    };

    private void handleEndDateClick() {
        hideKeyboard(this);
        if (mUserTimer != null) {
            DateTime dt = new DateTime(mUserTimer.getEndTimeStamp());
            new DatePickerDialog(CreateEventActivity.this, mEndDateListener, dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth()).show();
        } else if (eventToEdit != null) {
            DateTime dt = isAllDay ? convertToDateTime(eventToEdit.getEnd().getDate()) : convertToDateTime(eventToEdit.getEnd().getDateTime());
            new DatePickerDialog(CreateEventActivity.this, mEndDateListener, dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth()).show();
        } else {
            new DatePickerDialog(CreateEventActivity.this, mEndDateListener, year, month, day).show();
        }
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
        if (mUserTimer != null) {
            DateTime dt = new DateTime(mUserTimer.getStartTimeStamp());
            new TimePickerDialog(CreateEventActivity.this, mStartTimeListener, dt.getHourOfDay(), dt.getMinuteOfHour(), false).show();
        } else if (eventToEdit != null && !isAllDay) {
            DateTime dt = convertToDateTime(eventToEdit.getStart().getDateTime());
            new TimePickerDialog(CreateEventActivity.this, mStartTimeListener, dt.getHourOfDay(), dt.getMinuteOfHour(), false).show();
        } else {
            new TimePickerDialog(CreateEventActivity.this, mStartTimeListener, hour, minute, false).show();
        }
    }

    private TimePickerDialog.OnTimeSetListener mStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            c.set(Calendar.HOUR, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            DateTime dateTime = new DateTime(c);
            startTime = fmt.print(dateTime);
            String time = convertTo12HourFormat(hourOfDay, minute);

            Log.d(TAG, "onTimeSet: START hour: " + hourOfDay + " minute: " + minute);
            startTimeEditText.setText(time);
        }
    };

    private void handleEndTimeClick() {
        hideKeyboard(this);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        if (mUserTimer != null) {
            DateTime dt = new DateTime(mUserTimer.getEndTimeStamp());
            new TimePickerDialog(CreateEventActivity.this, mEndTimeListener, dt.getHourOfDay(), dt.getMinuteOfHour(), false).show();
        } else if (eventToEdit != null && !isAllDay) {
            DateTime dt = convertToDateTime(eventToEdit.getEnd().getDateTime());
            new TimePickerDialog(CreateEventActivity.this, mEndTimeListener, dt.getHourOfDay(), dt.getMinuteOfHour(), false).show();
        } else {
            new TimePickerDialog(CreateEventActivity.this, mEndTimeListener, hour, minute, false).show();
        }
    }

    private TimePickerDialog.OnTimeSetListener mEndTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            c.set(Calendar.HOUR, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            DateTime dateTime = new DateTime(c);
            endTime = fmt.print(dateTime);
            String time = convertTo12HourFormat(hourOfDay, minute);

            Log.d(TAG, "onTimeSet: END hour: " + hourOfDay + " minute: " + minute);
            endTimeEditText.setText(time);
        }
    };

    private String getNote() {
        return summaryEditText.getText().toString();
    }

    private void handleSubmit() {
        mProgressBar.setVisibility(View.VISIBLE);
        hideKeyboard(this);

        note = getNote();
        if (mUserTimer != null) {
            if (endDate == null) {
                endDate = new DateTime(mUserTimer.getEndTimeStamp()).toDateTime().toString();
            }
            if (startDate == null) {
                startDate = new DateTime(mUserTimer.getStartTimeStamp()).toDateTime().toString();
            }
            if (endTime == null) {
                endTime = new DateTime(mUserTimer.getEndTimeStamp()).toDateTime().toString();
            }
            if (startTime == null) {
                startTime = new DateTime(mUserTimer.getStartTimeStamp()).toDateTime().toString();
            }
            event = new CreateEventPostBody(endDate, startDate, endTime, startTime, mTimeZone.getTimeZone(), note);
        } else if (eventToEdit != null) {
            if (endDate == null) {
                endDate = eventToEdit.getEnd().getDateTime();
            }
            if (startDate == null) {
                startDate = eventToEdit.getStart().getDateTime();
            }
            if (endTime == null) {
                endTime = eventToEdit.getEnd().getDateTime();
            }
            if (startTime == null) {
                startTime = eventToEdit.getStart().getDateTime();
            }

            event = new CreateEventPostBody(endDate, startDate, endTime, startTime, mTimeZone.getTimeZone(), note);
        } else {
            event = new CreateEventPostBody(endDate, startDate, endTime, startTime, mTimeZone.getTimeZone(), note);
        }


        if (startDate == null || endDate == null || startTime == null || endTime == null)

        {
            Log.d(TAG, "handleSubmit: ALL is NULL =====================");
            Snackbar errorSnackbar = Snackbar.make(findViewById(R.id.coordinator), "Are all Dates and Times Set?", Snackbar.LENGTH_INDEFINITE);
//            View view = errorSnackbar.getView();
//            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            errorSnackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorSnackbar.dismiss();
                }
            });
            mProgressBar.setVisibility(View.GONE);
            errorSnackbar.show();
            return;
        }

        HashMap<String, CreateEventPostBody> postBody = new HashMap<>();
        postBody.put("event", event);

        if (eventToEdit != null)

        {
            model.updateEvent(calendarId, eventToEdit.getId(), postBody).observe(this, returnEvent -> {
                resetAndStartActivity();
            });
        } else

        {
            if (sheetId == null) {
                Snackbar errorSnackbar = Snackbar.make(findViewById(R.id.coordinator), "Must have a Sheet selected to create an event", Snackbar.LENGTH_INDEFINITE);
//                View view = errorSnackbar.getView();
//                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//                tv.setTextColor(Color.RED);
                errorSnackbar.setAction("Select Sheet", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goToHomeIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                        startActivity(goToHomeIntent);
                    }
                });
                mProgressBar.setVisibility(View.GONE);
                errorSnackbar.show();
                return;
            }
            if (calendarId == null) {
                Snackbar errorSnackbar = Snackbar.make(findViewById(R.id.coordinator), "Must have a Calendar selected to create an event", Snackbar.LENGTH_INDEFINITE);
//                View view = errorSnackbar.getView();
//                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//                tv.setTextColor(Color.RED);
                errorSnackbar.setAction("Select Calendar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goToHomeIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                        startActivity(goToHomeIntent);
                    }
                });
                mProgressBar.setVisibility(View.GONE);
                errorSnackbar.show();
                return;
            }
            model.makeEvent(calendarId, sheetId, postBody).observe(this, returnEvent -> {
                if (mUserTimer != null) {
                    model.deleteUserTimer();
                }
                resetAndStartActivity();
            });
        }

    }

    public void cancelCreateEvent() {
        resetAndStartActivity();
    }

    public void resetAndStartActivity() {
        mProgressBar.setVisibility(View.GONE);
        eventToEditId = null;
        eventToEdit = null;
        mUserTimer = null;
        summaryEditText.setText(summaryPrefix);
        summaryEditText.setSelection(summaryPrefix.length());
        startDateEditText.setText("");
        startTimeEditText.setText("");
        endDateEditText.setText("");
        endTimeEditText.setText("");


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    public void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
