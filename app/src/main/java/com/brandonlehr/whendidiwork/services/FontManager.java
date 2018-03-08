package com.brandonlehr.whendidiwork.services;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by blehr on 3/6/2018.
 */

public class FontManager {
    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
