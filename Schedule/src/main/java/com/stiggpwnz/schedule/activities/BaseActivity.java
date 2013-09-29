package com.stiggpwnz.schedule.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.stiggpwnz.schedule.MultiThreadedBus;
import com.stiggpwnz.schedule.Persistence;
import com.stiggpwnz.schedule.ScheduleApp;

import javax.inject.Inject;

import butterknife.Views;

/**
 * Created by stiggpwnz on 30.08.13
 */
public abstract class BaseActivity extends SherlockFragmentActivity {

    @Inject protected MultiThreadedBus bus;
    @Inject protected Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScheduleApp.getObjectGraph().inject(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        Views.inject(this);
    }

    @Override
    public void onResume() {
        ScheduleApp.inApp = true;
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
        ScheduleApp.inApp = false;
    }
}
