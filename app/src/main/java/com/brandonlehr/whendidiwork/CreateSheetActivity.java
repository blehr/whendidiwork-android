package com.brandonlehr.whendidiwork;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brandonlehr.whendidiwork.models.CreateSheetPostBody;
import com.brandonlehr.whendidiwork.viewModels.CreateSheetViewModel;

import javax.inject.Inject;

public class CreateSheetActivity extends AppCompatActivity {
    private static final String TAG = "CreateSheetActivity";

    EditText createSheetEditText;
    TextView sheetResponse;
    TextView revealSheetName;
    Button createSheetButton;
    CreateSheetViewModel model;

    @Inject
//    @Named("createSheet_viewModelFactory")
    ViewModelProvider.Factory mViewModelFactory;

    String sheetNameConstant = "whendidiwork@";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sheet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Sheet");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((Whendidiwork) getApplication()).getGoogleClientComponent().inject(this);

//        model = ViewModelProviders.of(this).get(CreateSheetViewModel.class);
        model = ViewModelProviders.of(this, mViewModelFactory).get(CreateSheetViewModel.class);

        createSheetEditText = findViewById(R.id.create_sheet_edit_text);
        createSheetButton = findViewById(R.id.create_sheet_button);
        sheetResponse = findViewById(R.id.sheet_response);
        revealSheetName = findViewById(R.id.reveal_sheet_name);

        createSheetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                revealSheetName.setText(String.format("%s%s", sheetNameConstant, s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createSheetButton.setOnClickListener(view -> handleSheetSubmit());
    }

    private void handleSheetSubmit() {
        String editTextValue = createSheetEditText.getText().toString();
        if (editTextValue.length() != 0) {
            String sheetName = sheetNameConstant + editTextValue;

            CreateSheetPostBody sheetPostBody = new CreateSheetPostBody(sheetName);

            model.createSheet(sheetPostBody).observe(this, sheet -> {
                Log.d(TAG, "handleSheetSubmit: observe sheet fired =====================================================");
                sheetResponse.setText(sheet.toString());

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("newSheetId", sheet.getId());
                startActivity(intent);
            });
        }

    }
}
