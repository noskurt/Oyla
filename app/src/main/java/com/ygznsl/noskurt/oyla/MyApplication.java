package com.ygznsl.noskurt.oyla;

import android.app.Application;

import com.ygznsl.noskurt.oyla.helper.OylaDatabase;

public final class MyApplication extends Application {

    private final OylaDatabase OYLA = new OylaDatabase();

    public synchronized OylaDatabase oyla() {
        return OYLA;
    }

}
