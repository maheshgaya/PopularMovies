package com.maheshgaya.android.popularmovies.syncdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.maheshgaya.android.popularmovies.R;

/**
 * Created by Mahesh Gaya on 10/18/16.
 */

public class Utility {
    private static  final String TAG = Utility.class.getSimpleName();

    public static String getSortPreference(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }


}
