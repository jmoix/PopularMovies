package com.jasonmoix.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by jmoix on 7/15/2015.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

}
