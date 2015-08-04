package com.jasonmoix.popularmovies.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jasonmoix.popularmovies.R;

/**
 * Created by jmoix on 7/20/2015.
 */
public class Utils {

    //Get preferred sorting order from user settings
    public static String getPreferredSortOrder(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return(prefs.getString(context.getString(R.string.pref_order_key), context.getString(R.string.pref_order_popularity)));
    }

}
