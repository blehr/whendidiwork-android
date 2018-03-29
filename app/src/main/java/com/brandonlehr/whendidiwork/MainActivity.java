package com.brandonlehr.whendidiwork;

import android.animation.Animator;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brandonlehr.whendidiwork.models.Calendar;
import com.brandonlehr.whendidiwork.models.Event;
import com.brandonlehr.whendidiwork.models.Sheet;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.viewModels.MainActivityViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity implements
        CalendarListFragment.OnListFragmentInteractionListener,
        SheetListFragment.OnListFragmentInteractionListener,
        EventListFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    private Button calendarSelect;
    private Button sheetSelect;
    TextView fragmentHeading;
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    LinearLayout fabLayout1, fabLayout2;
    View fabBGLayout;
    boolean isFABOpen = false;

    private List<Calendar> mCalendars;
    private List<Sheet> mSheets;
    private List<Event> mEvents;
    private Calendar mCalendar = null;
    private UserResponse mUser;
    private Sheet mSheet = null;
    private String sheetId;
    private String calendarId;
    private AdView mAdView;

    private static final String SHOWCASE_MainActivity_ID = "simple example";

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
        ((Whendidiwork) getApplication()).getDIComponent().inject(this);
        model = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        MobileAds.initialize(this, Constants.ADMOB_ID);

        if (account == null) {
            Intent signInIntent = new Intent(this, LoginActivity.class);
            startActivity(signInIntent);
            return;
        } else {
            Log.d(TAG, "onCreate: account ========= " + account.getDisplayName());

            model.getUser().observe(this, userResponse -> {
                mUser = userResponse;

                CircleImageView profileImage = findViewById(R.id.profileImage);

                if (mUser != null) {
                    Picasso.get().load(mUser.getGoogle().getProfileImg()).resize(125, 125).centerCrop().into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_action_event);
                }
            });

        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);

        fabBGLayout = findViewById(R.id.fabBGLayout);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        sheetSelect = findViewById(R.id.sheetSelect);
        calendarSelect = findViewById(R.id.calendarSelect);
        fragmentHeading = findViewById(R.id.fragment_heading);

        mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        model.InitialDataLoad();

        model.getUser().observe(this, userResponse -> mUser = userResponse);

        model.retrieveSheets().observe(this, sheets -> mSheets = sheets);

        model.retrieveCalendars().observe(this, calendars -> mCalendars = calendars);

        model.retrieveSelectedCalendar().observe(this, calendar -> {
            if (calendar == null) {
                calendarSelect.setText("");
                model.retrieveEvents("");
            } else {
                calendarSelect.setText(calendar.getSummary());
                model.retrieveEvents(calendar.getId());
            }
        });

        model.retrieveSelectedSheet().observe(this, sheet -> {
            if (sheet == null) {
                sheetSelect.setText("");
            } else {
                sheetSelect.setText(sheet.getName());
            }
        });

        calendarSelect.setOnClickListener(view -> {
            showCalendarListFragment();
        });

        sheetSelect.setOnClickListener(view -> {
            if (mSheets.size() > 0) {
                showSheetListFragment();
            } else {
                showEmptySheetListMessage();
            }
        });


        fab.setOnClickListener(view -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        fabBGLayout.setOnClickListener(view -> closeFABMenu());

        fabLayout1.setOnClickListener(view -> {
            closeFABMenu();
            Intent createEventIntent = new Intent(this, CreateEventActivity.class);
            startActivity(createEventIntent);
        });
        fabLayout2.setOnClickListener(view -> {
            closeFABMenu();
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        });

        showEventListFragment();

//        MaterialShowcaseView.resetSingleUse(this, SHOWCASE_MainActivity_ID);

        presentShowcaseSequence();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();

        if (count == 2) {
            Toast.makeText(this, "Pressing back one more time will exit", Toast.LENGTH_LONG).show();
        }

        if (count == 1) {
            if (isFABOpen) {
                closeFABMenu();
            } else {
                finish();
            }
            return;
        } else if (!isFABOpen) {
            super.onBackPressed();
        } else {
            closeFABMenu();
        }
    }

    private boolean isCalendarInList(List<Calendar> calendars, Calendar cal) {
        for (Calendar c : calendars) {
            if (c.getId().equals(cal.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isSheetInList(List<Sheet> sheets, Sheet sheet) {
        for (Sheet s : sheets) {
            if (s.getId().equals(sheet.getId())) {
                return true;
            }
        }
        return false;
    }

    private void showCalendarListFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = CalendarListFragment.newInstance(1);
        transaction.replace(R.id.replace_me, fragment);
        transaction.addToBackStack("DISPLAY_CALENDARS");
        transaction.commit();
        fragmentHeading.setText(R.string.select_a_calendar);
    }

    private void showSheetListFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = SheetListFragment.newInstance(1);
        transaction.replace(R.id.replace_me, fragment);
        transaction.addToBackStack("DISPLAY_SHEETS");
        transaction.commit();
        fragmentHeading.setText(R.string.select_a_sheet);
    }

    private void showEventListFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = EventListFragment.newInstance(1);
        transaction.replace(R.id.replace_me, fragment);
        transaction.addToBackStack("DISPLAY_EVENTS");
        transaction.commit();
        fragmentHeading.setText(R.string.swipe_to_);
    }

    private void showEmptySheetListMessage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new EmptySheetListFragment();
        transaction.replace(R.id.replace_me, fragment);
        transaction.commit();
        fragmentHeading.setText(R.string.select_a_sheet);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
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
        switch (item.getItemId()) {
            case R.id.settings:
//                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                Intent mapIntent = new Intent(this, MapsActivity.class);
                startActivity(mapIntent);
                return true;
            case R.id.setting_sign_out:
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.createCalendar:
                Intent createCalendarIntent = new Intent(this, CreateCalendarActivity.class);
                startActivity(createCalendarIntent);
                return true;
            case R.id.createSheet:
                Intent createSheetIntent = new Intent(this, CreateSheetActivity.class);
                startActivity(createSheetIntent);
                return true;
            case R.id.faq_menu_item:
                Intent faqIntent = new Intent(this, FaqActivity.class);
                startActivity(faqIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000); //  second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_MainActivity_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(fab, "Click to Create Events and Timers", "GOT IT");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(calendarSelect)
                        .withRectangleShape()
                        .setContentText("Click to choose a Calendar")
                        .setDismissText("GOT IT")
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(sheetSelect)
                        .withRectangleShape()
                        .setDismissText("GOT IT")
                        .setContentText("Click to choose a Sheet")
                        .build()
        );
        sequence.start();
    }

    @Override
    public void onListFragmentInteraction(Calendar item) {
        model.updateSelectedCalendar(item);
        showEventListFragment();
    }

    @Override
    public void onListFragmentInteraction(Sheet item) {
        model.updateSelectedSheet(item);
        showEventListFragment();
    }

    @Override
    public void onListFragmentInteraction(Event item) {

    }
}
