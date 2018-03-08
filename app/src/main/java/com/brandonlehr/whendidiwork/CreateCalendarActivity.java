package com.brandonlehr.whendidiwork;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brandonlehr.whendidiwork.models.CreateCalendarPostBody;
import com.brandonlehr.whendidiwork.viewModels.CreateCalendarViewModel;

import javax.inject.Inject;

public class CreateCalendarActivity extends AppCompatActivity {
    private static final String TAG = "CreateCalendarActivity";

    EditText createCalendarEditText;
    TextView calendarResponse;
    Button createCalendarButton;
    String sheetNameConstant = "whendidiwork@";
    TextView revealName;


    @Inject
    ViewModelProvider.Factory viewModelFactory;
    CreateCalendarViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Calendar");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((Whendidiwork) getApplication()).getGoogleClientComponent().inject(this);

        model = ViewModelProviders.of(this, viewModelFactory).get(CreateCalendarViewModel.class);

        createCalendarEditText = findViewById(R.id.create_calendar_edit_text);
        createCalendarButton = findViewById(R.id.create_calendar_button);
        calendarResponse = findViewById(R.id.calendar_reponse);
        revealName = findViewById(R.id.reveal_name);

        createCalendarButton.setOnClickListener(view -> handleCalendarSubmit());
        createCalendarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                revealName.setText(String.format("%s%s", sheetNameConstant, s));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void handleCalendarSubmit() {
        String calendarName = createCalendarEditText.getText().toString();
        if (calendarName.length() != 0) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storedPrefs), Context.MODE_PRIVATE);
            String userTimeZone = sharedPreferences.getString("timeZone", "");
            Log.d(TAG, "handleCalendarSubmit: =================" + userTimeZone);

            CreateCalendarPostBody calendarPostBody = new CreateCalendarPostBody(calendarName, userTimeZone);

            model.createCalendar(calendarPostBody).observe(this, calendar -> {
                calendarResponse.setText(calendar.toString());
                Intent intent  = new Intent(CreateCalendarActivity.this, MainActivity.class);
                intent.putExtra("newCalendarId", calendar.getId());
                startActivity(intent);
            });
        }


    }
}
