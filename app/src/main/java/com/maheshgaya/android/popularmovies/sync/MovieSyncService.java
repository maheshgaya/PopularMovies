package com.maheshgaya.android.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Mahesh Gaya on 10/22/16.
 */

public class MovieSyncService extends Service {
    private static final String TAG = MovieSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter sMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: executed");
        synchronized (sSyncAdapterLock) {
            if (sMovieSyncAdapter == null) {
                sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
