package com.ajzamora.flixdb.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.ajzamora.flixdb.R;

public class FlixPreferences {

    public static String getPreferredAPI(Context context) {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_api_key);
        String defaultLocation = context.getString(R.string.pref_api_default);

        return prefs.getString(keyForLocation, defaultLocation);
    }

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortOrder = context.getString(R.string.pref_sort_order_key);
        String defaultSortOrder = context.getString(R.string.pref_sort_order_popular);

        return prefs.getString(keyForSortOrder, defaultSortOrder);
    }

    public static boolean isSortPopular(Context context) {
        return getPreferredSortOrder(context).equals(context.getString(R.string.pref_sort_order_popular));
    }

}
