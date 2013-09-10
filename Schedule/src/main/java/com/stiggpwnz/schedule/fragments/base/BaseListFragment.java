package com.stiggpwnz.schedule.fragments.base;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.gson.Gson;
import com.stiggpwnz.schedule.DatabaseHelper;
import com.stiggpwnz.schedule.MultiThreadedBus;
import com.stiggpwnz.schedule.Persistence;
import com.stiggpwnz.schedule.ScheduleApp;

import javax.inject.Inject;

import timber.log.Timber;

public class BaseListFragment extends SherlockListFragment {

    @Inject protected MultiThreadedBus bus;
    @Inject protected Timber timber;
    @Inject protected Persistence persistence;
    @Inject protected DatabaseHelper databaseHelper;
    @Inject protected Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApp.getObjectGraph().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}
