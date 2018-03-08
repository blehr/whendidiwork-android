package com.brandonlehr.whendidiwork;

import android.animation.Animator;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.viewModels.MainActivityViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements FirstScreenFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    String calendarId = null;
    String sheetId = null;
    Sheet mSheet;
    LinearLayout fabLayout1, fabLayout2, fabLayout3;
    View fabBGLayout;
    boolean isFABOpen = false;

    @Inject
    GoogleSignInClient mGoogleSignInClient;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    MainActivityViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Whendidiwork");
        setSupportActionBar(toolbar);
        ((Whendidiwork) getApplication()).getGoogleClientComponent().inject(this);
        model = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);


        fab = (FloatingActionButton) findViewById(R.id.fab);

        fabLayout1= (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3= (LinearLayout) findViewById(R.id.fabLayout3);

        fabBGLayout=findViewById(R.id.fabBGLayout);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);


        model.getSheet().observe(this, this::syncSetSheet);
        model.getCalendar().observe(this, calendar -> calendarId = calendar.getId());

        Intent initiatingIntent = getIntent();

        if (initiatingIntent.getExtras() != null) {
            if (initiatingIntent.hasExtra("newCalendarId")) {
                calendarId = initiatingIntent.getExtras().getString("newCalendarId");
            }
            if (initiatingIntent.hasExtra("newSheetId")) {
                sheetId = initiatingIntent.getExtras().getString("newSheetId");
            }

            Log.d(TAG, "onCreate: sheetId==============" + sheetId);
            Log.d(TAG, "onCreate: calendarId============" + calendarId);
        }


        fab.setOnClickListener(view -> {
            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        });

        fabBGLayout.setOnClickListener(view -> closeFABMenu());

        fabLayout1.setOnClickListener(view -> {
            Intent createEventIntent = new Intent(this, CreateEventActivity.class);
//            createEventIntent.putExtra("sheet_name", mSheet.getName());
//            createEventIntent.putExtra("sheet_id", mSheet.getId());
//            createEventIntent.putExtra("calendar_id", calendarId);
            startActivity(createEventIntent);
        });
        fabLayout2.setOnClickListener(view -> {
            Intent createSheetIntent = new Intent(MainActivity.this, CreateSheetActivity.class);
            startActivity(createSheetIntent);
        });
        fabLayout3.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateCalendarActivity.class);
            startActivity(intent);
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment firstScreenFragment = FirstScreenFragment.newInstance(calendarId, sheetId);
        transaction.replace(R.id.replace_me, firstScreenFragment);

        transaction.commit();

    }

    private void syncSetSheet(Sheet sheet) {
        mSheet = sheet;
    }

//    private void showFABMenu(){
//        isFABOpen=true;
//        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
//        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
//        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
//    }
//
//    private void closeFABMenu(){
//        isFABOpen=false;
//        fab1.animate().translationY(0);
//        fab2.animate().translationY(0);
//        fab3.animate().translationY(0);
//    }
//
    @Override
    public void onBackPressed() {
        if(!isFABOpen){
            super.onBackPressed();
        }else{
            closeFABMenu();
        }
    }


    private void showFABMenu(){
        isFABOpen=true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                return true;
            case R.id.setting_sign_out:
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
