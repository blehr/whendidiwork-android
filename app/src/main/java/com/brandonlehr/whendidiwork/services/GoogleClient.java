package com.brandonlehr.whendidiwork.services;

import android.content.Context;

import com.brandonlehr.whendidiwork.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

/**
 * Created by blehr on 2/19/2018.
 */

public class GoogleClient {
    private GoogleSignInClient mGoogleSignInClient;


    public GoogleSignInClient getInstance(Context context) {
        if(mGoogleSignInClient != null) {
            return mGoogleSignInClient;
        } else {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                    .requestScopes(new Scope("https://www.googleapis.com/auth/drive"))
                    .requestScopes(new Scope("https://www.googleapis.com/auth/spreadsheets"))
                    .requestServerAuthCode(Constants.CLIENT_ID) // 732491487008-ta8o5q0lf2m1df8ttosesr28m7appu89.apps.googleusercontent.com good
                    .requestEmail()
                    .build();                                                                                   // 992939485110-saio8vio3h5d3lonl32v5fhm4fgudmim.apps.googleusercontent.com

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }
        return mGoogleSignInClient;

    }



    public GoogleClient() {
    }
}
