package com.brandonlehr.whendidiwork.module;

import android.app.Application;

import com.brandonlehr.whendidiwork.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by blehr on 3/7/2018.
 */

@Module(includes = {AppModule.class})
public class GoogleClientModule {

    public GoogleClientModule() {}

    @Singleton
    @Provides
    GoogleSignInClient mGoogleSignInClient(@Named("application_context")Application application) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .requestScopes(new Scope("https://www.googleapis.com/auth/drive"))
                .requestScopes(new Scope("https://www.googleapis.com/auth/spreadsheets"))
                .requestServerAuthCode(Constants.CLIENT_ID) // 732491487008-ta8o5q0lf2m1df8ttosesr28m7appu89.apps.googleusercontent.com good
                .requestEmail()
                .build();                                                                                   // 992939485110-saio8vio3h5d3lonl32v5fhm4fgudmim.apps.googleusercontent.com

        // Build a GoogleSignInClient with the options specified by gso.
        return GoogleSignIn.getClient(application, gso);
    }
}
