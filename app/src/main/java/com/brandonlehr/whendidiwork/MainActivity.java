package com.brandonlehr.whendidiwork;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brandonlehr.whendidiwork.services.GoogleClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class MainActivity extends AppCompatActivity implements FirstScreenFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    LinearLayout fabLayout1, fabLayout2, fabLayout3;
    View fabBGLayout;
    boolean isFABOpen = false;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Whendidiwork");
        setSupportActionBar(toolbar);

        mGoogleSignInClient = new GoogleClient().getInstance(getApplicationContext());

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fabLayout1= (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) findViewById(R.id.fabLayout2);
        fabLayout3= (LinearLayout) findViewById(R.id.fabLayout3);

        fabBGLayout=findViewById(R.id.fabBGLayout);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        fabLayout1.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "fab 1", Toast.LENGTH_SHORT).show();
        });
        fabLayout2.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "fab 2", Toast.LENGTH_SHORT).show();
        });
        fabLayout3.setOnClickListener(view -> {
//            Toast.makeText(getApplicationContext(), "fab 3", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CreateCalendarActivity.class);
            startActivity(intent);
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.replace_me, FirstScreenFragment.newInstance());
        transaction.commit();

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
