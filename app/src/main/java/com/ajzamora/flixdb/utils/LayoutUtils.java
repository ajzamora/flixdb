package com.ajzamora.flixdb.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class LayoutUtils {
    public static int calculateNoOfColumns(Context context, float columnWidthDp) {
        final double MIN_ROUND_DECIMAL_VALUE = 0.5;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + MIN_ROUND_DECIMAL_VALUE);
        return noOfColumns;
    }
}
