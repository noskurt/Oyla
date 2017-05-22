package com.ygznsl.noskurt.oyla;

import android.app.Application;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.ygznsl.noskurt.oyla.helper.Consumer;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;

public final class MyApplication extends Application {

    private final OylaDatabase OYLA = new OylaDatabase();

    public static synchronized void setIconBar(AppCompatActivity activity){
        new Nullable<>(activity.getSupportActionBar()).operate(new Consumer<ActionBar>() {
            @Override
            public void accept(ActionBar in) {
                in.setDisplayShowHomeEnabled(true);
                in.setIcon(R.mipmap.ic_launcher);
            }
        });
    }

    public synchronized OylaDatabase oyla() {
        return OYLA;
    }

}
