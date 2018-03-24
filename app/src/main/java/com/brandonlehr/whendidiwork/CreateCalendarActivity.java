package com.brandonlehr.whendidiwork;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brandonlehr.whendidiwork.models.CreateCalendarPostBody;
import com.brandonlehr.whendidiwork.models.TimeZone;
import com.brandonlehr.whendidiwork.viewModels.CreateCalendarViewModel;

import javax.inject.Inject;

public class CreateCalendarActivity extends AppCompatActivity {
    private static final String TAG = "CreateCalendarActivity";

    EditText createCalendarEditText;
    TextView calendarResponse;
    Button createCalendarButton;
    String sheetNameConstant = "whendidiwork@";
    TextView revealName;
    TimeZone mTimeZone;
    boolean mIsLoading = false;
    ProgressBar mProgressBar;


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
        ((Whendidiwork) getApplication()).getDIComponent().inject(this);

        model = ViewModelProviders.of(this, viewModelFactory).get(CreateCalendarViewModel.class);

        createCalendarEditText = findViewById(R.id.create_calendar_edit_text);
        createCalendarButton = findViewById(R.id.create_calendar_button);
        calendarResponse = findViewById(R.id.calendar_reponse);
        revealName = findViewById(R.id.reveal_name);
        mProgressBar = findViewById(R.id.progressBar2);


        model.getTimeZone().observe(this, timeZone -> mTimeZone = timeZone);
        model.getIsLoading().observe(this, isLoading -> {
            mIsLoading = isLoading;
            if (mIsLoading) {
                mProgressBar.setVisibility(View.VISIBLE);
                createCalendarButton.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                createCalendarButton.setVisibility(View.VISIBLE);
            }
        });

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
        String editTextValue = createCalendarEditText.getText().toString();
        if (editTextValue.length() != 0) {
            String calendarName = sheetNameConstant + editTextValue;
            CreateCalendarPostBody calendarPostBody = new CreateCalendarPostBody(calendarName, mTimeZone.getTimeZone());

            model.createCalendar(calendarPostBody);

            Intent intent = new Intent(CreateCalendarActivity.this, MainActivity.class);
            startActivity(intent);
        }


    }
}
