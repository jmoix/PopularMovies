package com.jasonmoix.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by jmoix on 7/20/2015.
 */
public class MovieAuthenticatorService extends Service {

    private MovieAuthenticator movieAuthenticator;

    @Override
    public void onCreate() {
        movieAuthenticator = new MovieAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return movieAuthenticator.getIBinder();
    }
}
