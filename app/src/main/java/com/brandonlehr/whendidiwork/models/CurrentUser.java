package com.brandonlehr.whendidiwork.models;

/**
 * Created by blehr on 2/19/2018.
 */

public class CurrentUser {
    private static UserResponse mUserResponse;

    public static UserResponse getUserResponse() {
        return mUserResponse;
    }

    public static void setUserResponse(UserResponse userResponse) {
        mUserResponse = userResponse;
    }
}
