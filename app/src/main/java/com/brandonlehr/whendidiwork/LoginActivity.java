package com.brandonlehr.whendidiwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.brandonlehr.whendidiwork.models.SigninTime;
import com.brandonlehr.whendidiwork.models.TokenObject;
import com.brandonlehr.whendidiwork.models.UserResponse;
import com.brandonlehr.whendidiwork.repository.UserRepository;
import com.brandonlehr.whendidiwork.services.ApiCalls;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity implements Callback<UserResponse> {
    private static final String TAG = "LoginActivity";

    public static final int RC_SIGN_IN = 1;
    private ApiCalls client;

    // UI references.
    private View mProgressView;
    private SignInButton signInButton;
    @Inject
    GoogleSignInClient mGoogleSignInClient;
    @Inject
    Retrofit mRetrofitClient;
    @Inject
    UserRepository mUserRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Whendidiwork");
        setSupportActionBar(toolbar);

        mProgressView = findViewById(R.id.login_progress);
        signInButton = findViewById(R.id.sign_in_button);

        hideProgress();

        ((Whendidiwork) getApplication()).getDIComponent().inject(this);
        client = mRetrofitClient.create(ApiCalls.class);

        signInButton.setOnClickListener(v -> loginUser());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginUser() {
        showProgress();
        signIn();
    }

    private void hideProgress() {
        mProgressView.setVisibility(View.GONE);
        signInButton.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        mProgressView.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d(TAG, "onActivityResult: " + task);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "handleSignInResult: " + account);

            String authCode = account.getServerAuthCode();

            Call<UserResponse> call = client.sendToken(new TokenObject(authCode));
            call.enqueue(LoginActivity.this);

            Log.d(TAG, "handleSignInResult: account " + account.getDisplayName());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode() + ", " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Starts");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            showProgress();
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
                            signOut();
                            hideProgress();
                        }
                    });
        }

    }

    @Override
    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
        if (response.isSuccessful()) {
            mUserRepository.insertUser(response.body());
            mUserRepository.insertSigninTime(new SigninTime(System.currentTimeMillis()));

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(Call<UserResponse> call, Throwable t) {
        Log.d(TAG, "onFailure: response " + t.getMessage());
    }

    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.d(TAG, "onComplete: SIGNOUT =========================================");
                        mUserRepository.deleteSigninTime();
                    }
                });
    }

}

