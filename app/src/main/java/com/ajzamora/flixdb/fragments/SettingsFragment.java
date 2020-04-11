package com.ajzamora.flixdb.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.ajzamora.flixdb.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);
    }
}
