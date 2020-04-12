package com.ajzamora.flixdb.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.ajzamora.flixdb.R;

public class FlixPrefences {

    public static String getPreferredAPI(Context context) {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_api_key);
        String defaultLocation = context.getString(R.string.pref_api_default);

        return prefs.getString(keyForLocation, defaultLocation);
    }

    public static boolean isOrderPopular(Context context) {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortOrder = context.getString(R.string.pref_sort_order_key);
        String defaultSortOrder = context.getString(R.string.pref_sort_order_popular);
        String preferredOrder = prefs.getString(keyForSortOrder, defaultSortOrder);
        String sortOrder = "popular";

        return sortOrder.equals(preferredOrder);
    }

}
