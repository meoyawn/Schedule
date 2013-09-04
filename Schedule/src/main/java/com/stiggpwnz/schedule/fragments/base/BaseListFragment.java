package com.stiggpwnz.schedule.fragments.base;

import android.os.Bundle;
import android.os.Looper;

import com.actionbarsherlock.app.SherlockListFragment;
import com.stiggpwnz.schedule.MultiThreadedBus;
import com.stiggpwnz.schedule.ScheduleApp;

import javax.inject.Inject;

public class BaseListFragment extends SherlockListFragment implements FragmentInterface {

    @Inject
    protected MultiThreadedBus bus;

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

    @Override
    public void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    @Override
    public void runOnBackgroundThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }
}
