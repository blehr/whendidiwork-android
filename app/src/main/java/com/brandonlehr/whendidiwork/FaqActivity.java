package com.brandonlehr.whendidiwork;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

public class FaqActivity extends AppCompatActivity {

    TextView twitter;
    TextView email;
    Button revokePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("The FAQ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        twitter = findViewById(R.id.twitter_contact);
        revokePermissions = findViewById(R.id.google_permissions_button);
        email = findViewById(R.id.email_contact);

        email.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:blehr.mail@gmail.com"));
            startActivity(intent);
        });

        revokePermissions.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://security.google.com/settings/security/permissions"));
            startActivity(intent);
        });

        twitter.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/intent/tweet?screen_name=brandonlehr"));
            startActivity(intent);
        });


    }
}
